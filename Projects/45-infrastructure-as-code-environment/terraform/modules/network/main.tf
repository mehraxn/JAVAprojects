# Network module — MODEL ONLY.
# In a real project these terraform_data resources would be an aws_vpc /
# google_compute_network / azurerm_virtual_network plus subnets. Here they are
# built-in terraform_data records so applying creates NOTHING in any cloud.

resource "terraform_data" "vpc" {
  input = {
    name   = "${var.project_name}-${var.environment}-vpc"
    cidr   = var.vpc_cidr
    region = var.region
    labels = var.labels
  }
}

resource "terraform_data" "subnet" {
  for_each = { for idx, cidr in var.public_subnet_cidrs : idx => cidr }

  input = {
    name   = "${var.project_name}-${var.environment}-subnet-${each.key}"
    cidr   = each.value
    region = var.region
    labels = var.labels
  }
}
