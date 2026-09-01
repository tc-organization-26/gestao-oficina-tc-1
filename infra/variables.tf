variable "kubeconfig_path" {
  description = "Caminho do kubeconfig local."
  type        = string
  default     = "~/.kube/config"
}

variable "kubeconfig_context" {
  description = "Contexto Kubernetes. Exemplos locais: docker-desktop, kind-oficina ou minikube. Em CI/CD, pode ficar vazio para usar o current-context do kubeconfig."
  type        = string
  default     = ""
}

variable "namespace" {
  description = "Namespace Kubernetes da aplicacao."
  type        = string
  default     = "oficina"
}

variable "app_image" {
  description = "Imagem Docker da API."
  type        = string
  default     = "oficina-api:local"
}

variable "postgres_image" {
  description = "Imagem Docker do PostgreSQL."
  type        = string
  default     = "postgres:16-alpine"
}

variable "postgres_db" {
  description = "Nome do banco de dados."
  type        = string
  default     = "oficina_db_2"
}

variable "postgres_storage_class_name" {
  description = "StorageClass usado pelo PVC do PostgreSQL. No Docker Desktop, o padrao costuma ser hostpath. No Minikube e em alguns clusters remotos, costuma ser standard."
  type        = string
  default     = "hostpath"
}

variable "app_service_type" {
  description = "Tipo do Service Kubernetes da API. Use NodePort localmente e LoadBalancer em EKS para obter URL publica da AWS."
  type        = string
  default     = "NodePort"

  validation {
    condition     = contains(["NodePort", "LoadBalancer"], var.app_service_type)
    error_message = "app_service_type deve ser NodePort ou LoadBalancer."
  }
}

variable "postgres_user" {
  description = "Usuario do banco de dados."
  type        = string
  sensitive   = true
}

variable "postgres_password" {
  description = "Senha do banco de dados."
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "Chave usada para assinar tokens JWT."
  type        = string
  sensitive   = true
}

variable "jwt_expiration_seconds" {
  description = "Tempo de expiracao do JWT em segundos."
  type        = string
  default     = "3600"
}

variable "registry_server" {
  description = "Servidor do registry usado pela imagem da aplicacao."
  type        = string
  default     = "ghcr.io"
}

variable "registry_username" {
  description = "Usuario opcional do registry para criar imagePullSecret no cluster."
  type        = string
  default     = ""
  sensitive   = true
}

variable "registry_password" {
  description = "Token opcional do registry para criar imagePullSecret no cluster."
  type        = string
  default     = ""
  sensitive   = true
}
