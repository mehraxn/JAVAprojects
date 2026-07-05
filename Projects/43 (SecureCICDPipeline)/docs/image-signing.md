# Image Signing

Covers stage 9 of [`../ci/github-actions.example.yml`](../ci/github-actions.example.yml).
**No image was signed in this repository.** The template pushes nothing
(`push: false`), so there is deliberately no real artifact to sign. Every command
below is documented and marked **NOT executed**.

## What image signing is

Signing attaches a cryptographic signature to a container image so a consumer can
**verify two things before running it**:

1. **Integrity** — the image has not been modified since it was signed.
2. **Provenance** — it was produced by a trusted identity (this pipeline), not an
   attacker who pushed a look-alike to the registry.

Signatures bind to the image **digest** (`@sha256:...`), never a mutable tag like
`:latest`, because tags can be repointed.

## Keyless signing with Cosign

The template uses [Cosign](https://github.com/sigstore/cosign) in **keyless**
mode (Sigstore). Keyless avoids the biggest risk of signing — a long-lived
private key that can be stolen:

```
GitHub OIDC token (workflow identity)
        │
        ▼
   Fulcio  ──issues──▶ short-lived signing certificate (valid ~minutes)
        │
   cosign sign ──▶ signature + cert recorded in Rekor (public transparency log)
```

The workflow proves *who it is* with its OIDC identity (hence
`permissions: id-token: write`), Fulcio issues a throwaway certificate, and the
signature is logged in the Rekor transparency log. No private key is stored
anywhere.

## The flow — NOT executed

```bash
# NOT executed. Sign the image BY DIGEST (never by tag):
cosign sign --yes ghcr.io/example/secure-cicd-app@sha256:<digest>

# NOT executed. Attach the SBOM as a signed attestation (ties docs/sbom.md
# evidence to the exact image):
cosign attest --yes \
  --predicate sbom.cyclonedx.json \
  --type cyclonedx \
  ghcr.io/example/secure-cicd-app@sha256:<digest>

# NOT executed. A deployer/admission controller verifies before running:
cosign verify \
  --certificate-identity-regexp 'https://github.com/example/.*' \
  --certificate-oidc-issuer https://token.actions.githubusercontent.com \
  ghcr.io/example/secure-cicd-app@sha256:<digest>
```

## Where verification is enforced

Signing only helps if something **checks** the signature. In a real system that
happens at deploy time via an admission controller, e.g. Kubernetes with
**Sigstore Policy Controller** or **Kyverno**, configured to reject any image
lacking a valid signature from this pipeline's identity. That enforcement is out
of scope here and was not configured.

## Why this template does not sign anything real

- `push: false` — the image never leaves the runner, so there is no registry
  digest to sign.
- No OIDC identity or Fulcio/Rekor interaction was performed.
- The signing step in the workflow only **echoes** the commands; it does not run
  `cosign sign` against a real image.

## What was prepared but NOT executed

- No image was pushed, so nothing was signed.
- No Cosign signature or attestation was created.
- No Rekor entry was made and no verification policy was applied.
