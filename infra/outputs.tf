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
  value       = var.app_service_type == "NodePort" ? "http://localhost:30081" : null
}

output "app_load_balancer_hostname" {
  description = "Hostname publico gerado pela AWS quando o Service usa LoadBalancer."
  value       = var.app_service_type == "LoadBalancer" ? try(kubernetes_service_v1.app.status[0].load_balancer[0].ingress[0].hostname, null) : null
}

output "app_load_balancer_ip" {
  description = "IP publico gerado pelo provedor quando o Service usa LoadBalancer."
  value       = var.app_service_type == "LoadBalancer" ? try(kubernetes_service_v1.app.status[0].load_balancer[0].ingress[0].ip, null) : null
}

output "postgres_service_name" {
  description = "Service interno do PostgreSQL."
  value       = kubernetes_service_v1.postgres.metadata[0].name
}
