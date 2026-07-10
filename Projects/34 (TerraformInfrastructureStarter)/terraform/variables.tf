variable "project_name" {
  description = "Short lowercase name used in the local learning model."
  type        = string
  default     = "iac-starter"

  validation {
    condition = (
      length(var.project_name) >= 3 &&
      length(var.project_name) <= 40 &&
      can(regex("^[a-z][a-z0-9-]*[a-z0-9]$", var.project_name))
    )
    error_message = "project_name must be 3-40 characters using only lowercase letters, numbers, and hyphens; it must start with a lowercase letter and end with a letter or number (so no leading/trailing hyphen and no uppercase)."
  }
}

variable "environment" {
  description = "Logical environment label for the learning model (no real environment is touched)."
  type        = string
  default     = "dev"

  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "environment must be one of: dev, staging, prod."
  }
}

variable "components" {
  description = "Names of imaginary application components modeled as local Terraform data."
  type        = set(string)
  default     = ["backend", "database", "frontend"]

  validation {
    condition = length(var.components) > 0 && alltrue([
      for component in var.components : (
        length(component) >= 2 &&
        length(component) <= 30 &&
        can(regex("^[a-z][a-z0-9-]*[a-z0-9]$", component))
      )
    ])
    error_message = "components needs at least one name of 2-30 characters using only lowercase letters, numbers, and hyphens; each must start with a lowercase letter and end with a letter or number (no trailing hyphen, no underscores)."
  }
}

variable "additional_labels" {
  description = "Extra non-sensitive labels merged into the learning model. Reserved label keys are controlled by the module and cannot be set here."
  type        = map(string)
  default     = {}

  validation {
    condition = alltrue([
      for key, value in var.additional_labels :
      length(trimspace(key)) > 0 && length(trimspace(value)) > 0
    ])
    error_message = "additional_labels cannot contain empty keys or values."
  }

  validation {
    condition = length(setintersection(
      keys(var.additional_labels),
      ["managed_by", "project", "environment", "purpose", "component"]
    )) == 0
    error_message = "additional_labels must not override reserved labels: managed_by, project, environment, purpose, component."
  }
}
