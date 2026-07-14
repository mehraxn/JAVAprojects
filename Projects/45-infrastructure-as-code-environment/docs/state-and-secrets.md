# State and Secrets

Terraform state and Ansible variable/inventory files can both expose sensitive
data. This document explains how each is meant to be handled and confirms that
**this project ships only safe placeholders** — no real state file, credential,
or secret exists in the repo.

## Why Terraform state can contain sensitive data

Terraform records every managed resource in a **state file** so it knows what
already exists. That file stores resource attributes verbatim — including values
that started as secrets (generated passwords, connection strings, private keys,
tokens returned by a provider). This happens **even when an output is marked
`sensitive`**: `sensitive` only hides the value from CLI output, it does *not*
remove it from state. Treat the state file itself as a secrets store.

## Why remote state should be encrypted

Local state is a plaintext JSON file on disk — easy to leak by accident (a
commit, a backup, a shared laptop). A **remote backend** (e.g. an S3 bucket)
keeps a single shared copy and can **encrypt it at rest** (`encrypt = true` in
the backend block). Encryption means that if the underlying storage is ever
exposed, the state is not readable without the key. In this repo the backend is
example-only — see the `backend.tf.example` files.

## Why state locking matters

If two people (or two CI runs) apply at the same time, they can write to the
state file concurrently and **corrupt it**, leaving Terraform unable to tell
what really exists. **State locking** (e.g. a DynamoDB lock table for the S3
backend) forces operations to run one at a time: the second writer waits until
the first releases the lock. This prevents races and half-written state.

## Why state access should be restricted

Because state can contain secrets and is the source of truth for what exists,
**read and write access must be limited** to the people and pipelines that
genuinely need it. Anyone who can read the state bucket can read its secrets;
anyone who can write it can alter infrastructure. Scope the bucket/table with
least-privilege IAM, enable versioning for recovery, and turn on audit logging
so access is traceable.

## How secrets should NOT be committed

Never put credentials, tokens, private keys, or real IPs in `*.tf`, `*.tfvars`,
`group_vars`, or the inventory. The repo's [`.gitignore`](../.gitignore) blocks
the real `*.tfvars`, `inventory.ini`, `backend.tf`, and vault files so only the
`*.example` files are tracked. The examples in this project contain placeholders
only.

## How secrets should be injected

Secrets should reach the tooling at **run time**, not from the repository:

- **Terraform provider credentials** come from the environment — an
  `AWS_PROFILE`, short-lived OIDC credentials in CI, or a secrets manager —
  never from committed files.
- **CI/CD variables** (masked/protected) inject values into pipeline runs so the
  plaintext never lands in version control.
- **Ansible secrets** go in an **`ansible-vault`**-encrypted file, decrypted at
  run time with a vault password that is itself never committed. SSH auth uses an
  agent or `--key-file` at run time, not a key stored in the inventory.

## Why secret rotation and audit ownership matter

- **Rotation:** credentials should be changed on a schedule and immediately after
  any suspected exposure, so a leaked secret has a limited useful lifetime. Short-
  lived credentials (OIDC, temporary tokens) rotate automatically and are
  preferable to long-lived static keys.
- **Audit & ownership:** every secret and every state bucket needs a clear owner
  and an audit trail (who accessed it, when). Without ownership, secrets go stale
  and unrotated; without auditing, a leak can go unnoticed.

## Safe placeholders only

This project uses **safe placeholders throughout**: no cloud provider is
declared, IPs come from the RFC 5737 documentation and RFC 1918 private ranges,
hostnames use the reserved `.invalid` TLD, and backend region/bucket/table
values are literal `REPLACE_WITH_REAL_*` placeholders. Nothing here is a real
secret and no real state file is produced by the default workflow.
