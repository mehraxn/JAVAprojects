# Negative test: reserved label keys must be rejected in additional_labels.
run "rejects_reserved_label_key" {
  command = plan

  variables {
    project_name = "iac-starter"
    environment  = "dev"
    components   = ["web"]
    additional_labels = {
      project = "override-attempt"
    }
  }

  expect_failures = [
    var.additional_labels,
  ]
}
