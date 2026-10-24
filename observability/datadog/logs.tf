resource "datadog_logs_custom_pipeline" "java" {
  name       = "Oficina ${var.environment} - Spring JSON"
  is_enabled = true
  filter { query = local.scope }
  processor {
    status_remapper {
      name       = "Logback level"
      is_enabled = true
      sources    = ["level"]
    }
  }
  processor {
    date_remapper {
      name       = "Spring timestamp"
      is_enabled = true
      sources    = ["@timestamp"]
    }
  }
  processor {
    trace_id_remapper {
      name       = "Datadog trace correlation"
      is_enabled = true
      sources    = ["dd.trace_id"]
    }
  }
  processor {
    span_id_remapper {
      name       = "Datadog span correlation"
      is_enabled = true
      sources    = ["dd.span_id"]
    }
  }
}
