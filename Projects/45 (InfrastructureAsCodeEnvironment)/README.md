# Infrastructure as Code Environment

*A safe Infrastructure-as-Code environment pairing Terraform (provisioning) with Ansible (configuration) — reusable modules, dev/prod separation, and encrypted-state / secret discipline.*

## Problem this project solves

Click-ops infrastructure is unrepeatable and undocumented; ad-hoc SSH changes
drift. This project shows the **IaC** answer with two complementary tools:
**Terraform** declares *what infrastructure exists* (and tracks it in state), and
**Ansible** decides *what runs on each host once it exists*. It's built to be
**provably safe** — no cloud provider, no real hosts, no real IPs.

## Technologies & concepts

- **Terraform** — reusable modules (`network`, `compute`), dev/prod roots, remote-state design
- **Ansible** — `common` + `app` roles, group_vars, idempotent tasks, templates
- **Provisioning vs configuration**; **environment isolation** via separate state
- **Secret handling** — encrypted state, `ansible-vault`, no creds in Git

## Architecture overview

```
        PROVISIONING (Terraform)             CONFIGURATION (Ansible)
   ┌───────────────────────────┐       ┌───────────────────────────┐
   │ modules: network, compute │  ───▶ │ roles: common, app        │
   │ declarative + stateful    │ hosts │ idempotent, push over SSH │
   └───────────────────────────┘       └───────────────────────────┘
        handoff: Terraform `ansible_hosts` output → Ansible inventory
```

Every Terraform resource is a built-in `terraform_data` model — applying creates
**nothing** in any cloud.

## Project structure

```text
terraform/
  versions.tf main.tf variables.tf outputs.tf terraform.tfvars.example
  modules/network/   modules/compute/          reusable building blocks
  environments/dev/  environments/prod/        own state, own sizing
ansible/
  ansible.cfg inventory.ini.example playbook.yml
  group_vars/{all,dev,prod}.yml
  roles/common/  roles/app/
docs/architecture.md  docs/provisioning-flow.md  docs/security-notes.md
.gitignore  README.md  TESTING.md
```

## Important files explained

- **terraform/modules/{network,compute}** — reusable models; `compute` emits `ansible_hosts` (the handoff to Ansible).
- **terraform/environments/{dev,prod}** — separate roots, each with its **own `backend.tf.example`** (isolated state) and env-specific sizing/access.
- **ansible/roles/common** (packages, timezone, no-login app user) + **roles/app** (config template + hardened systemd unit).
- **docs/security-notes.md** — the encrypted-state, no-creds, placeholder-only rules.

## How it would work in a real environment

`terraform init/plan/apply` in `environments/dev` provisions the modeled
infrastructure and outputs `ansible_hosts`; a generator turns that into an
inventory; `ansible-playbook` applies `common` then `app` to each host. Prod is a
separate root with separate state, so a dev change can never touch prod. Real
credentials come from the environment/secrets manager, never the repo.

## What was prepared but NOT executed

Prepared: the full Terraform composition (root + 2 modules + dev/prod roots), the
Ansible playbook/roles/vars/templates, and three docs. **Not executed:** no
`terraform init/plan/apply`, no `terraform output`, no inventory generation, no
`ansible-playbook`, no SSH. **No infrastructure was applied.**

## Security notes

- **No cloud provider** declared (only `terraform_data`) — nothing to authenticate, no creds to leak.
- **No real hosts** (`*.example.invalid`) and **no real IPs** (RFC 5737 docs ranges / RFC 1918).
- **No real secrets** — real secrets via env vars / `ansible-vault`; `.gitignore` blocks real `tfvars`/`inventory.ini`/`backend.tf`/vault files.
- State is treated as sensitive: encrypted, locked remote backend (`encrypt = true`).
- Least privilege: prod access scoped to a single `/32`; app user has no login shell.

## Limitations

- Nothing was `init`/`plan`/`apply`-ed; no state file or inventory exists.
- `terraform`/`ansible` were not run (nor validated/linted); no JDK/tooling assumed.
- The `terraform_data` resources model infrastructure — they don't create it.

## Future improvements

- Swap `terraform_data` for a real provider module against a disposable account.
- Add `terraform validate`/`fmt`, `ansible-lint`, and Molecule role tests in CI.
- Dynamic inventory plugin instead of a generated file; sealed secrets.
- Drift detection and a teardown/cleanup workflow.

## What I learned

- The clean split between **provisioning (Terraform)** and **configuration (Ansible)**.
- Why **separate state per environment** is the core of dev/prod isolation.
- Reusable **module composition** and the provisioning→configuration **handoff**.
- Handling secrets and state safely: encrypted state, vault, placeholders, `.gitignore`.
