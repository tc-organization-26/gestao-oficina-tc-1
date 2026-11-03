import json
from pathlib import Path
import subprocess
import tempfile
import unittest
from unittest.mock import patch

from validate import Validation


def deployment(environment):
    return {"spec": {"template": {
        "metadata": {"labels": {"app": "oficina-api", "tags.datadoghq.com/env": environment}},
        "spec": {"containers": [{"name": "oficina-api", "env": []}]},
    }}}


class ValidationEnvironmentTest(unittest.TestCase):
    def setUp(self):
        target = Path(__file__).resolve().parents[2] / "target"
        target.mkdir(exist_ok=True)
        self.directory = tempfile.TemporaryDirectory(dir=target)
        self.addCleanup(self.directory.cleanup)

    def validation(self, environment="hom"):
        return Validation("https://example.execute-api.us-east-1.amazonaws.com",
                          self.directory.name, environment)

    def test_prod_checks_its_own_ssm_url(self):
        validation = self.validation("prod")
        with patch("validate.kube", return_value=json.dumps(deployment("prod"))), \
             patch("validate.subprocess.run", return_value=subprocess.CompletedProcess([], 0, "example\n")) as run:
            validation.validate_target()
        self.assertIn("/oficina/prod/edge/http-api-id", run.call_args.args[0])

    def test_wrong_cluster_is_rejected_before_discovery(self):
        with patch("validate.kube", return_value=json.dumps(deployment("hom"))), \
             patch("validate.subprocess.run") as run:
            with self.assertRaisesRegex(RuntimeError, "prod"):
                self.validation("prod").validate_target()
        run.assert_not_called()

    def test_wrong_gateway_is_rejected(self):
        with patch("validate.kube", return_value=json.dumps(deployment("prod"))), \
             patch("validate.subprocess.run", return_value=subprocess.CompletedProcess([], 0, "other-api")):
            with self.assertRaisesRegex(RuntimeError, "API Gateway de prod"):
                self.validation("prod").validate_target()

    def test_wrong_cluster_cannot_create_fault_pod(self):
        with patch("validate.kube", return_value=json.dumps(deployment("hom"))) as kube:
            with self.assertRaisesRegex(RuntimeError, "env:prod"):
                self.validation("prod").failure()
        self.assertEqual(kube.call_count, 1)

    def test_failed_startup_still_cleans_isolated_pod(self):
        validation = self.validation("prod")
        with patch("validate.kube", side_effect=[json.dumps(deployment("prod")), "", RuntimeError("startup"), ""]) as kube:
            with self.assertRaisesRegex(RuntimeError, "startup"):
                validation.failure()
        pod = kube.call_args_list[1].kwargs["manifest"]
        self.assertNotIn("app", pod["metadata"]["labels"])
        self.assertEqual(pod["metadata"]["labels"]["admission.datadoghq.com/enabled"], "true")
        self.assertEqual(pod["metadata"]["labels"]["tags.datadoghq.com/env"], "prod")
        overrides = {e["name"]: e["value"] for e in pod["spec"]["containers"][0]["env"]}
        self.assertIn("127.0.0.1:1", overrides["SPRING_DATASOURCE_URL"])
        self.assertEqual(overrides["SPRING_FLYWAY_ENABLED"], "false")
        self.assertEqual(kube.call_args.args[:5], ("-n", "oficina", "delete", "pod", validation.run_id + "-fault"))

    def test_evidence_and_datadog_query_use_selected_environment(self):
        for environment in ("hom", "prod"):
            with self.subTest(environment=environment):
                validation = self.validation(environment)
                validation.report["fault_events"] = [
                    {"event": "integration.failed", "integracao": "postgresql"},
                    {"event": "os.processing_failed"},
                ]
                validation.check_logs()
                evidence = json.loads((Path(self.directory.name) / "evidence.json").read_text())
                self.assertEqual(evidence["environment"], environment)
                self.assertIn("env:" + environment, evidence["datadog_query"])


if __name__ == "__main__":
    unittest.main()
