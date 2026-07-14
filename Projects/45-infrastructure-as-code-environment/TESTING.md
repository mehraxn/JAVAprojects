# Testing — Infrastructure as Code Environment

This project can be **validated safely without creating any real infrastructure.**
Terraform is validated with `fmt`/`validate` (no `apply`), the Terraform →
Ansible handoff is exercised with a bundled sample, and Ansible is validated with
`--syntax-check`. **No cloud provider is declared, no host is contacted, and
`terraform apply` is never part of this workflow.**

Recorded results live in [TEST_RESULTS.md](TEST_RESULTS.md).

The inventory generator is executable on Unix-like systems. The commands below
use `python3 scripts/generate-inventory.py` because it also works on Windows.

> Commands assume you run them from this project's root folder —
> `45-infrastructure-as-code-environment/`, the directory containing this file.
> On Windows, run the multi-line commands on a single line or use the PowerShell
> line-continuation backtick instead of `\`.

## 1. Terraform formatting

```bash
terraform fmt -check -recursive terraform
```

## 2. Terraform init (no backend) — dev

`-backend=false` skips any remote state; nothing is created.

```bash
terraform -chdir=terraform/environments/dev init -backend=false
```

## 3. Terraform validate — dev

```bash
terraform -chdir=terraform/environments/dev validate
```

## 4. Terraform init (no backend) — prod

```bash
terraform -chdir=terraform/environments/prod init -backend=false
```

## 5. Terraform validate — prod

```bash
terraform -chdir=terraform/environments/prod validate
```

## 6. Generate a sample inventory (no real infrastructure)

Uses the bundled sample output, so no Terraform run is required:

```bash
python3 scripts/generate-inventory.py \
  examples/terraform-output/ansible_hosts.dev.json \
  ansible/inventory.ini
```

The generator is deliberately per-environment. If a JSON file mixes dev and prod
hosts, it fails instead of producing an ambiguous inventory.

The full pipeline (only when you have a real, disposable environment) would be:

```bash
terraform -chdir=terraform/environments/dev output -json ansible_hosts \
  > /tmp/ansible_hosts.json
python3 scripts/generate-inventory.py /tmp/ansible_hosts.json ansible/inventory.ini
```

## 7. Ansible syntax check

```bash
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml --syntax-check
```

## 8. Optional: Ansible check mode (dry run)

```bash
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml --check
```

> Check mode **connects to the hosts** to gather facts and report intended
> changes. The bundled hosts use RFC 1918 placeholder IPs that are not reachable,
> so `--check` only works against real, reachable, disposable hosts. Use step 7
> (`--syntax-check`) for offline validation.

## 9. Cleanup

The generated inventory is local-only and git-ignored — remove it when done:

```bash
rm -f ansible/inventory.ini
```

## 10. What NOT to run

```bash
terraform apply     # intentionally NOT part of this workflow
```

Only run `terraform apply` if you have deliberately swapped in a real provider
and are pointing at a disposable account you own. By default every resource is a
built-in `terraform_data` record, so even an apply would create nothing in a
cloud — but `apply` is still excluded from the default workflow on purpose.

## 11. Manual review checklist (portfolio quality)

- [ ] README makes the provisioning-vs-configuration distinction clear.
- [ ] Module + dev/prod structure reads like real-world Terraform.
- [ ] tfvars/variables are actually used (env roots pass `var.*`, not literals).
- [ ] `private_ip` values are RFC 1918 (`10.x`), not documentation ranges.
- [ ] The generator turns `ansible_hosts` JSON into a valid inventory.
- [ ] Secret/state handling is explicit and safe (docs/state-and-secrets.md).
- [ ] No real secrets, credentials, or real IPs anywhere.
