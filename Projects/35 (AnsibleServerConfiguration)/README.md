# Ansible Server Configuration

## Description

A beginner-friendly Ansible learning project demonstrating repeatable Linux server configuration with an example inventory, group variables, a playbook, roles, copied files, a systemd template, and a restart handler.

## Goal

The goal is to understand how Ansible declares desired server state—packages installed, accounts present, directories configured, files deployed, and services enabled—while keeping all tracked hosts and credentials non-real.

## Technologies and concepts used

- Ansible inventory and configuration
- YAML playbooks and facts
- Roles, group variables, tasks, templates, files, and handlers
- Package, group, user, file, copy, template, assert, and systemd modules
- Privilege escalation and host-key-checking concepts
- Idempotency and check-mode review

## Project structure

```text
ansible/
  ansible.cfg
  inventory.ini.example
  playbook.yml
  group_vars/all.yml
  roles/common/tasks/main.yml
  roles/app/tasks/main.yml
  roles/app/handlers/main.yml
  roles/app/files/
  roles/app/templates/
docs/runbook.md
.gitignore
README.md
TESTING.md
```

## Important files explained

- `inventory.ini.example` uses `server.example.invalid`, a placeholder user, and a placeholder key path.
- `ansible.cfg` points to an ignored real inventory and keeps host-key checking enabled.
- `playbook.yml` validates Linux/systemd facts and applies the common and app roles.
- `group_vars/all.yml` contains non-sensitive package, account, path, and service defaults.
- The `common` role installs packages and manages the system account and directories.
- The `app` role copies configuration and a harmless demo process, renders a systemd unit, and starts/enables it.
- `docs/runbook.md` describes approval, check mode, execution, verification, idempotency, and cleanup.

## Intended real-environment workflow

For an approved disposable Linux lab, copy `inventory.ini.example` to ignored `inventory.ini`, replace placeholders with the reviewed host/user/key path, inspect all variables and tasks, parse the inventory, perform syntax/list checks, and run check mode with an explicit host limit. A real playbook run should happen only after reviewing predicted package, account, file, and service changes.

The default configuration requests the sudo password interactively rather than storing it.

## Prepared but not executed

- Inventory example, playbook, variables, two roles, app files, service template, and handler were prepared.
- Tasks demonstrate package installation, user/group creation, directories, file copying, service start, and conditional restart.
- Ansible was not installed or run; no YAML was parsed by Ansible, no inventory resolved, and no SSH/sudo connection occurred.
- No host, package, user, directory, file, or service was changed.

## Manual validation checklist

- [ ] Confirm the inventory contains only the approved disposable host.
- [ ] Review gathered-fact assumptions and platform assertion.
- [ ] Review every package, path, owner, group, and mode.
- [ ] Confirm app file changes notify one matching restart handler.
- [ ] Use `--limit`, `--check`, and `--diff` before a real run.
- [ ] Verify the service account cannot log in interactively.
- [ ] Run twice in a lab and investigate any second-run change.

## Common mistakes avoided

- No real IP address, hostname, SSH key, or password is tracked.
- Host-key checking is not disabled for convenience.
- The application service does not run as root.
- Package state is `present`, not an unconditional shell installation.
- Copy/template modules drive restart notification only when content changes.
- Check mode is not described as a guarantee of real execution behavior.

## Possible future improvements

- Add distribution-specific package mappings.
- Add Molecule-style role tests only after external tooling is approved.
- Add explicit rollback tasks for a disposable lab.
- Add vault/external-secret documentation without committing secret material.
- Add CI syntax/lint checks after the project is placed in an executable environment.
