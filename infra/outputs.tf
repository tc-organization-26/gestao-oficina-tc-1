output "namespace" {
  description = "Namespace criado no cluster local."
  value       = kubernetes_namespace_v1.oficina.metadata[0].name
}

output "app_service_name" {
  description = "Service Kubernetes da API."
  value       = kubernetes_service_v1.app.metadata[0].name
}

output "app_node_port_url" {
  description = "URL local esperada para Docker Desktop Kubernetes."
  value       = "http://localhost:30081"
}

output "postgres_service_name" {
  description = "Service interno do PostgreSQL."
  value       = kubernetes_service_v1.postgres.metadata[0].name
}
