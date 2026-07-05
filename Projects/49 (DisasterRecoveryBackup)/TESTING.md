# Testing Disaster Recovery and Backup

## Static checks

- [ ] Confirm CronJob is suspended and contains only placeholders.
- [ ] Confirm no real database endpoint, credential, bucket, or account exists.
- [ ] Confirm runbooks distinguish backup success from restore success.
- [ ] Confirm RPO/RTO are targets, not achieved claims.

## Deferred checks

- [ ] Validate backup artifacts in an isolated destination.
- [ ] Perform a full restore into a disposable environment.
- [ ] Measure recovery time and data loss against approved targets.

No backup, restore, Kubernetes, Terraform, Ansible, database, or cloud command was executed.
