# Negative tests: malformed project names must fail variable validation.
run "rejects_trailing_hyphen" {
  command = plan

  variables {
    project_name = "iac-starter-"
    environment  = "dev"
    components   = ["web"]
  }

  expect_failures = [
    var.project_name,
  ]
}

run "rejects_uppercase" {
  command = plan

  variables {
    project_name = "IacStarter"
    environment  = "dev"
    components   = ["web"]
  }

  expect_failures = [
    var.project_name,
  ]
}
