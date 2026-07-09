# Infrastructure as Code Environment

*A safe Infrastructure-as-Code environment pairing Terraform (provisioning) with
Ansible (configuration) — reusable modules, dev/prod separation, a real
Terraform → Ansible inventory handoff, and encrypted-state / secret discipline.
Validated locally without creating any real cloud infrastructure.*

## Problem this project solves

Click-ops infrastructure is unrepeatable and undocumented; ad-hoc SSH changes
drift. This project shows the **IaC** answer with two complementary tools:
**Terraform** declares *what infrastructure exists* (and tracks it in state), and
**Ansible** decides *what runs on each host once it exists*. It is built to be
**provably safe** — no cloud provider, no real hosts, no real IPs — yet still
**validatable**: `terraform validate`, an inventory generator, and
`ansible-playbook --syntax-check` all run without touching a cloud.

## The project story

```
Terraform modules (network, compute)        reusable building blocks
        │
        ▼   dev/prod roots pass different values (sizing, CIDRs, access)
Terraform outputs `ansible_hosts`            host records for the handoff
        │
        ▼   scripts/generate-inventory.py
Ansible inventory (ansible/inventory.ini)    [app] group + app_environment
        │
        ▼   ansible-playbook (common → app roles)
configured hosts                             packages, user, config, service
        │
        ▼
validated safely — no `terraform apply`, no real infrastructure
```

## Technologies & concepts

- **Terraform** — reusable modules (`network`, `compute`), separate dev/prod
  roots, variables/tfvars, remote-state design (`backend.tf.example`).
- **Ansible** — `common` + `app` roles, `group_vars`, idempotent tasks, templates.
- **Provisioning vs configuration**; **environment isolation** via separate state.
- **Terraform → Ansible handoff** — a Python generator turns Terraform output
  JSON into an Ansible inventory.
- **Secret & state handling** — encrypted remote state, locking, restricted
  access, `ansible-vault`, no creds in Git (see
  [docs/state-and-secrets.md](docs/state-and-secrets.md)).

## Project structure

```text
terraform/
  versions.tf main.tf variables.tf outputs.tf terraform.tfvars.example
  modules/network/   modules/compute/          reusable building blocks
  environments/dev/  environments/prod/        own state, own variables/sizing
ansible/
  ansible.cfg  inventory.ini.example  playbook.yml
  group_vars/all.yml                            shared + per-env-derived vars
  roles/common/  roles/app/
scripts/generate-inventory.py                   Terraform output -> inventory
examples/terraform-output/ansible_hosts.*.json  safe sample handoff data
docs/architecture.md  docs/provisioning-flow.md
docs/security-notes.md  docs/state-and-secrets.md
.gitignore  README.md  TESTING.md  TEST_RESULTS.md
```

## Terraform module structure

- **`modules/network`** — models a VPC + subnets (from `vpc_cidr` /
  `public_subnet_cidrs`).
- **`modules/compute`** — models app instances and emits **`ansible_hosts`**, the
  handoff to Ansible. Each node gets a deterministic **RFC 1918 private IP**
  derived from its subnet CIDR (e.g. `10.10.1.0/24` → `10.10.1.10`).
- The shared **root** (`terraform/main.tf`) composes both modules.

## dev / prod environments

`terraform/environments/dev` and `terraform/environments/prod` are **separate
Terraform roots with separate state**, each reusing the shared composition via
`source = "../../"`. Each declares its inputs in `variables.tf` (with sensible
defaults so `validate` works) and documents them in `terraform.tfvars.example`:

| | dev | prod |
| --- | --- | --- |
| `vpc_cidr` | `10.10.0.0/16` | `10.20.0.0/16` |
| `instance_count` | 1 | 2 |
| `instance_size` | small | medium |
| SSH access | `192.0.2.0/24` (range) | `192.0.2.10/32` (single bastion) |

Because state is separate, a dev change can never touch prod.

## Ansible role structure

- **`roles/common`** — baseline every host: packages, timezone, a no-login app
  user/group.
- **`roles/app`** — renders the app config template and installs a hardened
  systemd unit, then ensures the service is running.
- **`group_vars/all.yml`** — shared, non-sensitive values. Per-environment
  settings (log level, replica count) are **derived from `app_environment`**,
  which the generated inventory sets from Terraform's per-environment output.

> The app role demonstrates configuration management and service supervision
> using a **placeholder service** (a systemd unit that just logs and stays up).
> It does **not** deploy a production application binary.

## Terraform → Ansible handoff

The compute module's `ansible_hosts` output is the contract between the two
layers. `scripts/generate-inventory.py` converts that JSON into an Ansible
inventory. The script is executable and can also be run as
`./scripts/generate-inventory.py`; the examples below use `python3` for Windows
compatibility. It intentionally generates **one inventory per environment** and
rejects mixed dev/prod input so `app_environment` is never ambiguous.


