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

variable "instance_count" {
  description = "Number of app nodes to model."
  type        = number
}

variable "instance_size" {
  description = "Placeholder instance-size label."
  type        = string
}

variable "subnet_ids" {
  description = "Placeholder subnet ids from the network module."
  type        = list(string)
}

variable "public_subnet_cidrs" {
  description = "Subnet CIDRs used to derive deterministic RFC 1918 private IPs (model only)."
  type        = list(string)
}

variable "ansible_user" {
  description = "SSH login user carried through to the Ansible handoff."
  type        = string
  default     = "ubuntu"
}

variable "allowed_ssh_cidrs" {
  description = "Source CIDRs allowed for SSH (documentation ranges only)."
  type        = list(string)
}

variable "labels" {
  description = "Non-sensitive labels to attach to modeled resources."
  type        = map(string)
}
