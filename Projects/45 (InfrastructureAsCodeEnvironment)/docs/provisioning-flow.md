# Provisioning Flow

How the two layers connect, end to end. **Every step below is documented but
NOT executed** — no command in this file was run, and no resource or host exists.

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

## The end-to-end flow (NOT executed)

```
1. terraform -chdir=terraform/environments/dev init      # NOT executed
2. terraform -chdir=terraform/environments/dev plan       # NOT executed
3. terraform -chdir=terraform/environments/dev apply       # NOT executed
        │
        ▼  output: ansible_hosts = [ {name, ansible_host, private_ip, environment}, ... ]
4. generate ansible/inventory.ini from that output          # NOT executed
5. ansible-playbook -i inventory.ini playbook.yml           # NOT executed
        │
        ▼  common role → app role on every host
6. application node is configured and the service is running
```

## The handoff contract

The compute module emits one record per node:

```hcl
# terraform/modules/compute/outputs.tf  (placeholder values)
ansible_hosts = [
  { name = "iac-lab-dev-app-0", ansible_host = "iac-lab-dev-app-0.example.invalid",
    private_ip = "198.51.100.10", environment = "dev" }
]
```

A real pipeline feeds this into an inventory generator (e.g.
`terraform output -json ansible_hosts | <template> > inventory.ini`, or a
dynamic-inventory plugin). Here the same shape is shown statically in
`ansible/inventory.ini.example`. No `terraform output` was actually run.

## Why the boundary matters

Keeping provisioning and configuration separate means:

- Terraform state stays the single source of truth for *what exists*.
- Ansible can re-run any time to fix drift *without* Terraform re-creating hosts.
- Each side can be reviewed, versioned, and gated independently.

## What was NOT done

- No `terraform init/plan/apply` ran; no state file was created.
- No `terraform output` was produced; no inventory was generated.
- No `ansible-playbook` ran; no host was contacted.
