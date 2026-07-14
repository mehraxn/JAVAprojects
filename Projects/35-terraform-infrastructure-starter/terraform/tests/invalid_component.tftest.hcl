# Negative test: malformed component names must fail variable validation.
run "rejects_invalid_component_name" {
  command = plan

  variables {
    project_name = "iac-starter"
    environment  = "dev"
    components   = ["web-"]
  }

  expect_failures = [
    var.components,
  ]
}
