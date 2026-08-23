variable "kubeconfig_path" {
  description = "Caminho do kubeconfig local."
  type        = string
  default     = "~/.kube/config"
}

variable "kubeconfig_context" {
  description = "Contexto Kubernetes local. Exemplos: docker-desktop, kind-oficina ou minikube."
  type        = string
  default     = "docker-desktop"
}

variable "namespace" {
  description = "Namespace Kubernetes da aplicacao."
  type        = string
  default     = "oficina"
}

variable "app_image" {
  description = "Imagem Docker local da API."
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
  description = "StorageClass usado pelo PVC do PostgreSQL. No Docker Desktop, o padrao costuma ser hostpath. No Minikube, costuma ser standard."
  type        = string
  default     = "hostpath"
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
