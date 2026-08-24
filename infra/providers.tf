terraform {
  required_version = ">= 1.6.0"

  backend "kubernetes" {
    config_path   = "~/.kube/config"
    namespace     = "default"
    secret_suffix = "oficina-api-local"
  }

  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.31"
    }
  }
}

provider "kubernetes" {
  config_path    = pathexpand(var.kubeconfig_path)
  config_context = var.kubeconfig_context == "" ? null : var.kubeconfig_context
}
