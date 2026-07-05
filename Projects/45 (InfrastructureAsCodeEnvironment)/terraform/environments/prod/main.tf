# PROD environment root.
# Same shared composition as dev (`source = "../../"`) but with production-sized
# inputs and a STRICTER access range — and, critically, its own separate state
# (see backend.tf.example). Nothing here was initialized or applied.

terraform {
  required_version = ">= 1.6.0, < 2.0.0"
}

module "environment" {
  source = "../../"

  project_name   = "iac-lab"
  environment    = "prod"
  region         = "example-region-1"
  instance_count = 3
  instance_size  = "large"

  # Prod is locked down to a single documentation address (a would-be bastion),
  # not an open range.
  allowed_ssh_cidrs = ["198.51.100.10/32"] # RFC 5737 TEST-NET-2 placeholder
}

output "ansible_hosts" {
  value = module.environment.ansible_hosts
}
