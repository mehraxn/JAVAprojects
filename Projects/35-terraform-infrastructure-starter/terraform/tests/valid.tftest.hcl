# Positive test: valid inputs apply cleanly and produce the expected outputs.
# `command = apply` is safe here: terraform_data resources are local-only and
# the test framework destroys its temporary state afterwards. (A plan-only
# test cannot assert these outputs, because terraform_data.output is unknown
# until apply.)
run "valid_configuration" {
  command = apply

  variables {
    project_name = "iac-starter"
    environment  = "dev"
    components   = ["backend", "frontend"]
    additional_labels = {
      course = "terraform-basics"
    }
  }

  assert {
    condition     = output.component_names == tolist(["backend", "frontend"])
    error_message = "component_names should list the modeled components, sorted."
  }

  assert {
    condition     = output.configuration_summary.labels["managed_by"] == "terraform"
    error_message = "The managed_by standard label must be set by the module."
  }

  assert {
    condition     = output.configuration_summary.labels["course"] == "terraform-basics"
    error_message = "Non-reserved additional labels must be preserved."
  }
}
