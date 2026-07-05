# Testing — Infrastructure as Code Environment

> **Nothing was executed.** No Terraform, Ansible, cloud, or SSH command ran; no
> state file exists and **no infrastructure was applied.** This documents static
> review and expected behavior in a disposable environment.

## 1. Static validation checklist

- [ ] Terraform declares only the built-in provider; every resource is `terraform_data`.
- [ ] Root `main.tf` references `./modules/network` + `./modules/compute`; env roots reference `../../`.
- [ ] `ansible_hosts` output fields match what the inventory example models.
- [ ] `playbook.yml` targets `app_servers` and applies `common` then `app`.
- [ ] Handler names match `notify:` strings.

## 2. File existence checks

- [ ] `terraform/`: versions/main/variables/outputs/tfvars.example, `modules/{network,compute}/*`, `environments/{dev,prod}/*`
- [ ] `ansible/`: ansible.cfg, inventory.ini.example, playbook.yml, `group_vars/*`, `roles/{common,app}/*`
- [ ] `docs/architecture.md`, `provisioning-flow.md`, `security-notes.md`
- [ ] `.gitignore`, `README.md`, `TESTING.md`

## 3. YAML / HCL / config review checklist

- [ ] HCL parses; module variable/output names line up.
- [ ] dev vs prod `backend.tf.example` use **distinct** state keys/buckets.
- [ ] dev vs prod sizing/access differ (e.g. prod SSH = single `/32`).
- [ ] Ansible roles have tasks/handlers/templates/defaults as needed.

## 4. Security checks

- [ ] **No real secrets** — no creds/keys/tokens in `*.tf`, tfvars.example, group_vars, or inventory.
- [ ] **No real credentials** — no provider auth; real creds from env/secrets manager.
- [ ] **No production endpoints / real IPs** — `*.example.invalid`, RFC 5737/1918 only.
- [ ] `.gitignore` excludes real `*.tfvars`, `inventory.ini`, `backend.tf`, vault files.

## 5. Commands normally used — NOT executed

```bash
# NOT executed
terraform -chdir="terraform/environments/dev" init
terraform -chdir="terraform/environments/dev" plan
terraform -chdir="terraform/environments/dev" apply
terraform -chdir="terraform/environments/dev" output -json ansible_hosts
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml
```

## 6. Expected results in a proper environment

- `terraform validate`/`plan` succeed and show the modeled resources.
- dev and prod plans are independent (separate state); no cross-impact.
- `ansible-playbook --check` (dry run) reports intended, idempotent changes only.
- The `ansible_hosts` output renders cleanly into an inventory.

## 7. Manual review checklist (portfolio quality)

- [ ] README makes the provisioning-vs-configuration distinction clear.
- [ ] Module + dev/prod structure reads like real-world Terraform.
- [ ] Secret/state handling is explicit and safe.
- [ ] Every command marked NOT executed; no fake outputs/badges.
- [ ] Honest that no infrastructure was applied.
