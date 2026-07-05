# Architecture

An educational Infrastructure-as-Code environment with two cooperating layers.
**Nothing here was initialized, planned, applied, connected, or configured.**

## Two layers, two jobs

```
                 PROVISIONING                        CONFIGURATION
        ┌──────────────────────────┐        ┌──────────────────────────┐
        │        Terraform         │        │         Ansible          │
        │  create/patch/destroy    │  --->  │  install packages, users │
        │  network + compute       │ hosts  │  render config, run svc  │
        └──────────────────────────┘        └──────────────────────────┘
             (declarative, stateful)             (idempotent, push-based)
```

- **Terraform (provisioning)** declares *what infrastructure exists* — the
  network and the app nodes — and tracks it in state. In this lab every resource
  is a built-in `terraform_data` record, so applying creates **nothing real**.
- **Ansible (configuration)** decides *what runs on each host once it exists* —
  packages, the app user, config files, and the service. It targets only
  unreachable `*.example.invalid` hosts here.

## Terraform composition

```
terraform/
├── versions.tf                 # built-in provider only, no cloud
├── main.tf                     # composes the modules (sandbox root)
├── variables.tf / outputs.tf
├── terraform.tfvars.example
├── modules/
│   ├── network/                # models a VPC + subnets
│   └── compute/                # models app instances; emits ansible_hosts
└── environments/
    ├── dev/                    # own root + own state, dev-sized
    └── prod/                   # own root + own state, prod-sized, stricter
```

The reusable **modules** (`network`, `compute`) are the building blocks. The
**root** composes them for a sandbox. Each **environment** is its own working
directory that reuses that composition with different inputs and, crucially, its
own state backend.

## Ansible composition

```
ansible/
├── ansible.cfg
├── inventory.ini.example       # dev + prod groups, *.example.invalid only
├── playbook.yml                # applies common -> app to app_servers
├── group_vars/{all,dev,prod}.yml
└── roles/
    ├── common/                 # baseline: packages, timezone, app user
    └── app/                    # config template + systemd service
```

## The handoff

Terraform's `ansible_hosts` output is the contract between the layers: a list of
placeholder host records that a real workflow would render into Ansible's
inventory. See [provisioning-flow.md](provisioning-flow.md). Secrets and state
handling are covered in [security-notes.md](security-notes.md).
