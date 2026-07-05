# Ansible Server Configuration

A beginner-friendly Ansible project that demonstrates repeatable Linux server configuration with an inventory, group variables, a playbook, roles, handlers, copied files, and a systemd service. The tracked inventory uses `server.example.invalid`, so it cannot identify a real server.

## What Ansible is

Ansible is an automation tool that connects to managed hosts, usually over SSH, and brings them toward a declared configuration. Playbooks are YAML files composed of tasks. Modules such as `package`, `user`, `file`, `copy`, and `systemd` describe the required state instead of relying on a long shell script.

## Project structure

```text
ansible/
  ansible.cfg
  inventory.ini.example
  playbook.yml
  group_vars/all.yml
  roles/
    common/tasks/main.yml
    app/
      tasks/main.yml
      handlers/main.yml
      files/application.conf
      files/learning-app.sh
      templates/learning-app.service.j2
docs/runbook.md
.gitignore
README.md
TESTING.md
```

## What the playbook demonstrates

If executed against a separately approved disposable Linux lab host, the playbook would:

1. Gather host facts and require Linux with systemd.
2. Install `curl` and `unzip` through the host package manager.
3. Create the `learning_app` system group and non-login user.
4. Create application, configuration, data, and log directories with explicit ownership and modes.
5. Copy a non-sensitive application configuration file.
6. Copy a harmless shell process that represents an application artifact.
7. Render a hardened example systemd unit from a template.
8. Enable and start the `learning-app` service.
9. Restart the service through a handler only when managed app files change.

The demonstration process prints a periodic heartbeat and does not open a network port. It is not a production application.

## Roles

- `common` handles packages, the system account, and shared directories.
- `app` deploys application-specific files and manages the systemd service.

Keeping responsibilities in roles makes tasks easier to read and reuse. Shared non-sensitive settings are in `group_vars/all.yml`.

## Inventory safety

`inventory.ini.example` contains only `server.example.invalid`, the placeholder user `lab_user`, and a placeholder SSH-key path. The configured `inventory.ini` is ignored and does not exist by default, so an accidental command has no real target.

For a future approved lab, copy the example to `inventory.ini` and replace the placeholders with details for a disposable host. Do not commit real hostnames, IP addresses, passwords, or private keys. `become_ask_pass = True` requests a sudo password interactively instead of storing it.

## Idempotency

Idempotency means repeated runs should converge on the same result without repeating unnecessary changes:

- `package` uses `state: present`, not an unconditional install command.
- `group`, `user`, and `file` declare their required state and attributes.
- `copy` and `template` compare content before reporting a change.
- `systemd` declares the service enabled and started.
- The restart handler runs only when copied or templated application files change.

On a stable approved host, a second run should normally report zero changed tasks. That claim must be verified in a real lab; this playbook was not executed.

## Normal learning workflow

The following commands show how a future lab review would normally proceed from the `ansible/` directory. None were executed here.

```text
ansible-inventory --graph
ansible-playbook playbook.yml --syntax-check
ansible-playbook playbook.yml --list-tasks
ansible-playbook playbook.yml --check --diff --limit app-lab-01
ansible-playbook playbook.yml --limit app-lab-01
```

Check mode predicts many changes but cannot guarantee that every package or service operation will behave exactly like a real run. Review [docs/runbook.md](docs/runbook.md) before any approved execution.

## Host assumptions

- A disposable Linux lab machine using systemd
- Python available for Ansible modules
- A supported package manager containing `curl` and `unzip`
- An approved SSH user and key stored outside the repository
- Sudo access reviewed and entered interactively
- `/usr/sbin/nologin` available for the service account

## Limitations

- Ansible was not installed or run.
- No inventory was parsed and no YAML syntax check was performed by Ansible.
- No SSH, sudo, package, user, directory, file, or service operation occurred.
- Idempotency and check-mode behavior remain unverified.
- The systemd hardening options may need adjustment for another Linux distribution.
- No successful playbook execution is claimed.
