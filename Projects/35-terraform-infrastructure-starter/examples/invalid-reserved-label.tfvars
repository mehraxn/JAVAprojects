# Intentionally INVALID: "project" is a reserved label key controlled by the
# module (managed_by, project, environment, purpose, component).
# Terraform must reject this file.
project_name = "iac-starter"
environment  = "dev"
components   = ["web"]

additional_labels = {
  project = "override-attempt"
}
