# DEV environment root.
# Each environment is its OWN Terraform working directory with its OWN state
# (see backend.tf.example) so a change to dev can never touch prod. It reuses the
# shared root composition via `source = "../../"`, passing dev-sized inputs from
# variables (defaults in variables.tf, overridable via terraform.tfvars).
# Nothing here was initialized or applied against a real cloud.

terraform {
  required_version = ">= 1.6.0, < 2.0.0"
}

module "environment" {
  source = "../../"

  project_name        = var.project_name
  environment         = var.environment
  region              = var.region
  vpc_cidr            = var.vpc_cidr
  public_subnet_cidrs = var.public_subnet_cidrs
  instance_count      = var.instance_count
  instance_size       = var.instance_size
  allowed_ssh_cidrs   = var.allowed_ssh_cidrs
}

output "ansible_hosts" {
  description = "Handoff records for the Ansible inventory generator."
  value       = module.environment.ansible_hosts
}
