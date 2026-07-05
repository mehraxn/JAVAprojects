output "configuration_summary" {
  description = "Non-sensitive description of the local learning environment."
  value       = module.learning_environment.summary
}

output "component_names" {
  description = "Sorted component names represented by built-in terraform_data resources."
  value       = module.learning_environment.component_names
}
