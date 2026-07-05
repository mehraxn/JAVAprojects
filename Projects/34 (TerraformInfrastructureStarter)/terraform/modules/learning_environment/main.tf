# terraform_data is built into Terraform. These resources record structured
# values in local state but do not call a cloud API or run external commands.
resource "terraform_data" "environment" {
  input = {
    project_name = var.project_name
    environment  = var.environment
    labels       = var.labels
  }
}

resource "terraform_data" "component" {
  for_each = var.components

  input = {
    name         = each.value
    project_name = var.project_name
    environment  = var.environment
    labels       = var.labels
  }
}
