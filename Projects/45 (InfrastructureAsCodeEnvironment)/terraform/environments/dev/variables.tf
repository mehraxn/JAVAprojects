# DEV environment inputs. Defaults here are the dev values, so `terraform
# validate` works without a tfvars file. Copy terraform.tfvars.example to
# terraform.tfvars (git-ignored) to override per checkout.

variable "project_name" {
  description = "Non-sensitive project label."
  type        = string
  default     = "iac-lab"
}

variable "environment" {
  description = "Environment name for this root."
  type        = string
  default     = "dev"
}

variable "region" {
  description = "Placeholder region label (not sent to any provider)."
  type        = string
  default     = "example-region-1"
}

variable "vpc_cidr" {
  description = "Network CIDR (RFC 1918 private range, model only)."
  type        = string
  default     = "10.10.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "Subnet CIDRs carved from vpc_cidr (model values only)."
  type        = list(string)
  default     = ["10.10.1.0/24", "10.10.2.0/24"]
}

variable "instance_count" {
  description = "How many app nodes to model."
  type        = number
  default     = 1
}

variable "instance_size" {
  description = "Placeholder instance-size label."
  type        = string
  default     = "small"
}

variable "allowed_ssh_cidrs" {
  description = "Source CIDRs allowed for SSH (RFC 5737 documentation ranges only)."
  type        = list(string)
  default     = ["192.0.2.0/24"]
}
