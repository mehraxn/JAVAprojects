# Terraform Infrastructure Starter

## Description

A safe Infrastructure as Code learning project that models an imaginary application environment with Terraform's built-in `terraform_data` resource. It contains no cloud provider, credential, provisioner, or production resource.

## Goal

The goal is to learn Terraform file organization, typed variables, validation, outputs, modules, state concepts, and review workflow without creating real infrastructure or requiring a provider account.

## Technologies and concepts used

- Terraform configuration language
- Root and child modules
- Typed variables and validation rules
- Local values, `for_each`, and outputs
- Built-in `terraform_data` resources
- State, plan, formatting, and validation concepts
- Source-control exclusions for local state and variable files

## Project structure

```text
terraform/
  versions.tf
  main.tf
  variables.tf
  outputs.tf
  terraform.tfvars.example
  modules/learning_environment/
    main.tf
    variables.tf
    outputs.tf
docs/architecture.md
.gitignore
README.md
TESTING.md
```

## Important files explained

- `versions.tf` defines the supported Terraform CLI range and documents the built-in-only design.
- `variables.tf` declares project, environment, component, and label inputs with validation.
- `main.tf` combines standard labels and calls the child module.
- `outputs.tf` exposes non-sensitive configuration summaries.
- `terraform.tfvars.example` contains safe example values, not credentials.
- `modules/learning_environment/` demonstrates a reusable interface and local-only resources.
- `docs/architecture.md` explains module and state boundaries.

## Intended real-environment workflow

In a controlled learning environment, a developer would review all files, copy the example variables into ignored `terraform.tfvars`, format the configuration, initialize the working directory, validate it, and inspect a plan. Applying is unnecessary for understanding the structure. If separately approved, applying this specific configuration should create only local Terraform state records for built-in data resources.

Any future cloud extension requires a separate review of provider versions, disposable accounts, costs, credentials, network exposure, remote state, locking, recovery, and cleanup.

## Prepared but not executed

- Root-module inputs/outputs and a reusable child module were prepared.
- Local-only `terraform_data` resources and validation rules were written.
- State, plan, and real `.tfvars` files were excluded from source control.
- Terraform was not installed or invoked; formatting, initialization, validation, planning, applying, and destroying were not performed.
- No state file or infrastructure was created.

## Manual validation checklist

- [ ] Confirm every resource type is `terraform_data`.
- [ ] Confirm there is no provider, backend, provisioner, or external command.
- [ ] Review variable types, validation expressions, and error messages.
- [ ] Confirm module inputs match child-module declarations.
- [ ] Confirm outputs contain no sensitive value.
- [ ] Review a future plan line by line before considering apply.
- [ ] Keep state and real variable files uncommitted.

## Common mistakes avoided

- No real cloud example can be applied accidentally.
- Credentials are not stored in `.tf` or example variable files.
- State is treated as potentially sensitive even in a learning project.
- Modules are used for a clear reusable boundary rather than unnecessary nesting.
- Applying is not presented as required for static learning.
- No successful validation, plan, or apply is claimed.

## Possible future improvements

- Add automated formatting and static-validation checks after Terraform is approved.
- Add more variable-validation examples.
- Demonstrate moved/import blocks using local-only state fixtures.
- Add a separately reviewed disposable provider module.
- Document remote-state security and recovery before any real provider is introduced.
