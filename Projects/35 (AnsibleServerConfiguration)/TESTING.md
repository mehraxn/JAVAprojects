# Testing Ansible Server Configuration

## Static checks

- Confirm inventory contains only example hosts.
- Confirm variables contain no credentials.
- Review YAML indentation, task names, and handler references.
- Review templates for placeholder paths and users.

## Deferred tool checks

- Run syntax and lint checks when tools are available.
- Use check mode against an approved disposable host.
- Run twice to evaluate idempotence.

No Ansible command or remote connection was executed.
