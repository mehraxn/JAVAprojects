# Testing — Terraform Infrastructure Starter

Exact commands to validate this project. Results actually observed are
recorded honestly in [TEST_RESULTS.md](TEST_RESULTS.md). Commands run from
the project root, entering the `terraform/` folder first.

No local Terraform? Every command below also works via the official image
(the recorded results used exactly this):

```bash
docker run --rm -v "$PWD:/w" -w /w/terraform hashicorp/terraform:1.9 <command>
```

## A) Format check

```bash
cd terraform
terraform fmt -check -recursive
```

Expected: exit 0, no files listed.

## B) Init

```bash
terraform init
```

Expected: succeeds offline — only the built-in provider and the local child
module are involved; nothing is downloaded.

## C) Validate

```bash
terraform validate
```

Expected: `Success! The configuration is valid.`

## D) Plan with example values

```bash
terraform plan -var-file="terraform.tfvars.example"
```

Expected: `Plan: 4 to add, 0 to change, 0 to destroy.` — one
`terraform_data.environment` plus one `terraform_data.component` per default
component. No provider credentials are requested; nothing external is
touched.

## E) Optional local apply/destroy

Safe only because every resource is a local-only `terraform_data`:

```bash
terraform apply -var-file="terraform.tfvars.example"
terraform destroy -var-file="terraform.tfvars.example"
```

Optional; this writes/removes a local `terraform.tfstate` (gitignored).

## F) Negative validation tests

Each of these must **fail** with a clear `Invalid value for variable` error:

```bash
terraform plan -var-file="../examples/invalid-environment.tfvars"
# fails: "production" is not in [dev, staging, prod]

terraform plan -var-file="../examples/invalid-project-name.tfvars"
# fails: project_name ends with a hyphen

terraform plan -var-file="../examples/invalid-component.tfvars"
# fails: component name "web-" ends with a hyphen

terraform plan -var-file="../examples/invalid-reserved-label.tfvars"
# fails: additional_labels sets the reserved key "project"
```

A negative test "passes" when Terraform correctly rejects the input.

## G) terraform test (native test suite)

```bash
terraform test
```

Expected after the final polish: `Success! 6 passed, 0 failed.` The suite in `terraform/tests/`
contains one apply-based positive test (asserting outputs — plan-only cannot
see `terraform_data` outputs, and the framework destroys its temporary state)
and five negative run blocks using `expect_failures` on the variables: invalid
environment, invalid project name with trailing hyphen, invalid uppercase
project name, invalid component name, and reserved additional label key.

## H) Cleanup

```bash
rm -rf .terraform
rm -f .terraform.lock.hcl
rm -f terraform.tfstate terraform.tfstate.backup
rm -f *.tfplan
```

The lock file is deliberately **not committed** (see `.gitignore`): with only
Terraform's built-in provider there are no dependencies for it to pin.
