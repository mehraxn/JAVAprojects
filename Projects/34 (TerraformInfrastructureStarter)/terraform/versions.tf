terraform {
  required_version = ">= 1.6.0, < 2.0.0"

  # No external provider is required. terraform_data belongs to Terraform's
  # built-in provider and does not create cloud or network infrastructure.
}
