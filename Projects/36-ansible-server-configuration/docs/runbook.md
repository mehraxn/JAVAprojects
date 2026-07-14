# Lab Runbook

The optional disposable-host exercise. The local validation in
[../TESTING.md](../TESTING.md) needs no server; everything below does — and
none of it was executed for this repository (see
[../TEST_RESULTS.md](../TEST_RESULTS.md)).

## 1. Prerequisites and lab boundary

You need: a **disposable** Linux VM with systemd that you own and may
reconfigure (never a production server, shared environment, or personal
workstation), SSH access with a key pair stored **outside this repository**,
a sudo-capable user, and Python on the target. The defaults also assume
`/usr/sbin/nologin` exists and the package manager knows `ca-certificates`
and `curl`.

## 2. Prepare inventory safely

```bash
cd ansible
cp inventory.ini.example inventory.ini
```

Edit `inventory.ini` (never the `.example`): replace the documentation
address `192.0.2.10` with your lab host, set the SSH user, and reference your
private-key path. `inventory.ini` is gitignored — keep it that way. Never put
SSH or sudo passwords, key contents, or provider credentials in inventory or
variables.

## 3. Check the host key first

Connect once manually so SSH records and you verify the host identity:

```bash
ssh -i ~/.ssh/your_lab_key ubuntu@<your-lab-host>
```

`host_key_checking = True` stays on — do not disable it as a shortcut.

## 4. Local preflight (also works without the lab host)

```bash
cd ansible
ansible-inventory -i inventory.ini --graph
ansible-playbook -i inventory.ini playbook.yml --syntax-check
ansible-playbook -i inventory.ini playbook.yml --list-hosts
ansible-playbook -i inventory.ini playbook.yml --list-tasks
```

Stop if an unexpected host, group, variable, or task appears.

## 5. Check mode (predict changes, make none)

```bash
ansible-playbook -i inventory.ini playbook.yml --check --diff --limit app-lab-01
```

Review every proposed package, user, directory, file, and service change.
Check mode is useful evidence but does not guarantee identical real-run
behavior (e.g. the service start may report differently once files exist).

## 6. Real run

```bash
ansible-playbook -i inventory.ini playbook.yml --diff --limit app-lab-01
```

The sudo password is requested interactively (`become_ask_pass = True`) —
never store it in a file or command line.

## 7. Verify the service on the host

```bash
systemctl status learning-app
journalctl -u learning-app --no-pager -n 50
```

Expect the unit `active (running)` as `learning_app`, and heartbeat log lines
("learning-app is running in lab."). Also worth checking:

```bash
id learning_app                          # system user, nologin shell
ls -l /opt/learning-app/bin              # root-owned, 0750 executable
ls -l /etc/learning-app/application.conf # root:learning_app 0640
ls -ld /var/lib/learning-app /var/log/learning-app  # learning_app-owned
```

The service account must NOT be able to overwrite
`/opt/learning-app/bin/learning-app` or the config.

## 8. Idempotency second pass

```bash
ansible-playbook -i inventory.ini playbook.yml --diff --limit app-lab-01
```

The steady-state recap should show `changed=0`. Only after seeing that on
your host may you say idempotency was validated. Changing a source file (e.g.
`application.conf`) should change exactly its destination and trigger exactly
one service restart via the handler.

## 9. Cleanup and rollback

This lab intentionally does not automate destructive removal. For a
disposable host, prefer deleting the whole VM after collecting notes. If
manual rollback is required: stop and disable the service, then remove the
unit file, `daemon-reload`, and remove the app directories, user, and group.
Do not remove shared packages without checking other consumers.

## Troubleshooting

- **Inventory cannot resolve:** expected with the documentation address;
  configure your approved lab host in `inventory.ini`.
- **Host-key failure:** verify the host identity; never disable checking.
- **Sudo failure:** confirm the account's sudo policy; never store passwords.
- **Package not found:** adapt `common_packages` to the distribution.
- **Platform assertion fails:** use a Linux + systemd lab host rather than
  bypassing the assertion.
- **Service restart loop:** inspect `journalctl -u learning-app`, the config
  file contents, and the executable's permissions.

## Verification status

Local syntax/list validation was actually run for this repository (see
[../TEST_RESULTS.md](../TEST_RESULTS.md)). Sections 2–9 — real SSH, check
mode, execution, verification, and the idempotency pass — were **not**
executed; no host was contacted or changed.
