terraform {
  required_version = ">= 1.6.0, < 2.0.0"

  # NO external/cloud provider is declared on purpose. Every resource in this
  # project uses `terraform_data`, which is part of Terraform's built-in
  # provider and records values in LOCAL state only — it never calls a cloud
  # API, opens a network connection, or creates a real resource.
}
