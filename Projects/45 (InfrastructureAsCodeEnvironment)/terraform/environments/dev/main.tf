# DEV environment root.
# Each environment is its OWN Terraform working directory with its OWN state
# (see backend.tf.example) so a change to dev can never touch prod. It reuses the
# shared root composition via `source = "../../"`, passing dev-sized inputs.
# Nothing here was initialized or applied.

terraform {
  required_version = ">= 1.6.0, < 2.0.0"
}

module "environment" {
  source = "../../"

  project_name   = "iac-lab"
  environment    = "dev"
  region         = "example-region-1"
  instance_count = 1
  instance_size  = "small"

  # Dev is permissive within the documentation range for easy iteration.
  allowed_ssh_cidrs = ["192.0.2.0/24"] # RFC 5737 TEST-NET-1 placeholder
}

output "ansible_hosts" {
  value = module.environment.ansible_hosts
}
