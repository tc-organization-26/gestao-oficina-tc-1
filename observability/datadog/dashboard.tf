locals {
  charts = [
    { title = "Ordens criadas por dia (UTC)", query = "sum:${local.prefix}.os.created{*}.as_count().rollup(sum,86400)", display = "bars" },
    { title = "Tempo medio em Diagnostico (ms)", query = "avg:${local.prefix}.os.status_duration_ms{os_status:em_diagnostico}", display = "line" },
    { title = "Tempo medio em Execucao (ms)", query = "avg:${local.prefix}.os.status_duration_ms{os_status:em_execucao}", display = "line" },
    { title = "Tempo medio em Finalizacao: finalizada ate entrega (ms)", query = "avg:${local.prefix}.os.status_duration_ms{os_status:finalizada}", display = "line" },
    { title = "Falhas por integracao", query = "sum:${local.prefix}.integration.errors{*} by {integracao}.as_count()", display = "bars" },
    { title = "Falhas de processamento de OS", query = "sum:${local.prefix}.os.errors{*}.as_count()", display = "bars" },
    { title = "Latencia p95 por rota HTTP (ms)", query = "p95:${local.prefix}.http.duration_ms{*} by {http_route}", display = "line" },
    { title = "Latencia p95 de login e authorizer (ms)", query = "p95:${local.prefix}.lambda.duration_ms{*} by {service}", display = "line" },
    { title = "CPU por pod (cores)", query = "avg:kubernetes.cpu.usage.total{kube_namespace:oficina,kube_container_name:oficina-api,env:${var.environment}} by {pod_name} / 1000000000", display = "line" },
    { title = "Memoria por pod (bytes)", query = "avg:kubernetes.memory.usage{kube_namespace:oficina,kube_container_name:oficina-api,env:${var.environment}} by {pod_name}", display = "line" },
    { title = "Healthcheck interno: tempo de resposta (s)", query = "avg:network.http.response_time{service:oficina-api,env:${var.environment}} by {pod_name}", display = "line" }
  ]
}

resource "datadog_dashboard_json" "oficina" {
  dashboard = jsonencode({
    title       = "Oficina - ${var.environment} - Operação e ordens de serviço"
    description = <<-EOT
      Visão operacional da Oficina no ambiente **${var.environment}**, com volume de ordens de serviço, tempos por status, falhas, latência e saúde dos pods.

      **Filtro APM:** `service:oficina-api env:${var.environment}`
    EOT
    layout_type = "ordered"
    widgets = concat([
      { definition = {
        type             = "note"
        content          = <<-EOT
          **Glossário**

          - **Volume:** OS persistidas (`os.created`), agrupadas por dia em UTC.
          - **Tempos médios:** tempo de permanência nos status encerrados, não a duração da chamada HTTP.
          - **Finalização:** tempo entre FINALIZADA e ENTREGUE.
          - **Histórico:** dados coletados a partir da instrumentação; dados anteriores à V4 não são reconstruídos.
          - **Dependências:** consulte **APM → oficina-api**.
          - **Disponibilidade pública:** monitorada separadamente no **UptimeRobot**.
        EOT
        background_color = "white"
        font_size        = "14"
        text_align       = "left"
        show_tick        = false
        has_padding      = true
      } }
      ], [for chart in local.charts : {
        definition = {
          type     = "timeseries"
          title    = chart.title
          requests = [{ q = chart.query, display_type = chart.display }]
        }
      }], [
      { definition = {
        type     = "check_status"
        title    = "Readiness dos pods"
        check    = "http.can_connect"
        grouping = "cluster"
        group_by = ["pod_name"]
        tags     = ["service:oficina-api", "env:${var.environment}"]
      } }
    ])
  })
  depends_on = [datadog_logs_metric.business]
}
