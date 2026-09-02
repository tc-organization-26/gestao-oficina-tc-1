locals {
  app_labels = {
    app = "oficina-api"
  }

  postgres_labels = {
    app = "postgres"
  }
}

resource "kubernetes_namespace_v1" "oficina" {
  metadata {
    name = var.namespace
  }
}

resource "kubernetes_config_map_v1" "postgres" {
  metadata {
    name      = "postgres-config"
    namespace = kubernetes_namespace_v1.oficina.metadata[0].name
  }

  data = {
    POSTGRES_DB = var.postgres_db
    PGDATA      = "/var/lib/postgresql/data/pgdata"
  }
}

resource "kubernetes_secret_v1" "postgres" {
  metadata {
    name      = "postgres-secret"
    namespace = kubernetes_namespace_v1.oficina.metadata[0].name
  }

  type = "Opaque"

  data = {
    POSTGRES_USER     = var.postgres_user
    POSTGRES_PASSWORD = var.postgres_password
  }
}

resource "kubernetes_persistent_volume_claim_v1" "postgres" {
  wait_until_bound = false

  metadata {
    name      = "postgres-data"
    namespace = kubernetes_namespace_v1.oficina.metadata[0].name
  }

  spec {
    access_modes       = ["ReadWriteOnce"]
    storage_class_name = var.postgres_storage_class_name

    resources {
      requests = {
        storage = "1Gi"
      }
    }
  }
}

resource "kubernetes_deployment_v1" "postgres" {
  metadata {
    name      = "postgres"
    namespace = kubernetes_namespace_v1.oficina.metadata[0].name
  }

  spec {
    replicas = 1

    selector {
      match_labels = local.postgres_labels
    }

    template {
      metadata {
        labels = local.postgres_labels
      }

      spec {
        container {
          name              = "postgres"
          image             = var.postgres_image
          image_pull_policy = "IfNotPresent"

          port {
            container_port = 5432
          }

          env_from {
            config_map_ref {
              name = kubernetes_config_map_v1.postgres.metadata[0].name
            }
          }

          env_from {
            secret_ref {
              name = kubernetes_secret_v1.postgres.metadata[0].name
            }
          }

          volume_mount {
            name       = "postgres-data"
            mount_path = "/var/lib/postgresql/data"
          }

          readiness_probe {
            exec {
              command = ["sh", "-c", "pg_isready -U \"$POSTGRES_USER\" -d \"$POSTGRES_DB\""]
            }

            initial_delay_seconds = 10
            period_seconds        = 10
          }

          liveness_probe {
            exec {
              command = ["sh", "-c", "pg_isready -U \"$POSTGRES_USER\" -d \"$POSTGRES_DB\""]
            }

            initial_delay_seconds = 30
            period_seconds        = 20
          }
        }

        volume {
          name = "postgres-data"

          persistent_volume_claim {
            claim_name = kubernetes_persistent_volume_claim_v1.postgres.metadata[0].name
          }
        }
      }
    }
  }
}

resource "kubernetes_service_v1" "postgres" {
  metadata {
    name      = "postgres"
    namespace = kubernetes_namespace_v1.oficina.metadata[0].name
  }

  spec {
    type     = "ClusterIP"
    selector = local.postgres_labels

    port {
      name        = "postgres"
      port        = 5432
      target_port = 5432
    }
  }
}

resource "kubernetes_config_map_v1" "app" {
  metadata {
    name      = "oficina-api-config"
    namespace = kubernetes_namespace_v1.oficina.metadata[0].name
  }

  data = {
    SERVER_PORT                       = "8081"
    SPRING_DATASOURCE_URL             = "jdbc:postgresql://${kubernetes_service_v1.postgres.metadata[0].name}:5432/${var.postgres_db}"
    SPRING_JPA_HIBERNATE_DDL_AUTO     = "none"
    SPRING_FLYWAY_BASELINE_ON_MIGRATE = "true"
    SPRING_FLYWAY_BASELINE_VERSION    = "0"
    SPRING_FLYWAY_VALIDATE_ON_MIGRATE = "true"
    SECURITY_JWT_EXPIRATION_SECONDS   = var.jwt_expiration_seconds
  }
}

