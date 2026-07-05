variable "environment_name" {
  description = "Non-production learning environment name."
  type        = string
  default     = "sandbox"

  validation {
    condition     = contains(["dev", "test", "sandbox"], var.environment_name)
    error_message = "Use dev, test, or sandbox only."
  }
}
