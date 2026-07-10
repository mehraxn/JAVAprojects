# Test Results — Terraform Infrastructure Starter

Date: 2026-07-10. Host: Windows 11. Terraform is not installed on the host,
so all commands ran via the official `hashicorp/terraform:1.9` Docker image
(**Terraform v1.9.8**) — same commands, real results.

## Static and Terraform validation

| Check | Result | Notes |
|---|---:|---|
| `terraform fmt -check -recursive` | PASS | exit 0, including modules/ and tests/ |
| `terraform init` | PASS | built-in provider + local module only; nothing downloaded |
| `terraform validate` | PASS | `Success! The configuration is valid.` |
| `terraform plan -var-file=terraform.tfvars.example` | PASS | `Plan: 4 to add, 0 to change, 0 to destroy` — `terraform_data.environment` + 3 components |

## Negative validation tests

A negative test PASSES when Terraform correctly rejects the invalid input.

| Check | Expected | Result | Notes |
|---|---|---:|---|
| `examples/invalid-environment.tfvars` | FAIL | PASS | exit 1, `Invalid value for variable` — "production" rejected |
| `examples/invalid-project-name.tfvars` | FAIL | PASS | exit 1 — trailing hyphen rejected |
| `examples/invalid-component.tfvars` | FAIL | PASS | exit 1 — component "web-" rejected |
| `examples/invalid-reserved-label.tfvars` | FAIL | PASS | exit 1 — reserved key `project` rejected |

## terraform test

| Check | Result | Notes |
|---|---:|---|
| `terraform test` | PASS | **Historical recorded run:** `Success! 5 passed, 0 failed.` — 1 apply-based positive run + 4 negative runs via `expect_failures` before the final invalid-component native test was added. |
| final `terraform test` after adding `invalid_component.tftest.hcl` | NOT RUN HERE | Terraform/Docker are unavailable in this assistant environment. Re-run `terraform test`; expected final suite size is 6 runs. |

Note: the positive test uses `command = apply` because `terraform_data`
outputs are unknown at plan time; the test framework destroyed its temporary
state automatically. A first draft with `command = plan` failed its assertion
for exactly that reason and was corrected.

## Optional commands

| Check | Result | Notes |
|---|---:|---|
| `terraform apply` | NOT RUN | optional local-only exercise (state was exercised indirectly by the apply-based test) |
| `terraform destroy` | NOT RUN | optional local-only exercise |

## Known limitations

- No cloud provider configured; no backend configured; no real
  infrastructure was or can be created by this configuration.
- No secrets are managed anywhere in the project.
- `terraform_data` resources exist purely in local state, used for safe
  learning.
- The dependency lock file is intentionally not committed (nothing to pin);
  generated files (`.terraform/`, state) were removed after validation.
- Results are a point-in-time snapshot of one validation run with Terraform
  v1.9.8; newer versions may format or word errors differently.
- The final package adds `terraform/tests/invalid_component.tftest.hcl` for native
  coverage of the component-name validation. Because Terraform/Docker are not
  available in this assistant environment, rerun `terraform test` before claiming
  the final six-run suite result in a commit message.
