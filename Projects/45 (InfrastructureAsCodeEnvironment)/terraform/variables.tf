variable "project_name" {
  description = "Non-sensitive project label applied to all modeled resources."
  type        = string
  default     = "iac-lab"
}

variable "environment" {
  description = "Which environment this root represents."
  type        = string
  default     = "sandbox"

  validation {
    condition     = contains(["sandbox", "dev", "prod"], var.environment)
    error_message = "environment must be one of: sandbox, dev, prod."
  }
}

# Placeholder region string. It is NOT passed to any provider; it only labels
# the modeled resources so the design reads realistically.
variable "region" {
  description = "Placeholder region label (not sent to any cloud provider)."
  type        = string
  default     = "example-region-1"
}

variable "vpc_cidr" {
  description = "Network CIDR. Uses an RFC 1918 private range for the model only."
  type        = string
  default     = "10.20.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "Subnet CIDRs carved from vpc_cidr (model values only)."
  type        = list(string)
  default     = ["10.20.1.0/24", "10.20.2.0/24"]
}

variable "instance_count" {
  description = "How many app nodes to model."
  type        = number
  default     = 1

  validation {
    condition     = var.instance_count >= 1 && var.instance_count <= 10
    error_message = "instance_count must be between 1 and 10."
  }
}

variable "instance_size" {
  description = "Placeholder instance-size label (e.g. small/medium/large)."
  type        = string
  default     = "small"
}

# Which source ranges *would* be allowed to reach the app. Defaults to the
# RFC 5737 TEST-NET-1 documentation range — a reserved, non-routable placeholder,
# never a real address.
variable "allowed_ssh_cidrs" {
  description = "Source CIDRs allowed for SSH (RFC 5737 documentation ranges only)."
  type        = list(string)
  default     = ["192.0.2.0/24"]
}

variable "additional_labels" {
  description = "Extra non-sensitive labels to merge onto every resource."
  type        = map(string)
  default     = {}
}

# The login user Ansible would use on each host. Non-sensitive; carried through to
# the ansible_hosts handoff so the generated inventory sets ansible_user.
variable "ansible_user" {
  description = "SSH login user for the modeled hosts (used in the Ansible handoff)."
  type        = string
  default     = "ubuntu"
}
