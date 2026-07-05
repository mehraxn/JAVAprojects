variable "project_name" {
  description = "Short lowercase name used in the local learning model."
  type        = string
  default     = "iac-starter"

  validation {
    condition     = can(regex("^[a-z][a-z0-9-]{2,30}$", var.project_name))
    error_message = "project_name must be 3-31 lowercase letters, numbers, or hyphens and start with a letter."
  }
}

variable "environment" {
  description = "Logical non-production environment represented by this example."
  type        = string
  default     = "dev"

  validation {
    condition     = contains(["dev", "test", "sandbox"], var.environment)
    error_message = "environment must be dev, test, or sandbox."
  }
}

variable "components" {
  description = "Names of imaginary application components modeled as local Terraform data."
  type        = set(string)
  default     = ["backend", "database", "frontend"]

  validation {
    condition = length(var.components) > 0 && alltrue([
      for component in var.components :
      can(regex("^[a-z][a-z0-9-]{1,29}$", component))
    ])
    error_message = "components must contain at least one valid lowercase component name."
  }
}

variable "additional_labels" {
  description = "Extra non-sensitive labels added to the learning model."
  type        = map(string)
  default     = {}

  validation {
    condition = alltrue([
      for key, value in var.additional_labels :
      length(trimspace(key)) > 0 && length(trimspace(value)) > 0
    ])
    error_message = "additional_labels cannot contain empty keys or values."
  }
}
