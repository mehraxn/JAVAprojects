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

# This Terraform file is intentionally design-only. Real cloud resources are not
# created in this portfolio project. In a real setup, this layer would provision
# namespaces, Argo CD bootstrap resources, registry access, and environment-specific
# infrastructure.