```bash
# straight from Terraform (real, disposable environment only):
terraform -chdir=terraform/environments/dev output -json ansible_hosts \
  > /tmp/ansible_hosts.json
python3 scripts/generate-inventory.py /tmp/ansible_hosts.json ansible/inventory.ini

# or, fully offline, from the bundled sample:
python3 scripts/generate-inventory.py \
  examples/terraform-output/ansible_hosts.dev.json ansible/inventory.ini
# or: ./scripts/generate-inventory.py examples/terraform-output/ansible_hosts.dev.json ansible/inventory.ini
```

Generated inventory:

```ini
[app]
iac-lab-dev-app-0 ansible_host=10.10.1.10 ansible_user=ubuntu environment=dev

[app:vars]
app_environment=dev
```

`ansible/inventory.ini` is **generated locally and not committed** (git-ignored);
only `inventory.ini.example` is tracked.

## What is implemented vs example-only

**Implemented and runnable:**
- Terraform composition (root + `network`/`compute` modules + dev/prod roots),
  validatable with `terraform validate`.
- The inventory generator and safe sample data (really runs; see
  [TEST_RESULTS.md](TEST_RESULTS.md)).
- Ansible playbook/roles/templates, checkable with `--syntax-check`.

**Example-only (never run against a cloud):**
- `backend.tf.example` in each environment — remote state is real-world design,
  but the bucket/table/region are literal `REPLACE_WITH_REAL_*` placeholders and
  must be created outside this lab.
- No cloud provider is declared: every resource is a built-in `terraform_data`
  record, so even `apply` would create nothing real.

## How to validate safely

No real infrastructure is created by default. Validation can be run locally with
`terraform validate` and `ansible-playbook --syntax-check`; `terraform apply` is
intentionally **not** part of the default workflow. Full commands are in
[TESTING.md](TESTING.md); recorded outcomes are in [TEST_RESULTS.md](TEST_RESULTS.md).

**Run:**

```bash
terraform fmt -check -recursive terraform
terraform -chdir=terraform/environments/dev  init -backend=false && \
  terraform -chdir=terraform/environments/dev  validate
terraform -chdir=terraform/environments/prod init -backend=false && \
  terraform -chdir=terraform/environments/prod validate
python3 scripts/generate-inventory.py \
  examples/terraform-output/ansible_hosts.dev.json ansible/inventory.ini
# or: ./scripts/generate-inventory.py examples/terraform-output/ansible_hosts.dev.json ansible/inventory.ini
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml --syntax-check
rm -f ansible/inventory.ini   # generated, not committed
```

**Do NOT run** `terraform apply` unless you have deliberately swapped in a real
provider and a disposable account you own.

## Security notes

- **No cloud provider** declared (only `terraform_data`) — nothing to
  authenticate, no creds to leak.
- **No real hosts** (`*.example.invalid`) and **no real IPs** — RFC 1918 private
  ranges (`10.x`) for hosts, RFC 5737 documentation ranges (`192.0.2.0/24`) for
  SSH source placeholders.
- **No real secrets** — real secrets via env vars / CI variables / `ansible-vault`;
  `.gitignore` blocks real `tfvars`, `inventory.ini`, `backend.tf`, and vault files.
- State is treated as sensitive: encrypted, locked, access-restricted remote
  backend (`encrypt = true`) — see
  [docs/state-and-secrets.md](docs/state-and-secrets.md).
- Least privilege: prod SSH scoped to a single `/32`; the app user has no login
  shell and the systemd unit is hardened.

## Limitations

- Every resource is a `terraform_data` model — it does **not** create real cloud
  infrastructure; no `terraform apply` is part of the workflow.
- `ansible --check` needs reachable hosts; the placeholder IPs are not reachable,
  so offline validation uses `--syntax-check`.
- Terraform/Ansible must be installed to run their validation steps; see
  [TEST_RESULTS.md](TEST_RESULTS.md) for what was executed where.

## Future improvements

- Swap `terraform_data` for a real provider module against a disposable account.
- Add `terraform validate`/`fmt`, `ansible-lint`, and Molecule role tests in CI.
- Dynamic inventory plugin instead of a generated file; sealed secrets.
- Drift detection and a teardown/cleanup workflow.

## What I learned

- The clean split between **provisioning (Terraform)** and **configuration
  (Ansible)**, and the **handoff** that connects them.
- Why **separate state per environment** is the core of dev/prod isolation.
- Reusable **module composition** and driving env differences from variables.
- Handling secrets and state safely: encrypted/locked state, vault, placeholders,
  `.gitignore`.
