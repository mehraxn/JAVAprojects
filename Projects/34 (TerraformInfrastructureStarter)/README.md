# Terraform Infrastructure Starter

A safe Infrastructure as Code learning project that models an imaginary application environment with Terraform's built-in `terraform_data` resource. It has no cloud provider, external provider plugin, provisioner, credential, or real infrastructure target.

## What Terraform is

Terraform is a declarative Infrastructure as Code tool. Configuration describes a desired state, and Terraform normally compares that configuration with recorded state and provider APIs to propose changes. This project stops at a local learning model: applying it would only record structured `terraform_data` values in a local state file.

## Structure

```text
terraform/
  versions.tf
  main.tf
  variables.tf
  outputs.tf
  terraform.tfvars.example
  modules/
    learning_environment/
      main.tf
      variables.tf
      outputs.tf
docs/architecture.md
.gitignore
README.md
TESTING.md
```

## Terraform file roles

- `versions.tf` sets the supported Terraform CLI version and documents that no external provider is required.
- `main.tf` contains the root module logic and calls the reusable learning module.
- `variables.tf` declares typed inputs, descriptions, defaults, and validation rules.
- `outputs.tf` exposes useful non-sensitive results from the module.
- `terraform.tfvars.example` demonstrates input values that a learner can copy and customize.
- `modules/learning_environment/` groups reusable resources, inputs, and outputs behind a small interface.

Terraform automatically reads all `.tf` files in one directory as a single module; filenames are an organization convention rather than execution order.

## What modules are

A module is a collection of Terraform configuration files used together. The `terraform/` directory is the root module. It calls the child module in `modules/learning_environment`, passing project, environment, component, and label values. Modules help keep repeated infrastructure definitions consistent, but they should remain small and clearly documented.

## Learning model

The root module defines a project such as `iac-starter`, a non-production environment such as `dev`, and imaginary components such as `frontend`, `backend`, and `database`. The child module represents those values with built-in `terraform_data` resources and returns a sorted summary.

No resource creates a server, database, network, file, cloud account object, or external command.

## Safety decisions

- No AWS, Azure, Google Cloud, or other provider block is present.
- No external provider or provider token is required.
- No `local-exec` or `remote-exec` provisioner is used.
- No remote backend or production state location is configured.
- Variable validation limits names and environments to simple learning values.
- State, plan, and real `.tfvars` files are ignored by Git.
- Outputs contain only non-sensitive learning metadata.

## Why `.tfvars` files should not contain secrets

Variable files are plain text and are easy to commit accidentally. Secret values may also be copied into Terraform state, saved plans, terminal output, CI logs, or provider error messages. The example file therefore contains only non-sensitive values. Future secrets should use an approved secret-management and credential-injection approach, and state storage must be protected as sensitive data.

## Commands normally used

The following illustrates a normal workflow from the `terraform/` directory. None of these commands were executed for this project.

```text
terraform fmt -recursive
terraform init
terraform validate
terraform plan -var-file="terraform.tfvars"
terraform apply -var-file="terraform.tfvars"
terraform destroy -var-file="terraform.tfvars"
```

Before planning, a learner would normally copy `terraform.tfvars.example` to the ignored `terraform.tfvars` file. Even though this example has no cloud resources, `apply` would still create or update a local Terraform state file. Review every plan before any apply operation.

## Extending the project later

Before adding real infrastructure:

1. Choose an explicitly disposable, non-production target.
2. Review provider version constraints and official documentation.
3. Understand costs, quotas, network exposure, and cleanup behavior.
4. Define secure credential injection without committing credentials.
5. Design protected remote state, access controls, locking, and recovery.
6. Add the smallest resource and review its plan before applying anything.

The current local-only module can later provide naming and labeling inputs to a separately reviewed provider module. Cloud examples are intentionally absent so this starter cannot be mistaken for deployable production infrastructure.

## Documentation

- [Architecture and data flow](docs/architecture.md)
- [Static and deferred testing checklist](TESTING.md)

## Limitations

- Terraform was not installed or invoked.
- Formatting, initialization, validation, planning, applying, and destroying were not performed.
- No state or plan file was generated.
- The configuration was reviewed statically but is not claimed to have passed Terraform validation.
