output "network_id" {
  description = "Deterministic placeholder network id (model only)."
  value       = "vpc-${var.project_name}-${var.environment}"
}

output "subnet_ids" {
  description = "Deterministic placeholder subnet ids (model only)."
  value       = [for k, _ in terraform_data.subnet : "subnet-${var.project_name}-${var.environment}-${k}"]
}
