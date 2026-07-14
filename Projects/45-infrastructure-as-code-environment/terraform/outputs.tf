output "network" {
  description = "Modeled network metadata (non-sensitive)."
  value = {
    network_id  = module.network.network_id
    vpc_cidr    = var.vpc_cidr
    subnet_ids  = module.network.subnet_ids
  }
}

output "compute" {
  description = "Modeled compute metadata (non-sensitive)."
  value = {
    instance_names = module.compute.instance_names
    instance_size  = var.instance_size
    instance_count = var.instance_count
  }
}

# This is the handoff Ansible would consume: a list of host records the
# configuration stage turns into an inventory. It contains only placeholder,
# non-sensitive values (see docs/provisioning-flow.md).
output "ansible_hosts" {
  description = "Placeholder host records for the provisioning -> configuration handoff."
  value       = module.compute.ansible_hosts
}
