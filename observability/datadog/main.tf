locals {
  scope               = "service:oficina-api env:${var.environment}"
  observability_scope = "env:${var.environment} (service:oficina-api OR service:oficina-login OR service:oficina-authorizer)"
  prefix              = "oficina.${var.environment}"
  tags                = ["service:oficina-api", "env:${var.environment}", "managed-by:terraform"]
  metrics = {
    os_created = { name = "os.created", event = "os.created", aggregation = "count", path = null, groups = {} }
    status_duration = {
      name   = "os.status_duration_ms", event = "os.status_duration", aggregation = "distribution", path = "@duracao_ms"
      groups = { "@os_status" = "os_status" }
    }
    http_duration = {
      name   = "http.duration_ms", event = "http.request", aggregation = "distribution", path = "@duracao_ms"
      groups = { "@http_route" = "http_route", "@http_method" = "http_method" }
    }
    integration_errors = {
      name   = "integration.errors", event = "integration.failed", aggregation = "count", path = null
      groups = { "@integracao" = "integracao" }
    }
    os_errors = { name = "os.errors", event = "os.processing_failed", aggregation = "count", path = null, groups = {} }
  }
}

resource "datadog_logs_metric" "business" {
  for_each = local.metrics
  name     = "${local.prefix}.${each.value.name}"
  filter {
    query = "${each.key == "integration_errors" ? local.observability_scope : local.scope} @event:${each.value.event}"
  }
  compute {
    aggregation_type    = each.value.aggregation
    path                = each.value.path
    include_percentiles = each.value.aggregation == "distribution"
  }
  dynamic "group_by" {
    for_each = each.value.groups
    content {
      path     = group_by.key
      tag_name = group_by.value
    }
  }
}

resource "datadog_monitor" "os_failure" {
  name    = "[${var.environment}] Falha no processamento de OS"
  type    = "log alert"
  query   = "logs(\"${local.scope} @event:os.processing_failed\").index(\"*\").rollup(\"count\").last(\"5m\") > 0"
  message = "Falha tecnica em ordens de servico. Investigue request_id e o trace no log. ${var.notification_target}"
  monitor_thresholds { critical = 0 }
  enable_logs_sample = true
  on_missing_data    = "resolve"
  tags               = local.tags
}

resource "datadog_monitor" "integration_failure" {
  name    = "[${var.environment}] Falha em integracao"
  type    = "log alert"
  query   = "logs(\"${local.observability_scope} @event:integration.failed\").index(\"*\").rollup(\"count\").last(\"5m\") > 0"
  message = "Falha em integracao Java ou Lambda. Verifique integracao, request_id e o trace no log. ${var.notification_target}"
  monitor_thresholds { critical = 0 }
  enable_logs_sample = true
  on_missing_data    = "resolve"
  tags               = local.tags
}

resource "datadog_monitor" "latency" {
  name    = "[${var.environment}] Latencia p95 da API"
  type    = "query alert"
  query   = "avg(last_5m):p95:${local.prefix}.http.duration_ms{*} > ${var.latency_p95_ms}"
  message = "p95 acima de ${var.latency_p95_ms} ms. Consulte APM e o dashboard por rota. ${var.notification_target}"
  monitor_thresholds { critical = var.latency_p95_ms }
  require_full_window = false
  tags                = local.tags
  depends_on          = [datadog_logs_metric.business]
}

resource "datadog_monitor" "readiness" {
  name    = "[${var.environment}] API indisponivel ou coleta ausente"
  type    = "service check"
  query   = "\"http.can_connect\".over(\"service:oficina-api\",\"env:${var.environment}\").by(\"pod_name\").last(3).count_by_status()"
  message = "Readiness HTTP falhou em tres verificacoes ou a coleta deixou de chegar. Verifique API, Agent e PostgreSQL. ${var.notification_target}"
  monitor_thresholds {
    critical = 3
    warning  = 1
    ok       = 1
  }
  notify_no_data    = true
  no_data_timeframe = 10
  tags              = local.tags
}

resource "datadog_monitor" "resources" {
  for_each = {
    cpu = {
      query     = "avg(last_10m):avg:kubernetes.cpu.usage.total{kube_namespace:oficina,kube_container_name:oficina-api,env:${var.environment}} by {pod_name} / 1000000000 > 0.45"
      threshold = 0.45
      message   = "CPU acima de 0.45 cores (limite configurado: 0.5)."
    }
    memory = {
      query     = "avg(last_10m):avg:kubernetes.memory.usage{kube_namespace:oficina,kube_container_name:oficina-api,env:${var.environment}} by {pod_name} > 713031680"
      threshold = 713031680
      message   = "Memoria acima de 680 MiB (limite configurado: 768 MiB)."
    }
  }
  name    = "[${var.environment}] Kubernetes ${each.key} por pod"
  type    = "query alert"
  query   = each.value.query
  message = "${each.value.message} ${var.notification_target}"
  monitor_thresholds { critical = each.value.threshold }
  require_full_window = false
  tags                = local.tags
}

output "dashboard_url" {
  value = datadog_dashboard_json.oficina.url
}
