# Image Signing

Covers the signing/attestation stage (stage 11) of
[`../ci/github-actions.example.yml`](../ci/github-actions.example.yml) and the
runnable workflow. **Signing is a documented release-stage design in this
project — no image was signed.** The pipeline never pushes an image, so there
is deliberately no registry digest to sign; the workflow step prints an
explanation instead of running `cosign sign`.

## What image signing is

Signing attaches a cryptographic signature to a container image so a consumer
can **verify two things before running it**:

1. **Integrity** — the image has not been modified since it was signed.
2. **Provenance** — it was produced by a trusted identity (this pipeline), not
   an attacker who pushed a look-alike to the registry.

Signatures bind to the image **digest** (`@sha256:...`), never a tag, because
tags are mutable and can be repointed. For the same reason, production
promotion between environments should move digests, not tags.

## Keyless signing with Cosign

The design uses [Cosign](https://github.com/sigstore/cosign) in **keyless**
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

The workflow proves *who it is* with its OIDC identity (which is why a signing
job needs `permissions: id-token: write`), Fulcio issues a throwaway
certificate, and the signature is logged in the Rekor transparency log. No
private key is stored anywhere.

## The release-stage flow (documented, not executed here)

```bash
# 1. A release pipeline pushes the image and captures its digest, then signs
#    BY DIGEST (never by tag):
cosign sign --yes <registry>/<repo>@sha256:<digest>

# 2. Attach the SBOM as a signed attestation (ties docs/sbom.md evidence to
#    the exact image):
cosign attest --yes \
  --predicate artifacts/sbom.cyclonedx.json \
  --type cyclonedx \
  <registry>/<repo>@sha256:<digest>

# 3. A deployer/admission controller verifies before running:
cosign verify \
  --certificate-identity-regexp 'https://github.com/<org>/.*' \
  --certificate-oidc-issuer https://token.actions.githubusercontent.com \
  <registry>/<repo>@sha256:<digest>
```

Running this for real requires a registry you control, a pushed image digest,
and workflow OIDC permissions — none of which this lab configures, on purpose.
Do not run signing against a real registry unless all of that is explicitly and
safely set up (e.g. a disposable test registry).

## Where verification is enforced

Signing only helps if something **checks** the signature. In a real system that
happens at deploy time via an admission controller, e.g. Kubernetes with
**Sigstore Policy Controller** or **Kyverno**, configured to reject any image
lacking a valid signature from this pipeline's identity. That enforcement is
out of scope here and was not configured.

## Lab boundary

- No image was pushed, so nothing was signed.
- No Cosign signature or attestation was created; no Rekor entry exists.
- No verification policy was applied anywhere.
