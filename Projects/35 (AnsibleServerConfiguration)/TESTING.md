# Testing Ansible Server Configuration

Ansible was not installed or executed. No inventory, syntax, check-mode, SSH, sudo, package, account, file, handler, or service operation ran.

## Static validation checklist

- [ ] Review YAML indentation and fully qualified module names.
- [ ] Confirm play hosts match the example inventory group.
- [ ] Confirm role names match role directories.
- [ ] Confirm handler notifications match the handler name exactly.
- [ ] Confirm variables referenced by tasks are declared.
- [ ] Review module state values for idempotent intent.

## File existence checks

- [ ] `ansible/ansible.cfg`, `inventory.ini.example`, and `playbook.yml` exist.
- [ ] `ansible/group_vars/all.yml` exists.
- [ ] Common and app task files exist.
- [ ] App handler, copied files, and systemd template exist.
- [ ] `docs/runbook.md`, `.gitignore`, `README.md`, and `TESTING.md` exist.

## Configuration review checklist

- [ ] Real `inventory.ini` remains ignored and absent by default.
- [ ] Roles path resolves from `ansible.cfg`.
- [ ] Package names suit the approved lab distribution.
- [ ] `/usr/sbin/nologin` exists on the target distribution.
- [ ] systemd hardening fields and writable paths suit the demo process.
- [ ] Service and file paths agree across variables, tasks, script, and template.

## Security checks

- [ ] No real secret, credential, SSH key, or sudo password is present.
- [ ] No production endpoint or real IP address is present.
- [ ] Private-key and vault-password file patterns are ignored.
- [ ] Host-key checking remains enabled.
- [ ] Application files and service use least-privilege ownership/modes.

## Commands normally used - NOT executed

```text
ansible-inventory --graph
ansible-playbook playbook.yml --syntax-check
ansible-playbook playbook.yml --list-hosts
ansible-playbook playbook.yml --list-tasks
ansible-playbook playbook.yml --check --diff --limit app-lab-01
ansible-playbook playbook.yml --limit app-lab-01
```

These commands require an approved disposable host and deliberately prepared ignored inventory. None were executed.

## Expected results in a proper environment

- Inventory and syntax checks show only the intended lab host and tasks.
- Unsupported operating systems stop at the platform assertion.
- Packages, system account, directories, configuration, script, and unit reach their declared states.
- The service is enabled and active.
- Changed app files trigger one restart handler.
- A second unchanged playbook run normally reports `changed=0`.
