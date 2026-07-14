variable "project_name" {
  description = "Project label from the root module."
  type        = string
}

variable "environment" {
  description = "Environment label from the root module."
  type        = string
}

variable "region" {
  description = "Placeholder region label (not sent to any provider)."
  type        = string
}

variable "vpc_cidr" {
  description = "Network CIDR (model value only)."
  type        = string
}

variable "public_subnet_cidrs" {
  description = "Subnet CIDRs (model values only)."
  type        = list(string)
}

variable "labels" {
  description = "Non-sensitive labels to attach to modeled resources."
  type        = map(string)
}
