output "instance_names" {
  description = "Modeled instance names."
  value       = [for r in terraform_data.instance : r.input.name]
}

# The provisioning -> configuration handoff: one record per node, shaped the way
# an Ansible inventory generator would consume it. All values are placeholders.
output "ansible_hosts" {
  description = "Placeholder host records for Ansible (non-sensitive)."
  value = [
    for r in terraform_data.instance : {
      name        = r.input.name
      ansible_host = "${r.input.name}.example.invalid"
      private_ip  = r.input.private_ip
      environment = var.environment
    }
  ]
}
