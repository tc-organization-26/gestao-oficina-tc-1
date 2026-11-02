variable "environment" {
  type    = string
  default = "hom"
  validation {
    condition     = contains(["hom", "prod"], var.environment)
    error_message = "Use hom ou prod, como no CI/CD."
  }
}
variable "datadog_api_url" {
  type    = string
  default = "https://api.datadoghq.com/"
}
variable "notification_target" {
  type        = string
  description = "Destino Datadog, por exemplo @email@dominio.com ou @slack-canal."
  validation {
    condition     = startswith(var.notification_target, "@")
    error_message = "Informe um destino de notificacao do Datadog iniciado por @."
  }
}
variable "latency_p95_ms" {
  type    = number
  default = 1000
}
