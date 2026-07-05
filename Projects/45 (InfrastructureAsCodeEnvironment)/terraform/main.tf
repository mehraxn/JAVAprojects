# Root composition. This wires the reusable modules into one modeled
# environment. Running `terraform apply` here would only write terraform_data to
# LOCAL state — no cloud resource is created. Nothing was applied in this repo.

locals {
  common_labels = merge(
    var.additional_labels,
    {
      managed_by  = "terraform"
      project     = var.project_name
      environment = var.environment
      purpose     = "learning"
    }
  )
}

module "network" {
  source = "./modules/network"

  project_name        = var.project_name
  environment         = var.environment
  region              = var.region
  vpc_cidr            = var.vpc_cidr
  public_subnet_cidrs = var.public_subnet_cidrs
  labels              = local.common_labels
}

module "compute" {
  source = "./modules/compute"

  project_name      = var.project_name
  environment       = var.environment
  region            = var.region
  instance_count    = var.instance_count
  instance_size     = var.instance_size
  subnet_ids        = module.network.subnet_ids
  allowed_ssh_cidrs = var.allowed_ssh_cidrs
  labels            = local.common_labels
}
