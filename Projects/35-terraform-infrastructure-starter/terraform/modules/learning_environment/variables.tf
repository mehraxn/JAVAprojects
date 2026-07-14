variable "project_name" {
  description = "Validated project name supplied by the root module."
  type        = string
}

variable "environment" {
  description = "Validated learning environment supplied by the root module."
  type        = string
}

variable "components" {
  description = "Component names represented by local-only data resources."
  type        = set(string)
}

variable "labels" {
  description = "Non-sensitive labels associated with the learning model."
  type        = map(string)
}
