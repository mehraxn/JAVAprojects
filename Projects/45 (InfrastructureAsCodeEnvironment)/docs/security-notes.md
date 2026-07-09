# Security Notes

How secrets, state, and access are meant to be handled. **No secret, credential,
real IP, or state file exists in this repo** — this documents the intended
practice, and the safe choices already baked into the examples. For the deeper
walkthrough of state and secret handling, see
[state-and-secrets.md](state-and-secrets.md).

## 1. No credentials anywhere

- Terraform declares **no cloud provider**, so there is nothing to authenticate
  and no credentials to leak.
- In a real project, provider credentials come from the **environment** (e.g.
  `AWS_PROFILE`, OIDC in CI, or a secrets manager) — **never** from `*.tfvars`,
  `*.tf`, or the repo.
- `terraform.tfvars.example` and `inventory.ini.example` contain only
  placeholders; the concrete `terraform.tfvars` and `inventory.ini` are
  git-ignored (see [`../.gitignore`](../.gitignore)).

## 2. Terraform state is sensitive

State can contain secret values **even when outputs are marked sensitive**, so:

- Never commit `*.tfstate` (git-ignored here).
- Use an **encrypted remote backend with locking** — see the `backend.tf.example`
  in each environment (`encrypt = true`, plus a lock table). Those files are
  example-only: bucket/table/region are literal `REPLACE_WITH_REAL_*`
  placeholders, supplied at `init` time and never committed.
- Restrict who can read the state bucket; it is effectively a secrets store.

## 3. Ansible secrets → ansible-vault

- `group_vars/*.yml` hold only **non-sensitive** values (log level, replica
  count). Any real secret (DB password, API token) goes in an
  **`ansible-vault`-encrypted** file, decrypted at run time with a vault password
  that is itself never committed (`vault-password*` is git-ignored).
- The inventory stores **no** passwords or private keys; SSH auth uses an agent
  or `--key-file` at run time.

## 4. No real hosts or IPs

- Inventory/host names use the reserved `.invalid` TLD (`*.example.invalid`,
  RFC 2606) which can never resolve, or RFC 1918 private IPs that are not
  routable on the public internet.
- Host private IPs use **RFC 1918 private space** (`10.10.x` for dev, `10.20.x`
  for prod), derived deterministically from each subnet CIDR.
- SSH source CIDRs use the **RFC 5737 TEST-NET-1 documentation range**
  (`192.0.2.0/24`) — reserved, non-routable placeholders, never a real target.

## 5. Least privilege & access scoping

- `allowed_ssh_cidrs` is tightened per environment: dev uses a documentation
  range (`192.0.2.0/24`) for convenience; **prod is a single `/32`**
  (`192.0.2.10/32`, a would-be bastion), not an open range.
- The Ansible app user has **no login shell** (`/usr/sbin/nologin`) and the
  systemd unit sets `NoNewPrivileges`, `ProtectSystem`, and `ProtectHome`.

## 6. Environment isolation

Dev and prod have **separate state backends** (different bucket/key), so a change
to one can never mutate the other — the blast radius of a mistake is one
environment.

## What was NOT done

- No credentials, keys, tokens, or real endpoints were created or used.
- No remote state file or vault file was produced; no `terraform apply` ran.
- Nothing was authenticated, connected to a cloud, or applied.

(Validation *is* available and safe — `terraform validate`, inventory generation,
and `ansible-playbook --syntax-check` — none of which contact a cloud or a host.
See [../TESTING.md](../TESTING.md).)
