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
    # Deterministic RFC 1918 private address, derived from the node's subnet CIDR
    # (e.g. 10.10.1.0/24 -> 10.10.1.10). Private and non-routable on the public
    # internet; dev (10.10.x) and prod (10.20.x) are distinguishable.
    private_ip = cidrhost(element(var.public_subnet_cidrs, count.index), 10 + count.index)
  }
}
