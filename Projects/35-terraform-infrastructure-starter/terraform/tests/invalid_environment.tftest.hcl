# Negative test: an unknown environment value must fail variable validation.
run "rejects_invalid_environment" {
  command = plan

  variables {
    project_name = "iac-starter"
    environment  = "production"
    components   = ["web"]
  }

  expect_failures = [
    var.environment,
  ]
}
