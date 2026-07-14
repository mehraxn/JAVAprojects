output "instance_names" {
  description = "Modeled instance names."
  value       = [for r in terraform_data.instance : r.input.name]
}

# The provisioning -> configuration handoff: one record per node, shaped the way
# the inventory generator (scripts/generate-inventory.py) consumes it. All values
# are deterministic placeholders (RFC 1918 private IPs, no secrets).
output "ansible_hosts" {
  description = "Placeholder host records for Ansible (non-sensitive)."
  value = [
    for r in terraform_data.instance : {
      name         = r.input.name
      ansible_host = r.input.private_ip
      ansible_user = var.ansible_user
      private_ip   = r.input.private_ip
      environment  = var.environment
    }
  ]
}
