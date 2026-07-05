# Infrastructure as Code Environment

Starter structure combining safe local-only Terraform concepts with an Ansible configuration-management example that targets documentation-only hosts.

## Structure

```text
terraform/main.tf
terraform/variables.tf
terraform/outputs.tf
terraform/terraform.tfvars.example
ansible/inventory.ini.example
ansible/playbook.yml
ansible/roles/common/tasks/main.yml
docs/architecture.md
docs/state-and-secrets.md
README.md
TESTING.md
```

## Status

Skeleton only. Terraform has no cloud provider and Ansible performs only debug output against an example host. Nothing was initialized, planned, applied, connected, or configured.

## Required confirmations

- Disposable infrastructure target and cost/cleanup boundary
- Terraform state, locking, recovery, and credential strategy
- Approved Ansible operating system, account, privilege, and package policy
- Clear handoff between provisioning outputs and configuration inputs
