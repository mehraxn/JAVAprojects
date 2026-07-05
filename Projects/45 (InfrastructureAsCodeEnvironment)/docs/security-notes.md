# Security Notes

How secrets, state, and access are meant to be handled. **No secret, credential,
real IP, or state file exists in this repo** — this documents the intended
practice, and the safe choices already baked into the examples.

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
  in each environment (`encrypt = true`, plus a lock table). Real bucket/table
  names are supplied at `init` time, not committed.
- Restrict who can read the state bucket; it is effectively a secrets store.

## 3. Ansible secrets → ansible-vault

- `group_vars/*.yml` hold only **non-sensitive** values (log level, replica
  count). Any real secret (DB password, API token) goes in an
  **`ansible-vault`-encrypted** file, decrypted at run time with a vault password
  that is itself never committed (`vault-password*` is git-ignored).
- The inventory stores **no** passwords or private keys; SSH auth uses an agent
  or `--key-file` at run time.

## 4. No real hosts or IPs

- Every inventory host is a `*.example.invalid` name — the `.invalid` TLD is
  reserved by RFC 2606 and can never resolve.
- Every IP/CIDR is from the **RFC 5737 documentation ranges** (`192.0.2.0/24`,
  `198.51.100.0/24`, `203.0.113.0/24`) or RFC 1918 private space — reserved,
  non-routable placeholders, never a real target.

## 5. Least privilege & access scoping

- `allowed_ssh_cidrs` is tightened per environment: dev uses a documentation
  range for convenience; **prod is a single `/32`** (a would-be bastion), not an
  open range.
- The Ansible app user has **no login shell** (`/usr/sbin/nologin`) and the
  systemd unit sets `NoNewPrivileges`, `ProtectSystem`, and `ProtectHome`.

## 6. Environment isolation

Dev and prod have **separate state backends** (different bucket/key), so a change
to one can never mutate the other — the blast radius of a mistake is one
environment.

## What was NOT done

- No credentials, keys, tokens, or real endpoints were created or used.
- No state file, vault file, or concrete inventory was produced.
- Nothing was authenticated, connected, or applied.
