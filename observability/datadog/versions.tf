terraform {
  required_version = ">= 1.10.0"
  backend "s3" {}
  required_providers {
    datadog = {
      source  = "DataDog/datadog"
      version = "~> 4.0"
    }
  }
}

# DD_API_KEY and DD_APP_KEY are supplied only through environment variables.
provider "datadog" {
  api_url = var.datadog_api_url
}
