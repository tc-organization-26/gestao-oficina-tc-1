"""Run real observability checks in hom; never changes the running Deployment.

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


def kube(*args, manifest=None):
    result = subprocess.run(["kubectl", "--request-timeout=30s", *args],
                            input=None if manifest is None else json.dumps(manifest),
                            capture_output=True, text=True, encoding="utf-8", timeout=90)
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
        if status != expected:
            raise RuntimeError(f"{method} {path}: esperado HTTP {expected}, recebido {status}")
        if not path.startswith("/actuator/") and returned_id != request_id:
            raise RuntimeError("X-Request-ID nao foi preservado; confirme o deploy da instrumentacao.")
        return result

    def traffic(self):
        deployment = json.loads(kube("-n", "oficina", "get", "deployment", "oficina-api", "-o", "json"))
        if deployment["spec"]["template"]["metadata"].get("labels", {}).get("tags.datadoghq.com/env") != "hom":
            raise RuntimeError("O contexto Kubernetes deve apontar para homologacao")
        region = os.getenv("AWS_REGION", "us-east-1")
        discovery = subprocess.run(["aws", "ssm", "get-parameter", "--name", "/oficina/hom/edge/http-api-id",
                                    "--region", region, "--query", "Parameter.Value", "--output", "text"],
                                   capture_output=True, text=True, timeout=30)
        if discovery.returncode or self.url != f"https://{discovery.stdout.strip()}.execute-api.{region}.amazonaws.com":
            raise RuntimeError("A URL deve corresponder ao API Gateway de hom publicado no SSM")
        if self.call("GET", "/actuator/health/readiness").get("status") != "UP":
            raise RuntimeError("Readiness nao retornou UP")
        if not self.token:
            # CPF ficticio do seed do projeto. Never save JWT or login response.
            self.token = self.call("POST", "/auth/login", {"cpf": os.getenv("OFICINA_TEST_CPF", "00361937040")}).get("token")
            if not self.token:
                raise RuntimeError("Login sem token")
        # Reuse the seed client, and create only a vehicle and one test OS.
        client_id = os.getenv("OFICINA_TEST_CLIENT_ID", "00000000-0000-0000-0000-000000000001")
        plate = "".join(secrets.choice("ABCDEFGHIJKLMNOPQRSTUVWXYZ") for _ in range(3)) + str(secrets.randbelow(10000)).zfill(4)
        vehicle = self.call("POST", "/veiculos", {"clienteId": client_id, "placa": plate,
                            "marca": "Teste", "modelo": self.run_id, "ano": 2026}, expected=201)
        self.report["vehicle_id"] = vehicle["id"]
        self.save()
        order = self.call("POST", "/ordens-servico", {"clienteId": client_id,
                          "veiculoId": vehicle["id"], "servicos": [], "pecas": [],
                          "anotacoes": "Teste de observabilidade " + self.run_id}, expected=201)
        self.report["order_id"] = order["id"]
        self.save()
        path = "/ordens-servico/" + order["id"]
        for status in ("EM_DIAGNOSTICO", "AGUARDANDO_APROVACAO"):
            self.call("PATCH", path + "/status", {"status": status})
            time.sleep(2)
        self.call("POST", path + "/orcamento/notificacoes-aprovacao",
                  {"decisao": "APROVADO", "origem": "observabilidade", "protocoloExterno": self.run_id})
        for status in ("EM_EXECUCAO", "FINALIZADA"):
            self.call("PATCH", path + "/status", {"status": status})
            time.sleep(2)
        self.call("POST", path + "/pagamento", {})
        self.call("PATCH", path + "/status", {"status": "ENTREGUE"})
        self.report["business_traffic"] = "concluido; conferir eventos no Datadog"
        self.save()

    def failure(self):
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
            "SPRING_JPA_PROPERTIES_HIBERNATE_BOOT_ALLOW_JDBC_METADATA_ACCESS": "false",
            "SPRING_APPLICATION_JSON": json.dumps({"spring.jpa.properties.hibernate.boot.allow_jdbc_metadata_access": False}),
            "SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT": "-1",
            "SPRING_DATASOURCE_HIKARI_CONNECTION_TIMEOUT": "1000",
            "SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE": "0",
        }
        container["env"] = [e for e in container.get("env", []) if e["name"] not in overrides]
        container["env"] += [{"name": k, "value": v} for k, v in overrides.items()]
        for key in ("livenessProbe", "readinessProbe", "startupProbe"):
            container.pop(key, None)
        spec["containers"] = [container]
        spec["restartPolicy"] = "Never"
        spec["activeDeadlineSeconds"] = 600
        spec.pop("initContainers", None)  # Let SSI inject fresh init containers for this pod.
        pod = {"apiVersion": "v1", "kind": "Pod", "metadata": {"name": pod_name, "namespace": "oficina",
               "labels": {k: v for k, v in labels.items() if k.startswith("tags.datadoghq.com/")},
               "annotations": {k: v for k, v in template["metadata"].get("annotations", {}).items()
                               if not k.endswith(".checks")}}, "spec": spec}
        forward = None
        try:
            kube("create", "-f", "-", manifest=pod)
            kube("-n", "oficina", "wait", "--for=condition=Ready", "pod/" + pod_name, "--timeout=60s")
            with socket.socket() as port:
                port.bind(("127.0.0.1", 0))
                local_port = port.getsockname()[1]
            forward = subprocess.Popen(["kubectl", "-n", "oficina", "port-forward", "pod/" + pod_name,
                                        f"{local_port}:8081", "--address=127.0.0.1"],
                                       stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            base = f"http://127.0.0.1:{local_port}"
            ready = False
            for _ in range(60):
                if forward.poll() is not None:
                    raise RuntimeError("Port-forward terminou antes da chamada")
                try:
                    if http(base + "/actuator/health/liveness")[0] == 200:
                        ready = True
                        break
                except (OSError, urllib.error.URLError):
                    pass
                time.sleep(2)
            if not ready:
                raise RuntimeError("Pod de teste nao iniciou em 120 segundos")
            self.call("GET", "/ordens-servico/" + str(uuid.uuid4()), expected=500, base=base)
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
            if not {"os.processing_failed", "integration.failed"}.issubset({e["event"] for e in events}):
                raise RuntimeError("A falha real de conexao nao emitiu os dois eventos esperados")
            # Allow Agent tailing before deleting the pod. Remote ingestion is verified separately.
            time.sleep(15)
        finally:
            if forward:
                forward.terminate()
                forward.wait(timeout=10)
            kube("-n", "oficina", "delete", "pod", pod_name, "--ignore-not-found", "--wait=false")
            self.save()

    def check_logs(self):
        raw = kube("-n", "oficina", "logs", "-l", "tags.datadoghq.com/service=oficina-api",
                   "-c", "oficina-api", "--since=15m", "--tail=5000", "--max-log-requests=10")
        events = list(self.report.get("fault_events", []))
        for line in raw.splitlines():
            try:
                entry = json.loads(line)
                if str(entry.get("request_id", "")).startswith(self.run_id):
                    events.append({k: entry.get(k) for k in
                        ("event", "request_id", "os_status", "duracao_ms", "integracao", "dd.trace_id", "dd.span_id")})
            except ValueError:
                pass
        self.report["events"] = events
        self.save()
        required = {"os.created", "os.status_duration"}
        if "fault_events" in self.report:
            required |= {"os.processing_failed", "integration.failed"}
        found = {e.get("event") for e in events}
        statuses = {e.get("os_status") for e in events if e.get("event") == "os.status_duration"}
        if not required <= found or not {"EM_DIAGNOSTICO", "EM_EXECUCAO", "FINALIZADA"} <= statuses:
            raise RuntimeError("Eventos incompletos; consulte evidence.json")
        if not any(e.get("dd.trace_id") and e.get("dd.span_id") for e in events):
            raise RuntimeError("Trace/span IDs ausentes; confira a injecao do tracer no pod")
        self.report["datadog_query"] = "service:oficina-api env:hom @request_id:" + self.run_id + "*"
        self.report["datadog_validation"] = "Confirmar ingestao, graficos, trace e monitores no Datadog"
        self.save()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("mode", choices=["run", "verify"])
    parser.add_argument("--url", required=True, help="URL publica de HOMOLOGACAO")
    parser.add_argument("--output", default="target/observability-evidence")
    parser.add_argument("--fault", action="store_true", help="Pod temporario com conexao PostgreSQL recusada")
    args = parser.parse_args()
    validation = Validation(args.url, args.output)
    try:
        if args.mode == "verify":
            validation.report = json.loads((validation.output / "evidence.json").read_text(encoding="utf-8"))
            validation.run_id = validation.report["run_id"]
        else:
            validation.traffic()
            if args.fault:
                validation.failure()
        validation.check_logs()
        validation.report.pop("error", None)
        validation.save()
        print("Evidencias: " + str(validation.output / "evidence.json"))
        print(validation.report["datadog_query"])
        print("Confira essa busca e as metricas no Datadog; abra um trace e confirme o email.")
    except (RuntimeError, OSError, KeyError, ValueError) as failure:
        validation.report["error"] = type(failure).__name__ + ": " + str(failure)
        validation.save()
        raise SystemExit(str(failure)) from None


if __name__ == "__main__":
    main()
