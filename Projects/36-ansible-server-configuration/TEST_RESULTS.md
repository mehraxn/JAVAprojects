# Test Results — Ansible Server Configuration

Date: 2026-07-10. Host: Windows 11. Ansible is not installed on the host, so
the Ansible checks ran inside a `python:3.12-slim` Docker container with
`ansible-core 2.21.1` installed via pip — same commands, real results. YAML
parsing used the host Python + PyYAML; shell syntax used Git Bash.

## Static validation

| Check | Result | Notes |
|---|---:|---|
| YAML parse (all 5 `ansible/**/*.yml`) | PASS | `yaml.safe_load` clean on playbook, group_vars, both role task files, handlers |
| Shell syntax (`bash -n learning-app.sh`) | PASS | no syntax errors |
| Executable bit on `learning-app.sh` | PASS | git index mode set to 100755 |
| `ansible-inventory -i inventory.ini.example --graph` | PASS | `@app_servers → app-lab-01` resolves |
| `ansible-playbook … --syntax-check` | PASS | no errors |
| `ansible-playbook … --list-hosts` | PASS | 1 host: `app-lab-01` |
| `ansible-playbook … --list-tasks` | PASS | all 12 tasks listed: assert pre-task, 6 common tasks, 5 app tasks |

Note: inside the container the mounted directory is world-writable, so
Ansible ignored `ansible.cfg` with a warning; every command therefore passed
`-i inventory.ini.example` explicitly. This does not affect the results.

## Real host validation

| Check | Result | Notes |
|---|---:|---|
| Check mode (`--check --diff`) | NOT RUN | no disposable host configured |
| Real playbook run | NOT RUN | no SSH/sudo/systemd operation was performed anywhere |
| Idempotency second run | NOT RUN | requires two real runs on a disposable host |

No host was contacted or changed. The playbook's tasks are **idempotent by
design** (declarative modules with `state:` values), but idempotency is only
proven by a second real run showing `changed=0` — see TESTING.md section D.

## Known limitations

- No production or lab hosts included; the inventory is documentation-only
  (TEST-NET-1 address).
- No secrets, SSH keys, or vault material committed; `.gitignore` blocks the
  real inventory and key files.
- The systemd hardening options are a demo baseline validated only by syntax
  checks here, not a CIS-style benchmark and not runtime-tested.
- Package names (`ca-certificates`, `curl`) assume a mainstream Linux
  distribution.
- Results are a point-in-time snapshot of one validation run.
