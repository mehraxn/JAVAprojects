# Lab Runbook

This runbook describes a possible future exercise. It was not followed during implementation, and no command or server connection occurred.

## 1. Approve the lab boundary

Before editing the inventory, identify a disposable Linux virtual machine that may be reconfigured. Confirm ownership, expected cost, network access, cleanup responsibility, supported package manager, Python availability, systemd availability, SSH access, and sudo policy.

Do not use a production server, shared environment, personal workstation, or unapproved cloud instance.

## 2. Prepare inventory safely

From `ansible/`, copy `inventory.ini.example` to `inventory.ini`. Replace:

- `server.example.invalid` with only the approved disposable host;
- `lab_user` with its approved SSH user; and
- the placeholder key path with a private key stored outside this repository.

Keep `inventory.ini` uncommitted. Do not place SSH passwords, sudo passwords, private-key contents, or provider credentials in inventory or variables.

## 3. Review configuration

Review these files before any connection:

- `group_vars/all.yml` for packages, account names, and paths;
- `roles/common/tasks/main.yml` for host-level changes;
- `roles/app/tasks/main.yml` for copied files and service actions;
- `roles/app/templates/learning-app.service.j2` for service permissions; and
- the approved host limit.

The defaults assume Linux, systemd, `/usr/sbin/nologin`, and package names `curl` and `unzip`.

## 4. Perform local preflight checks

Typical commands, not executed here:

```text
ansible-inventory --graph
ansible-playbook playbook.yml --syntax-check
ansible-playbook playbook.yml --list-hosts
ansible-playbook playbook.yml --list-tasks
```

Stop if an unexpected host, group, variable, or task appears.

## 5. Review check mode

Use an explicit host limit:

```text
ansible-playbook playbook.yml --check --diff --limit app-lab-01
```

Review all proposed package, user, directory, file, and service changes. Check mode is useful evidence but does not guarantee a real run will behave identically.

## 6. Execute only after separate approval

The normal command would be:

```text
ansible-playbook playbook.yml --limit app-lab-01
```

The configuration requests the sudo password interactively. Never add that password to a file or command argument.

## 7. Verify the disposable host

Verify the play recap and then inspect:

- the `learning_app` group and non-login user;
- directories under `/opt`, `/etc`, `/var/lib`, and `/var/log`;
- `/etc/learning-app/application.conf` permissions;
- `/etc/systemd/system/learning-app.service`;
- service status and recent journal messages; and
- absence of an unexpected listening network port.

## 8. Verify idempotency

Run the unchanged playbook a second time against the same approved host. The expected steady-state recap is `changed=0`. A changing source file should update only its destination and notify one service restart.

## 9. Cleanup and rollback

This starter intentionally does not automate destructive removal. For a disposable host, prefer deleting the entire lab machine after collecting learning notes. If manual rollback is required, review and stop the service before removing its unit, files, directories, user, or group. Do not remove shared packages without checking other consumers.

## Troubleshooting guide

- **Inventory cannot resolve:** expected while using `server.example.invalid`; configure only an approved lab host.
- **Host-key failure:** verify the host identity and known-hosts entry; do not disable checking as a shortcut.
- **Sudo failure:** confirm the approved account policy; do not store a sudo password.
- **Package not found:** adapt `common_packages` to the lab distribution after review.
- **Platform assertion fails:** use a supported Linux systemd lab rather than bypassing the assertion.
- **Service restart loop:** inspect the copied configuration, script permissions, and system journal before retrying.

## Verification status

This runbook is documentation only. Its inventory, commands, connections, changes, verification steps, idempotency check, and cleanup steps were not executed.
