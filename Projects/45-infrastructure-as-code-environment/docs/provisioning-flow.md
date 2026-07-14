# Provisioning Flow

How the two layers connect, end to end. **No real infrastructure is created** —
every Terraform resource is a built-in `terraform_data` record and no host is
contacted. The steps below are validatable safely (`terraform validate`,
inventory generation, `ansible-playbook --syntax-check`); `terraform apply` is
intentionally not part of the workflow.

## Provisioning vs configuration

| | Provisioning (Terraform) | Configuration (Ansible) |
| --- | --- | --- |
| Question | "What infrastructure should exist?" | "What should run on each host?" |
| Model | declarative + **stateful** (tracks reality) | **idempotent** steps, push-based over SSH |
| Creates | networks, VMs, disks, DNS | packages, users, files, services |
| Unit of change | resource in state | task/handler in a role |
| Rerun effect | converges state to config | re-asserts desired host state |

They are complementary: Terraform makes the empty servers; Ansible turns them
into useful application nodes. Using the right tool for each avoids two classic
mistakes — hand-editing servers Terraform thinks it owns, and scripting VM
creation imperatively inside Ansible.

## The end-to-end flow

```
1. terraform -chdir=terraform/environments/dev init -backend=false   # safe: validatable
2. terraform -chdir=terraform/environments/dev validate               # safe: validatable
   (terraform apply is intentionally NOT part of this workflow)
        │
        ▼  output: ansible_hosts = [ {name, ansible_host, ansible_user, private_ip, environment}, ... ]
3. python3 scripts/generate-inventory.py <output.json> ansible/inventory.ini
        │
        ▼  [app] group + app_environment, RFC 1918 private IPs
4. ansible-playbook -i ansible/inventory.ini playbook.yml --syntax-check
        │
        ▼  common role → app role would configure each host
5. application node is configured and the (placeholder) service is running
```

Steps 1–2 and 3–4 run without any cloud. The `terraform output` in step 3 only
produces real host records against a real, disposable environment; for offline
testing use the bundled `examples/terraform-output/ansible_hosts.dev.json`.

## The handoff contract

The compute module emits one record per node:

```hcl
# terraform/modules/compute/outputs.tf  (placeholder values, RFC 1918 private IPs)
ansible_hosts = [
  { name = "iac-lab-dev-app-0", ansible_host = "10.10.1.10",
    ansible_user = "ubuntu", private_ip = "10.10.1.10", environment = "dev" }
]
```

`scripts/generate-inventory.py` feeds this into an inventory:

```bash
terraform output -json ansible_hosts | \
  python3 scripts/generate-inventory.py - ansible/inventory.ini
```

The same shape is shown statically in `ansible/inventory.ini.example` and in the
bundled sample JSON under `examples/terraform-output/`.

## Why the boundary matters

Keeping provisioning and configuration separate means:

- Terraform state stays the single source of truth for *what exists*.
- Ansible can re-run any time to fix drift *without* Terraform re-creating hosts.
- Each side can be reviewed, versioned, and gated independently.

## Safety boundaries

- No cloud provider is declared; every resource is a `terraform_data` model.
- No `terraform apply` in the default workflow — validation only.
- Generated `ansible/inventory.ini` is git-ignored and local-only.
- No credentials, keys, tokens, or real endpoints are created or used.
