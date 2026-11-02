resource "datadog_logs_metric" "lambda_duration" {
  name = "${local.prefix}.lambda.duration_ms"
  filter { query = "env:${var.environment} (service:oficina-login OR service:oficina-authorizer) (@event:http.request OR @event:lambda.request)" }
  compute {
    aggregation_type    = "distribution"
    path                = "@duracao_ms"
    include_percentiles = true
  }
  group_by {
    path     = "service"
    tag_name = "service"
  }
}

resource "datadog_monitor" "lambda_failure" {
  name    = "[${var.environment}] Falha tecnica em login ou authorizer"
  type    = "log alert"
  query   = "logs(\"env:${var.environment} (service:oficina-login OR service:oficina-authorizer) (@event:auth.failed OR @event:lambda.request) status:error\").index(\"*\").rollup(\"count\").last(\"5m\") > 0"
  message = "Falha tecnica na autenticacao. Consulte request_id e trace. ${var.notification_target}"
  monitor_thresholds { critical = 0 }
  enable_logs_sample = true
  on_missing_data    = "resolve"
  tags               = ["env:${var.environment}", "managed-by:terraform"]
}
