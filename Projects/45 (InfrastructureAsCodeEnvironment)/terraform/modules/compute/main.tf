# Compute module — MODEL ONLY.
# In a real project each of these would be an aws_instance / google_compute_instance
# / azurerm_linux_virtual_machine. Here they are terraform_data records, so an
# apply produces LOCAL state only and NO real servers.

resource "terraform_data" "instance" {
  count = var.instance_count

  input = {
    name              = "${var.project_name}-${var.environment}-app-${count.index}"
    size              = var.instance_size
    region            = var.region
    subnet_id         = element(var.subnet_ids, count.index)
    allowed_ssh_cidrs = var.allowed_ssh_cidrs
    labels            = var.labels
    # Placeholder private address from the RFC 5737 TEST-NET-2 documentation
    # range. Reserved and non-routable — never a real host.
    private_ip = "198.51.100.${count.index + 10}"
  }
}
