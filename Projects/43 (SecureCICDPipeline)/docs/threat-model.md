# Supply-Chain Threat Model

Threats to this project's build-and-release chain, what this lab does about
each, and what a production deployment would add. The lab's scope is the
pipeline in [`../ci/github-actions.example.yml`](../ci/github-actions.example.yml)
/ [`../.github/workflows/secure-pipeline.yml`](../.github/workflows/secure-pipeline.yml);
anything past "image built and scanned locally" is release-stage and
documented rather than executed.

## 1. Malicious or vulnerable dependency

- **Risk:** a library (direct or transitive) ships known CVEs or intentionally
  malicious code that ends up inside the image (Log4Shell class).
- **In this lab:** the app is deliberately dependency-free, and the Trivy `fs`
  scan gates the build on HIGH/CRITICAL findings in the source tree.
- **Production:** lockfile-based dependency review on every PR, an internal
  proxy/allowlist for artifact repositories, and SBOM-driven re-scanning when
  new CVEs are published.

## 2. Vulnerable base image

- **Risk:** the app code is clean but the OS packages in
  `eclipse-temurin:21-jre-alpine` carry exploitable CVEs.
- **In this lab:** the Trivy `image` scan inspects the assembled image's OS
  packages and layers and fails the build on HIGH/CRITICAL findings.
- **Production:** pin base images by digest, rebuild on a schedule to pick up
  patched bases through a reviewed digest bump, and prefer minimal/distroless
  runtimes.

## 3. Leaked CI secret

- **Risk:** a credential committed to the repo, or printed to build logs, is
  harvested and used to push malicious images or access infrastructure.
- **In this lab:** no secrets exist in the repo at all; the gitleaks stage
  fails the build if a credential-shaped string is ever committed; the
  pipeline never echoes environment contents.
- **Production:** short-lived OIDC federation instead of stored credentials
  where possible, CI secret store with environment scoping, log masking, and
  automatic rotation/revocation on suspected exposure.

## 4. Compromised GitHub Action

- **Risk:** a third-party action this workflow depends on is hijacked
  upstream (as in the 2025 `tj-actions/changed-files` incident) and exfiltrates
  the runner's token or tampers with the build.
- **In this lab:** actions are referenced by version tag and the workflow runs
  with `permissions: contents: read`, so even a hijacked action holds a
  read-only token; nothing can be pushed or published. Version tags are
  honestly documented as *not* immutable pins.
- **Production:** pin every action to a full commit SHA, update via a reviewed
  dependency process, and restrict allowed actions at the organization level.

## 5. Mutable image tag overwrite

- **Risk:** an attacker (or an accidental rebuild) repoints an existing tag
  such as `0.1.0-<sha>` to a different image, so "the same tag" no longer
  means the same bits.
- **In this lab:** tags are traceable (`version-gitsha`) but nothing is pushed,
  so no shared registry state exists to overwrite. Docs consistently state
  tags are traceable, not immutable.
- **Production:** enable registry tag immutability where supported, and make
  promotion and signature verification operate on the image **digest**, never
  the tag.

## 6. Unsigned image promotion

- **Risk:** a look-alike or tampered image is deployed because nothing proves
  which pipeline produced it.
- **In this lab:** keyless signing (Cosign/Sigstore, by digest, via OIDC) and
  SBOM attestation are fully documented in
  [image-signing.md](image-signing.md) but not executed — there is no pushed
  image to sign.
- **Production:** sign every release by digest, and enforce verification at
  deploy time with an admission controller (Sigstore Policy Controller or
  Kyverno) that rejects unsigned or wrongly-signed images.

## 7. Tampered artifact / evidence

- **Risk:** scan results or the SBOM are altered after generation to hide
  findings, or evidence silently goes missing.
- **In this lab:** every tool writes into a single `artifacts/` folder created
  by the pipeline, and the upload step ships exactly that folder with
  `if-no-files-found: error` — a green run cannot lack its evidence.
- **Production:** attach the SBOM to the image as a signed attestation
  (`cosign attest`) so evidence is cryptographically bound to the digest, and
  retain artifacts in write-once storage.

## 8. Developer bypassing scan gates

- **Risk:** a developer merges or releases without the scans running, or
  force-pushes around a red pipeline.
- **In this lab:** gates are fail-closed within the workflow (any failing
  stage blocks all later stages), and
  [`../ci/security-policy.yml`](../ci/security-policy.yml) names each required
  check. Enforcement outside the workflow (branch rules) is out of scope.
- **Production:** branch protection with required status checks matching the
  policy file, no direct pushes to release branches, and deploy-time signature
  verification as the backstop that makes bypass useless.

## 9. Overly broad CI permissions

- **Risk:** a compromised step (see threat 4) uses a powerful default
  `GITHUB_TOKEN` to push code, publish packages, or tamper with releases.
- **In this lab:** top-level and job-level `permissions:` are `contents: read`
  only; the workflow is manual-only (`workflow_dispatch`), pushes nothing, and
  needs no write scopes. `id-token: write` is documented as needed only by a
  real signing job.
- **Production:** per-job least privilege, environment protection rules with
  required reviewers for release jobs, and org-level defaults of read-only
  tokens.

## Residual risks (accepted in this lab)

- Version-tagged actions and base images could change upstream; accepted for
  readability and called out wherever pinning is mentioned.
- No branch protection or deploy-time verification exists here, because the
  lab has no protected branches or deploy target.
- The threat model covers the build chain only; runtime threats (container
  escape, network policy) are out of scope.
