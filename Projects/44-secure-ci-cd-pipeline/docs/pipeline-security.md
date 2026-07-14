# Pipeline Security

This document explains the security design of the pipeline defined in
[`../ci/github-actions.example.yml`](../ci/github-actions.example.yml) (annotated
example) and [`../.github/workflows/secure-pipeline.yml`](../.github/workflows/secure-pipeline.yml)
(runnable, manual-only copy). The required checks are declared in
[`../ci/security-policy.yml`](../ci/security-policy.yml).

## Goal

Ship a Java container image while producing **verifiable evidence** at every
step: no secrets in the tree, the code compiles warning-free and passes tests,
its dependencies and image are free of known High/Critical vulnerabilities, and
a Software Bill of Materials (SBOM) records exactly what is inside. Signing and
attestation are documented as the release-stage control that would let
consumers verify integrity and provenance.

## The pipeline stages

| # | Stage | Tool / action | What it protects against |
| - | ----- | ------------- | ------------------------ |
| 1 | checkout | `actions/checkout` | — (fetch source) |
| 2 | create artifact dir | `mkdir -p` | evidence written to unpredictable paths |
| 3 | secret scan | gitleaks (container) | committed credentials reaching the build |
| 4 | setup Java | `actions/setup-java` (Temurin 21) | inconsistent toolchain |
| 5 | static analysis + compile | `javac -Xlint:all -Werror` | warning-level defects shipping |
| 6 | test | plain-Java assertions | regressions shipping |
| 7 | build image | `docker build` (local only, never pushed) | — (produce artifact locally) |
| 8 | dependency scan | Trivy `fs` | known-vulnerable libraries (SCA) |
| 9 | image vuln scan | Trivy `image` | vulnerable OS/app packages in the image |
| 10 | SBOM | Syft (CycloneDX) | "what's actually inside?" opacity |
| 11 | signing design note | echo only — see [image-signing.md](image-signing.md) | tampered / spoofed images (release stage) |
| 12 | artifact upload | `actions/upload-artifact` | losing the audit evidence |

All gates are **fail-closed**: gitleaks fails on any finding, `-Werror` fails on
any compiler warning, and both Trivy scans run with `exit-code: "1"` and
`severity: HIGH,CRITICAL`, so a High or Critical finding fails the build and
blocks every later stage — a vulnerable image is never inventoried, promoted,
or signed.

## Security principles baked in

- **Least-privilege CI permissions.** The workflow's `permissions:` is
  `contents: read` only. It pushes nothing and signs nothing, so it needs
  nothing more. A release job doing keyless signing would add
  `id-token: write`; SARIF upload to code scanning would add
  `security-events: write`. Widening happens per-job, never globally.
- **No embedded secrets.** No tokens, registry credentials, or production
  endpoints appear anywhere. Real runs would inject credentials via the CI
  secret store (GitHub environments/secrets) or, better, short-lived OIDC
  federation — never literals in the repo. Secrets injected by the CI system
  are automatically masked in logs; the pipeline additionally avoids echoing
  environment contents, because leaked build logs are a common exfiltration
  path.
- **Versioned tool references (honest limitation).** Actions and scanners use
  versioned references (`actions/checkout@v4`, `trivy-action@0.28.0`) for
  readability. These are **not** immutable SHA pins. A production pipeline
  should pin actions to full commit SHAs and update them through a controlled
  dependency process (e.g. Dependabot/Renovate with human review).
- **Traceable image tags.** Images are tagged `version-gitsha`
  (`secure-cicd-java-app:0.1.0-<sha>`, see `BuildInfo.imageReference`) so every
  image traces to an exact commit. Tags are traceable, **not inherently
  immutable** — a registry can allow a tag to be repointed. Production
  promotion and signing must therefore use the **image digest**
  (`@sha256:...`), which cannot be repointed.
- **No push, no sign in this lab.** The image is built and scanned locally on
  the runner and never pushed, so there is deliberately no registry digest to
  sign. Signing/attestation is documented, not performed.
- **Artifact retention.** Every tool writes into `artifacts/` and the final
  step uploads exactly that folder (`if-no-files-found: error`), so a "green"
  run guarantees the evidence exists: `trivy-deps.sarif`, `trivy-image.sarif`,
  `sbom.cyclonedx.json`.

## Why two complementary scans

Most vulnerabilities in a container come from **transitive dependencies** and
the **base image**, not first-party code:

- the **dependency (SCA) scan** reads the source tree and flags libraries with
  known CVEs — the class of issue behind incidents like Log4Shell;
- the **image scan** inspects the assembled image's OS packages and layers,
  catching a vulnerable base image even when the app's own dependencies are
  clean.

## Digest-based promotion

Passing this pipeline should never directly equal "deployed." In production the
built image is pushed once, its **digest** is captured, and every later
environment (staging → prod) promotes **that digest**, verified against its
signature. Rebuilding per environment or promoting by tag breaks the guarantee
that what was scanned and signed is what runs.

## What is implemented in this lab vs production-only

**Implemented and locally validated** (see [../TEST_RESULTS.md](../TEST_RESULTS.md)):
secret scan, static analysis + compile, tests, local Docker build, Trivy
fs/image scans, Syft SBOM, artifact folder, fail-closed thresholds.

**Production-only (documented, not executed):** registry push, keyless signing
and SBOM attestation, signature verification at deploy time (admission
control), branch protection, registry tag immutability, SHA-pinned actions,
digest-pinned base images, incident response for failed gates.
