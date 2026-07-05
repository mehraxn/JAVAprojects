# Secrets

How secrets are handled across environments. **No real secret exists anywhere in
this repo** — only `secret.example.yaml` placeholders.

## The rules

1. **Never commit a real secret to Git.** Only `*.example.yaml` files with
   obvious `REPLACE_ME_*` placeholders are tracked.
2. **Secrets are per environment**, in each environment's own namespace
   (`app-dev` / `app-staging` / `app-prod`), and are separate from config.
3. **Secrets are not baked into the image.** The image is environment-agnostic;
   secrets are injected at runtime via a Kubernetes `Secret` the app reads as
   env vars.

## How real secrets would be delivered (NOT done here)

Since GitOps wants everything in Git but secrets can't be, use one of:

- **Sealed Secrets** — commit an *encrypted* `SealedSecret`; the in-cluster
  controller decrypts it into a real `Secret`. Safe to store in Git.
- **External Secrets Operator** — commit a reference (`ExternalSecret`) that
  pulls the value from a real secrets manager (Vault, AWS/GCP/Azure) at runtime.

Both keep the plaintext out of Git while staying GitOps-friendly.

## What's in this repo

Each `environments/<env>/secret.example.yaml` shows the **shape** of the Secret
(`DB_PASSWORD`, `API_TOKEN`) with placeholder values, so you can see what the app
expects without any real credential being present. The Helm chart's
`secret.example.yaml` template is disabled unless explicitly enabled, and also
only emits placeholders.

## What was NOT done

- No real secret was created, committed, sealed, or synced.
- No secrets manager or controller was configured.
