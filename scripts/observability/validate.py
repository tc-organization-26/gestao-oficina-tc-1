"""Provoke a PostgreSQL integration failure in an isolated hom pod.

Credentials come from environment variables, and are never written to evidence.
"""
import argparse
import json
import os
from pathlib import Path
import secrets
import socket
import subprocess
import time
import urllib.error
import urllib.parse
import urllib.request
import uuid


def progress(message):
    print(f"[{time.strftime('%H:%M:%S')}] {message}", flush=True)


def http(url, method="GET", body=None, headers=None):
    data = None if body is None else json.dumps(body).encode()
    request = urllib.request.Request(url, data=data, method=method,
                                     headers={"Content-Type": "application/json", **(headers or {})})
    try:
        with urllib.request.urlopen(request, timeout=30) as response:
            raw = response.read()
            return response.status, dict(response.headers), json.loads(raw) if raw else {}
    except urllib.error.HTTPError as failure:
        # Don't print response bodies: authentication errors may contain private data.
        return failure.code, dict(failure.headers), {}


def kube(*args, manifest=None, timeout=90):
    result = subprocess.run(["kubectl", "--request-timeout=30s", *args],
                            input=None if manifest is None else json.dumps(manifest),
                            capture_output=True, text=True, encoding="utf-8", timeout=timeout)
    if result.returncode:
        raise RuntimeError("kubectl falhou: " + " ".join(args[:4]))
    return result.stdout


