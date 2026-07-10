# Ansible Server Configuration

*An Ansible server-configuration lab for deploying a small Linux service —
example inventory, role-based setup, non-root service user, least-privilege
file ownership, a hardened systemd unit, handler-driven restarts, and a
locally validated syntax/list workflow.*

## What this project is

A beginner-friendly Ansible control project that configures a Linux host for
a demo systemd service. All hosts and credentials are documentation-safe
placeholders; the playbook's structure, syntax, and task list were validated
with real Ansible commands (see [TEST_RESULTS.md](TEST_RESULTS.md)).

## What it demonstrates

- **Inventory structure** (`inventory.ini.example`, TEST-NET-1 documentation
  address, copy-before-use workflow)
- **ansible.cfg** with host-key checking kept ON and interactive sudo
- **Playbook + roles + group variables + handlers**
- **common role**: packages, system group, non-login system user, and a
  **least-privilege directory layout** — root owns `/opt/learning-app`,
  `/opt/learning-app/bin`, and `/etc/learning-app`; the service account owns
  only `/var/lib/learning-app` and `/var/log/learning-app`
- **app role**: config file (root-owned, group-readable `0640`), executable
  (root-owned, group-executable `0750`), systemd unit template, and
  enable/start — with restarts **notified only when content changes**
- **systemd hardening baseline**: `NoNewPrivileges`, `PrivateTmp`,
  `ProtectSystem=strict`, `ProtectHome`, explicit `ReadWritePaths`, empty
  `CapabilityBoundingSet`, `LockPersonality` — a local demo baseline, not a
  universal production profile
- **Local validation** (inventory graph, syntax-check, list-hosts,
  list-tasks) and a documented optional **check-mode / idempotency** workflow

## The permission model in one picture

```
/opt/learning-app        root:learning_app 0755   code (service can't replace it)
/opt/learning-app/bin    root:learning_app 0755
  └── learning-app       root:learning_app 0750   executable, group-exec only
/etc/learning-app        root:learning_app 0750   config (service can't modify it)
  └── application.conf   root:learning_app 0640   group-readable
/var/lib/learning-app    learning_app:...  0750   runtime data (writable)
/var/log/learning-app    learning_app:...  0750   logs (writable)
/etc/systemd/system/learning-app.service  root:root 0644
```

The service user can execute the app, read its config, and write its own
data/logs — and cannot replace the binary, edit config, or touch unit files.

## Quick validation (no server needed)

```bash
cd ansible
ansible-inventory -i inventory.ini.example --graph
ansible-playbook -i inventory.ini.example playbook.yml --syntax-check
ansible-playbook -i inventory.ini.example playbook.yml --list-hosts
ansible-playbook -i inventory.ini.example playbook.yml --list-tasks
```

All commands run from the `ansible/` folder (that's where `ansible.cfg`
lives). Full command list: [TESTING.md](TESTING.md). Real-host runbook:
[docs/runbook.md](docs/runbook.md).

## What is implemented (and validated)

Role-based configuration, package installation, user/group creation,
directory management, app file deployment, config templating, systemd unit
installation, and service enable/start. On 2026-07-10 the following passed
with ansible-core 2.21.1: YAML parse of every file, shell syntax check of the
demo script, `ansible-inventory --graph`, `--syntax-check`, `--list-hosts`,
and `--list-tasks` (all 12 tasks resolve). See
[TEST_RESULTS.md](TEST_RESULTS.md).

## What is not production-grade

- **No real host included** — the inventory is documentation-only, and no
  SSH/sudo/systemd operation was performed; check mode and the real playbook
  run are documented but **not executed**.
- **Idempotent design, not proven idempotency** — every task uses declarative
  modules with `state:` values, but real idempotency is only demonstrated by
  running twice on a disposable host (workflow in the runbook).
- No secrets, SSH keys, or cloud provisioning anywhere.
- The systemd hardening is a demo baseline, not a CIS-style benchmark.

## How to validate

[TESTING.md](TESTING.md) for commands, [TEST_RESULTS.md](TEST_RESULTS.md) for
what actually ran, [docs/runbook.md](docs/runbook.md) for the optional
disposable-host exercise.
