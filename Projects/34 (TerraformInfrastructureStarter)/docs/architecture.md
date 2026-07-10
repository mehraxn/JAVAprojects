# Architecture

## Configuration flow

```text
terraform.tfvars (local, ignored)
          |
          v
root module: terraform/
  variables.tf -> main.tf -> outputs.tf
                       |
                       v
child module: modules/learning_environment
  terraform_data.environment
  terraform_data.component["backend"]
  terraform_data.component["database"]
  terraform_data.component["frontend"]
                       |
                       v
local Terraform state only if a future apply is approved
```

The configuration has no connection to a cloud API, virtualization platform, container runtime, or remote machine.

## Root module

The root module owns the learner-facing variables and their validation. It merges standard learning labels with optional labels and passes a small, explicit interface to the child module. Root outputs expose the child module's non-sensitive summary.

## Child module

`learning_environment` demonstrates module inputs, `for_each`, built-in resources, and module outputs. One `terraform_data` resource stores environment metadata, while one resource per component stores the component model. These resources have no external side effects.

## State boundary

Terraform still uses state for built-in resources. A future local apply would write state under the working directory unless a backend is configured. State files are ignored here, but ignoring them is not a complete security strategy: future real projects need protected storage, access control, encryption, locking, backup, and recovery decisions.

## Dependency boundary

`terraform_data` is part of Terraform's built-in provider, so this configuration declares no external providers. `terraform init` initializes the working directory and local child module without downloading anything — verified in [../TEST_RESULTS.md](../TEST_RESULTS.md).

## Safe extension boundary

Real provider configuration should not be added casually to this starter. A separate review should establish:

- disposable target account or local sandbox;
- provider and module version constraints;
- cost and quota limits;
- credential and secret handling;
- remote-state protection;
- network exposure;
- cleanup and recovery steps; and
- approval before applying.

Until those decisions exist, the architecture should remain local-only.

## Verification status

Terraform formatting, initialization, validation, an example plan
(`Plan: 4 to add`), four negative validation plans, and the native
`terraform test` suite (5 passed at the time of that run) were actually run with Terraform v1.9.8 —
see [../TEST_RESULTS.md](../TEST_RESULTS.md). The final package also adds a native invalid-component test; rerun `terraform test` to record the final six-run result. A standalone `terraform apply`
outside the test framework was not run; no cloud API can be reached by this
configuration in any case.
