# Terraform Infrastructure Starter

*A safe, local-only Terraform Infrastructure-as-Code starter — root + child
module, typed variables with strict validation, safe `terraform_data`
resources, negative validation examples, a native `terraform test` suite, and
real fmt/init/validate/plan/test evidence.*

## What this project is

A Terraform learning starter that models an imaginary application environment
using only Terraform's built-in `terraform_data` resource. **No cloud
resources are created** — there is no provider, backend, credential, or
provisioner anywhere — yet the full Terraform workflow (fmt, init, validate,
plan, test, and even a safe local apply) works for real. All recorded results
were actually run: see [TEST_RESULTS.md](TEST_RESULTS.md).

## What it demonstrates

- **Root module + reusable child module** (`modules/learning_environment`)
- **Typed variables with strict validation**:
  - `project_name`: 3–40 chars, `^[a-z][a-z0-9-]*[a-z0-9]$` — lowercase
    letters/numbers/hyphens, starts with a letter, no trailing hyphen
  - `environment`: only `dev`, `staging`, or `prod`
  - `components`: each name 2–30 chars with the same shape rules
  - `additional_labels`: no empty keys/values, and **reserved keys are
    rejected** (`managed_by`, `project`, `environment`, `purpose`,
    `component`) — standard labels are controlled by the module;
    `additional_labels` is only for non-reserved custom metadata
- **Safe local-only `terraform_data` resources** with `for_each`
- **Non-sensitive outputs** (configuration summary + sorted component names)
- **Safe tfvars example** and four **intentionally-invalid examples** under
  `examples/` that variable validation must reject
- **Native `terraform test` suite** (`terraform/tests/`) — one apply-based
  positive test asserting outputs, plus five negative tests using
  `expect_failures`
- **.gitignore** for state, plans, and real tfvars (the example and the
  invalid test files stay tracked)

## Why terraform_data?

`terraform_data` is part of Terraform itself: it records values in local
state without calling any cloud API or running any command. That lets this
project exercise the complete plan/apply/destroy lifecycle — including state
concepts — with zero cost, zero credentials, and zero risk.

## Project structure

```text
terraform/
  versions.tf  main.tf  variables.tf  outputs.tf
  terraform.tfvars.example
  modules/learning_environment/       main/variables/outputs
  tests/                              *.tftest.hcl (terraform test)
examples/                             intentionally-invalid tfvars
docs/architecture.md
README.md  TESTING.md  TEST_RESULTS.md
```

## Quick validation

```bash
cd terraform
terraform fmt -check -recursive
terraform init
terraform validate
terraform plan -var-file="terraform.tfvars.example"   # Plan: 4 to add
terraform test                                        # expected after final polish: 6 passed, 0 failed

# each of these must FAIL with a clear validation error:
terraform plan -var-file="../examples/invalid-environment.tfvars"
terraform plan -var-file="../examples/invalid-project-name.tfvars"
terraform plan -var-file="../examples/invalid-component.tfvars"
terraform plan -var-file="../examples/invalid-reserved-label.tfvars"
```

Full command list with expected results: [TESTING.md](TESTING.md). The recorded Terraform run in [TEST_RESULTS.md](TEST_RESULTS.md) was done before the final native invalid-component test was added, so rerun `terraform test` once Terraform is available to record the final six-test result.

## What is intentionally not included

- **No cloud provider** (`aws`/`azurerm`/`google`/…) and no provider blocks
- **No backend configuration** — state would be local-only
- **No real credentials or secrets** of any kind
- **No provisioners** (`local-exec`/`remote-exec`)
- **No production infrastructure creation** — the `prod` environment value is
  just a label on local data

The dependency lock file is not committed: with only the built-in provider it
pins nothing meaningful (documented in `.gitignore`).

## How to validate

[TESTING.md](TESTING.md) for commands, [TEST_RESULTS.md](TEST_RESULTS.md) for
the honestly recorded results of the 2026-07-10 run (Terraform v1.9.8),
[docs/architecture.md](docs/architecture.md) for the module and state
boundaries.

## Resume Value

Created a provider-free Terraform starter with reusable modules, typed variables, deterministic outputs, native tests, and safe positive/negative validation workflows.

## Possible future improvements

- CI job running fmt/validate/test on every push
- `moved`/`import` block demonstrations with local-only state fixtures
- A separately reviewed disposable-provider module (cost, credentials, remote
  state, and cleanup all need their own review first)
