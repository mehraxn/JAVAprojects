# Testing Terraform Infrastructure Starter

Terraform was not installed or executed. No formatting, initialization, validation, plan, apply, destroy, provider, or state command was run.

## Static validation checklist

- [ ] Confirm braces, blocks, references, and module paths are internally consistent.
- [ ] Confirm variable defaults satisfy their own validation rules.
- [ ] Confirm standard labels cannot be overridden unexpectedly.
- [ ] Confirm component output ordering is stable.
- [ ] Confirm all resources use the built-in `terraform_data` type.

## File existence checks

- [ ] Root `main.tf`, `variables.tf`, `outputs.tf`, and `versions.tf` exist.
- [ ] `terraform.tfvars.example` exists.
- [ ] Child-module `main.tf`, `variables.tf`, and `outputs.tf` exist.
- [ ] `docs/architecture.md`, `.gitignore`, `README.md`, and `TESTING.md` exist.

## Configuration review checklist

- [ ] Required Terraform version is deliberate.
- [ ] No external provider requirement exists.
- [ ] Root module passes every required child-module input.
- [ ] Output references match child-module output names.
- [ ] State, plans, and real `.tfvars` files are ignored.
- [ ] Example variables describe only dev/test/sandbox learning values.

## Security checks

- [ ] No real secret, credential, provider token, or account ID is present.
- [ ] No production endpoint, region, subscription, or project identifier is present.
- [ ] No `local-exec` or `remote-exec` provisioner is present.
- [ ] Outputs contain no sensitive material.

## Commands normally used - NOT executed

```text
terraform fmt -check -recursive
terraform init
terraform validate
terraform plan -var-file="terraform.tfvars"
terraform apply -var-file="terraform.tfvars"
terraform destroy -var-file="terraform.tfvars"
```

The apply/destroy commands are shown only to explain the normal lifecycle. They were not run and require separate approval even for local-only state.

## Expected results in a proper environment

- Formatting and validation accept the configuration.
- Initialization uses only Terraform's built-in provider and local child module.
- A plan proposes only `terraform_data` resources.
- Invalid environment, project, component, or label values fail validation clearly.
- An approved apply creates only local state records and no cloud or external resource.
- A subsequent unchanged plan reports no infrastructure changes.
