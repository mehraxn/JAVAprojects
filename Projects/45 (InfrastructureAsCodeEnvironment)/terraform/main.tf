terraform {
  required_version = ">= 1.6.0, < 2.0.0"
}

# Built-in local data only; no cloud provider or external action.
resource "terraform_data" "environment_design" {
  input = {
    name        = var.environment_name
    description = "Local Phase 4 infrastructure design placeholder"
  }
}

# TODO: Add a separately approved disposable provider module later.
