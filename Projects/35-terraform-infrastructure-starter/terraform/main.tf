locals {
  # Standard labels are merged last, so they would win any collision — and a
  # validation rule on additional_labels rejects reserved keys outright, so
  # users get a clear error instead of a silent override.
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
