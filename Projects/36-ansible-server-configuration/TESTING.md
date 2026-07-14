# Testing — Ansible Server Configuration

Exact commands to validate this project. Results actually observed are
recorded honestly in [TEST_RESULTS.md](TEST_RESULTS.md). All Ansible commands
run from the `ansible/` folder, because `ansible.cfg` lives there.

## A) YAML validation (Python + PyYAML, from the project root)

```bash
python - <<'PY'
from pathlib import Path
import yaml

for path in sorted(Path("ansible").rglob("*.yml")):
    with path.open("r", encoding="utf-8") as f:
        yaml.safe_load(f)
    print(f"OK {path}")
PY
```

## B) Shell script syntax (from the project root)

```bash
bash -n ansible/roles/app/files/learning-app.sh
```

## C) Local Ansible validation (no server needed)

```bash
cd ansible

ansible-inventory -i inventory.ini.example --graph
ansible-playbook -i inventory.ini.example playbook.yml --syntax-check
ansible-playbook -i inventory.ini.example playbook.yml --list-hosts
ansible-playbook -i inventory.ini.example playbook.yml --list-tasks
```

Expected: the graph shows `app_servers → app-lab-01`; syntax-check completes
without errors; list-hosts shows the one example host; list-tasks shows the
assert pre-task plus the common and app role tasks.

No local Ansible? The same checks run in a container from the project root:

```bash
docker run --rm -v "$PWD:/w" -w /w/ansible python:3.12-slim bash -c \
  "pip install -q ansible-core && ansible-playbook -i inventory.ini.example playbook.yml --syntax-check"
```

(Inside a container the mounted directory may be world-writable, so Ansible
ignores `ansible.cfg` with a warning — that's why these commands always pass
`-i` explicitly.)

## D) Optional disposable-host workflow (check mode → real run → idempotency)

Only for a disposable Linux VM with systemd that you own. Never a production
or shared machine.

```bash
cd ansible
cp inventory.ini.example inventory.ini
# edit inventory.ini: real host, SSH user, key path (kept outside the repo).
# inventory.ini is gitignored — never commit it.

# 1. predict changes without making any
ansible-playbook -i inventory.ini playbook.yml --check --diff

# 2. real run (prompts for the sudo password)
ansible-playbook -i inventory.ini playbook.yml --diff

# 3. idempotency second pass — should report changed=0
ansible-playbook -i inventory.ini playbook.yml --diff
```

Only claim idempotency is validated after the second real run actually shows
`changed=0` on your host. On-host verification commands are in
[docs/runbook.md](docs/runbook.md).

## E) Cleanup

```bash
rm -f ansible/*.retry
rm -f ansible/inventory.ini    # your local copy, if you no longer need it
```
