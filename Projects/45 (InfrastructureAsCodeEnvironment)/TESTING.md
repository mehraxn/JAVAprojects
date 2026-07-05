# Testing Infrastructure as Code Environment

## Static checks

- [ ] Confirm Terraform contains no cloud provider or external provisioner.
- [ ] Confirm Ansible inventory contains only `example.invalid`.
- [ ] Confirm outputs and variables contain no secret or account identifier.
- [ ] Review the future provisioning-to-configuration handoff.

## Deferred checks

- [ ] Format and validate Terraform after approval.
- [ ] Parse and syntax-check Ansible after approval.
- [ ] Use only a disposable environment with explicit teardown ownership.

No Terraform, Ansible, cloud, SSH, or configuration command was executed.