class Validation:
    def __init__(self, url, output):
        parsed = urllib.parse.urlparse(url)
        if parsed.scheme != "https" or not parsed.netloc or parsed.username or parsed.password:
            raise ValueError("Informe a URL HTTPS publica de homologacao, sem credenciais.")
        self.url = url.rstrip("/")
        self.output = Path(output)
        self.output.mkdir(parents=True, exist_ok=True)
        self.run_id = "obs-" + uuid.uuid4().hex[:16]
        self.token = os.getenv("OFICINA_TOKEN")
        self.steps = []
        self.report = {"environment": "hom", "run_id": self.run_id, "started": int(time.time()),
                       "steps": self.steps, "notification_receipt": "confirmar no email do contato"}

    def save(self):
        (self.output / "evidence.json").write_text(json.dumps(self.report, indent=2), encoding="utf-8")

    def call(self, method, path, body=None, expected=200, base=None):
        request_id = f"{self.run_id}-{len(self.steps) + 1}"
        headers = {"X-Request-ID": request_id,
                   "x-datadog-trace-id": str(secrets.randbits(63) or 1),
                   "x-datadog-parent-id": str(secrets.randbits(63) or 1),
                   "x-datadog-sampling-priority": "2"}
        if self.token:
            headers["Authorization"] = "Bearer " + self.token
        status, response_headers, result = http((base or self.url) + path, method, body, headers)
        returned_id = next((value for key, value in response_headers.items() if key.lower() == "x-request-id"), None)
        self.steps.append({"method": method, "path": path, "status": status,
                           "request_id": request_id, "request_id_returned": returned_id})
        self.save()
        if expected is not None and status != expected:
            raise RuntimeError(f"{method} {path}: esperado HTTP {expected}, recebido {status}")
        return result

    def validate_target(self):
        deployment = json.loads(kube("-n", "oficina", "get", "deployment", "oficina-api", "-o", "json"))
        if deployment["spec"]["template"]["metadata"].get("labels", {}).get("tags.datadoghq.com/env") != "hom":
            raise RuntimeError("O contexto Kubernetes deve apontar para homologacao")
        region = os.getenv("AWS_REGION", "us-east-1")
        discovery = subprocess.run(["aws", "ssm", "get-parameter", "--name", "/oficina/hom/edge/http-api-id",
                                    "--region", region, "--query", "Parameter.Value", "--output", "text"],
                                   capture_output=True, text=True, timeout=30)
        if discovery.returncode or self.url != f"https://{discovery.stdout.strip()}.execute-api.{region}.amazonaws.com":
            raise RuntimeError("A URL deve corresponder ao API Gateway de hom publicado no SSM")

    def authenticate(self):
        if not self.token:
            self.token = self.call("POST", "/auth/login", {
                "cpf": os.getenv("OFICINA_TEST_CPF", "00361937040")
            }).get("token")
            if not self.token:
                raise RuntimeError("Login de preparacao nao retornou token")

    def failure(self):
        progress("Testando falha de banco em pod temporario; pode levar alguns minutos...")
        deployment = json.loads(kube("-n", "oficina", "get", "deployment", "oficina-api", "-o", "json"))
        template = deployment["spec"]["template"]
        labels = template["metadata"].get("labels", {})
        if labels.get("tags.datadoghq.com/env") != "hom":
            raise RuntimeError("O Deployment deve estar identificado com env:hom.")
        # This independent pod has no app=oficina-api label, so the Service never routes to it.
        pod_name = self.run_id + "-fault"
        spec = template["spec"]
        container = next(c for c in spec["containers"] if c["name"] == "oficina-api")
        overrides = {
            "SPRING_DATASOURCE_URL": "jdbc:postgresql://127.0.0.1:1/observability?connectTimeout=1",
            "SPRING_FLYWAY_ENABLED": "false", "SPRING_JPA_HIBERNATE_DDL_AUTO": "none",
            "SPRING_JPA_DATABASE_PLATFORM": "org.hibernate.dialect.PostgreSQLDialect",
            # Preserve exact property names; underscores in environment variables
            # would bind connection.timeout as the Hikari connection property.
            "SPRING_APPLICATION_JSON": json.dumps({
                "spring.jpa.properties.hibernate.boot.allow_jdbc_metadata_access": False,
                "spring.datasource.hikari.initialization-fail-timeout": -1,
                "spring.datasource.hikari.connection-timeout": 1000,
                "spring.datasource.hikari.minimum-idle": 0,
            }),
        }
        container["env"] = [e for e in container.get("env", []) if e["name"] not in overrides]
        container["env"] += [{"name": k, "value": v} for k, v in overrides.items()]
        for key in ("livenessProbe", "readinessProbe", "startupProbe"):
            container.pop(key, None)
        # The database is intentionally unavailable. Wait for HTTP startup using
        # liveness, otherwise Kubernetes marks the container Ready too early.
        container["readinessProbe"] = {
            "httpGet": {"path": "/actuator/health/liveness", "port": 8081},
            "periodSeconds": 2, "timeoutSeconds": 2,
        }
        spec["containers"] = [container]
        spec["restartPolicy"] = "Never"
        spec["activeDeadlineSeconds"] = 600
        spec.pop("initContainers", None)  # Let SSI inject fresh init containers for this pod.
        pod = {"apiVersion": "v1", "kind": "Pod", "metadata": {"name": pod_name, "namespace": "oficina",
               "labels": {k: v for k, v in labels.items() if k.startswith("tags.datadoghq.com/")},
               "annotations": {k: v for k, v in template["metadata"].get("annotations", {}).items()
                               if not k.endswith(".checks")}}, "spec": spec}
        forward = None
        forward_log = None
        try:
            kube("create", "-f", "-", manifest=pod)
            kube("-n", "oficina", "wait", "--for=condition=Ready", "pod/" + pod_name,
                 "--timeout=120s", timeout=150)
            with socket.socket() as port:
                port.bind(("127.0.0.1", 0))
                local_port = port.getsockname()[1]
            forward_log = (self.output / "port-forward.log").open("w", encoding="utf-8")
            forward = subprocess.Popen(["kubectl", "-n", "oficina", "port-forward", "pod/" + pod_name,
                                        f"{local_port}:8081", "--address=127.0.0.1"],
                                       stdout=forward_log, stderr=forward_log)
            base = f"http://127.0.0.1:{local_port}"
            ready = False
            for _ in range(60):
                if forward.poll() is not None:
                    raise RuntimeError("Port-forward terminou antes da chamada; consulte " + str(self.output / "port-forward.log"))
                try:
                    if http(base + "/actuator/health/liveness")[0] == 200:
                        ready = True
                        break
                except (OSError, urllib.error.URLError):
                    pass
                time.sleep(2)
            if not ready:
                raise RuntimeError("Pod de teste nao iniciou em 120 segundos")
            # An OS lookup emits both the integration and OS failure events without
            # creating an order. Confirm the actual failure in logs: Spring's error
            # dispatch can return 403 instead of 500 for an unhandled exception.
            self.call("GET", "/ordens-servico/" + str(uuid.uuid4()), expected=None, base=base)
            self.report["fault_http_status"] = self.steps[-1]["status"]
            time.sleep(5)
            logs = kube("-n", "oficina", "logs", pod_name, "-c", "oficina-api")
            events = []
            for line in logs.splitlines():
                try:
                    entry = json.loads(line)
                    if str(entry.get("request_id", "")).startswith(self.run_id):
                        events.append({k: entry.get(k) for k in ("event", "request_id", "integracao", "dd.trace_id", "dd.span_id")})
                except ValueError:
                    pass
            self.report["fault_events"] = events
            self.check_logs()
            progress(f"Falhas de PostgreSQL e processamento de OS confirmadas (HTTP {self.report['fault_http_status']}).")
            # Allow Agent tailing before deleting the pod. Remote ingestion is verified separately.
            time.sleep(15)
        finally:
            if forward:
                if forward.poll() is None:
                    forward.terminate()
                forward.wait(timeout=10)
            if forward_log:
                forward_log.close()
            kube("-n", "oficina", "delete", "pod", pod_name, "--ignore-not-found", "--wait=false")
            self.save()
            progress("Remocao do pod temporario solicitada.")

    def check_logs(self):
        events = self.report.get("fault_events", [])
        if not any(e.get("event") == "integration.failed" and e.get("integracao") == "postgresql" for e in events):
            raise RuntimeError("Nao foi encontrado integration.failed de PostgreSQL; o teste nao foi confirmado")
        if not any(e.get("event") == "os.processing_failed" for e in events):
            raise RuntimeError("Nao foi encontrado os.processing_failed; o teste nao foi confirmado")
        self.report["datadog_query"] = "service:oficina-api env:hom @request_id:" + self.run_id + "* (@event:integration.failed OR @event:os.processing_failed)"
        self.report["datadog_validation"] = "Confirmar os dois graficos e os alertas de integracao e OS no Datadog"
        self.save()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("mode", choices=["run", "verify"])
    parser.add_argument("--url", required=True, help="URL publica de HOMOLOGACAO")
    parser.add_argument("--output", default="target/observability-evidence")
    parser.add_argument("--fault", action="store_true", help="Opcional por compatibilidade; run sempre provoca a falha de banco")
    args = parser.parse_args()
    validation = Validation(args.url, args.output)
    try:
        if args.mode == "verify":
            validation.report = json.loads((validation.output / "evidence.json").read_text(encoding="utf-8"))
            validation.run_id = validation.report["run_id"]
            progress(f"Conferindo novamente os logs da execucao {validation.run_id}.")
        else:
            progress(f"Iniciando validacao em homologacao: {validation.run_id}")
            validation.validate_target()
            validation.authenticate()
            validation.failure()
        validation.check_logs()
        validation.report.pop("error", None)
        validation.save()
        progress("Teste de falha de banco concluido.")
        progress("Evidencias: " + str(validation.output / "evidence.json"))
        progress("Busca no Datadog: " + validation.report["datadog_query"])
    except (RuntimeError, OSError, KeyError, ValueError) as failure:
        validation.report["error"] = type(failure).__name__ + ": " + str(failure)
        validation.save()
        progress("ERRO: " + str(failure))
        progress("Evidencias parciais: " + str(validation.output / "evidence.json"))
        raise SystemExit(str(failure)) from None


if __name__ == "__main__":
    main()
