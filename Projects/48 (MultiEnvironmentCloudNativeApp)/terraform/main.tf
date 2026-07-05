terraform {
  required_version = ">= 1.6.0, < 2.0.0"
}

locals {
  environments = toset(["dev", "staging", "prod-design-only"])
}

resource "terraform_data" "environment_design" {
  for_each = local.environments
  input    = each.value
}

# TODO: Add real provider modules only for approved disposable environments.