resource "kubernetes_secret_v1" "app" {
  metadata {
    name      = "oficina-api-secret"
    namespace = kubernetes_namespace_v1.oficina.metadata[0].name
  }

  type = "Opaque"

  data = {
    SPRING_DATASOURCE_USERNAME = var.postgres_user
    POSTGRES_PASSWORD          = var.postgres_password
    SECURITY_JWT_SECRET        = var.jwt_secret
  }
}

resource "kubernetes_secret_v1" "registry" {
  count = var.registry_username != "" && var.registry_password != "" ? 1 : 0

  metadata {
    name      = "ghcr-credentials"
    namespace = kubernetes_namespace_v1.oficina.metadata[0].name
  }

  type = "kubernetes.io/dockerconfigjson"

  data = {
    ".dockerconfigjson" = jsonencode({
      auths = {
        (var.registry_server) = {
          username = var.registry_username
          password = var.registry_password
          auth     = base64encode("${var.registry_username}:${var.registry_password}")
        }
      }
    })
  }
}

resource "kubernetes_deployment_v1" "app" {
  metadata {
    name      = "oficina-api"
    namespace = kubernetes_namespace_v1.oficina.metadata[0].name
  }

  spec {
    replicas = 1

    selector {
      match_labels = local.app_labels
    }

    template {
      metadata {
        labels = local.app_labels
      }

      spec {
        dynamic "image_pull_secrets" {
          for_each = kubernetes_secret_v1.registry

          content {
            name = image_pull_secrets.value.metadata[0].name
          }
        }

        container {
          name              = "oficina-api"
          image             = var.app_image
          image_pull_policy = "IfNotPresent"

          port {
            container_port = 8081
          }

          env_from {
            config_map_ref {
              name = kubernetes_config_map_v1.app.metadata[0].name
            }
          }

          env_from {
            secret_ref {
              name = kubernetes_secret_v1.app.metadata[0].name
            }
          }

          resources {
            requests = {
              cpu    = "250m"
              memory = "512Mi"
            }

            limits = {
              cpu    = "500m"
              memory = "768Mi"
            }
          }

          readiness_probe {
            tcp_socket {
              port = 8081
            }

            initial_delay_seconds = 60
            period_seconds        = 10
            failure_threshold     = 6
          }

          liveness_probe {
            tcp_socket {
              port = 8081
            }

            initial_delay_seconds = 90
            period_seconds        = 20
            failure_threshold     = 3
          }
        }
      }
    }
  }

  depends_on = [
    kubernetes_deployment_v1.postgres,
    kubernetes_service_v1.postgres
  ]
}

resource "kubernetes_service_v1" "app" {
  metadata {
    name      = "oficina-api"
    namespace = kubernetes_namespace_v1.oficina.metadata[0].name
  }

  spec {
    type     = var.app_service_type
    selector = local.app_labels

    port {
      name        = "http"
      port        = 8081
      target_port = 8081
      node_port   = var.app_service_type == "NodePort" ? 30081 : null
    }
  }
}

resource "kubernetes_horizontal_pod_autoscaler_v2" "app" {
  metadata {
    name      = "oficina-api"
    namespace = kubernetes_namespace_v1.oficina.metadata[0].name
  }

  spec {
    min_replicas = 1
    max_replicas = 5

    scale_target_ref {
      api_version = "apps/v1"
      kind        = "Deployment"
      name        = kubernetes_deployment_v1.app.metadata[0].name
    }

    metric {
      type = "Resource"

      resource {
        name = "cpu"

        target {
          type                = "Utilization"
          average_utilization = 70
        }
      }
    }

    metric {
      type = "Resource"

      resource {
        name = "memory"

        target {
          type                = "Utilization"
          average_utilization = 80
        }
      }
    }
  }
}
