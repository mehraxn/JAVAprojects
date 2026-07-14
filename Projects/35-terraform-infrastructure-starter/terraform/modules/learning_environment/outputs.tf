output "summary" {
  description = "Structured values from the local-only environment model."
  value = {
    project_name = terraform_data.environment.output.project_name
    environment  = terraform_data.environment.output.environment
    labels       = terraform_data.environment.output.labels
    components   = sort([for component in terraform_data.component : component.output.name])
  }
}

output "component_names" {
  description = "Stable, sorted list of modeled component names."
  value       = sort([for component in terraform_data.component : component.output.name])
}
