locals {
  labels = merge(
    var.additional_labels,
    {
      managed_by  = "terraform"
      project     = var.project_name
      environment = var.environment
      purpose     = "learning"
    }
  )
}

module "learning_environment" {
  source = "./modules/learning_environment"

  project_name = var.project_name
  environment  = var.environment
  components   = var.components
  labels       = local.labels
}
