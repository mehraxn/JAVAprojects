# Secure CI/CD Pipeline

*A secure CI/CD pipeline lab for a Java container — secret scan, static
analysis, compile/test gates, hardened Docker build, dependency and image
vulnerability scanning, SBOM generation, artifact evidence, and a documented
signing/attestation design with a supply-chain threat model.*

## Problem this project solves

Most pipelines stop at "build and push." That ships known-vulnerable
dependencies and base images, with no record of what's inside and no proof the
image is genuine. This project shows a **secure supply chain**: fail the build
on leaked secrets, compiler warnings, failing tests, or High/Critical
vulnerabilities; generate a **Software Bill of Materials (SBOM)**; retain the
evidence as build artifacts; and document how **keyless signing** would let
consumers verify integrity and provenance at release time.

## Pipeline

```
checkout → artifact dir → secret scan (gitleaks) → setup Java
   → static analysis + compile (javac -Xlint:all -Werror)
   → test
   → docker build (local only, never pushed)
   → dependency scan (Trivy fs)    ─┐  HIGH/CRITICAL fails the build
   → image scan (Trivy image)      ─┘
   → SBOM (Syft, CycloneDX)
   → signing/attestation design note (Cosign keyless — documented, not executed)
   → upload artifacts/ (SARIF scans + SBOM)
```

## Technologies & concepts

- **GitHub Actions** — manual-only (`workflow_dispatch`), least-privilege workflow
- **gitleaks** — secret scanning gate
- **javac `-Xlint:all -Werror`** — lightweight static-analysis gate
- **Trivy** — dependency (SCA) + container image vulnerability scanning
- **Syft** — CycloneDX SBOM generation
- **Cosign (Sigstore)** — keyless signing design (OIDC → Fulcio → Rekor)
- **Java 21** + a hardened multi-stage **Dockerfile** (non-root, minimal JRE)

## Project structure

```text
src/securecicdpipeline/Main.java + BuildInfo.java    app + testable logic
test/securecicdpipeline/BuildInfoTest.java           dependency-free assertions
Dockerfile                                           canonical image build (multi-stage, non-root)
.github/workflows/secure-pipeline.yml                runnable manual workflow (see activation below)
ci/github-actions.example.yml                        annotated example of the same pipeline
ci/security-policy.yml                               required checks mapped to workflow steps
docs/pipeline-security.md                            stage-by-stage security design
docs/threat-model.md                                 supply-chain threat model
docs/sbom.md  docs/image-signing.md                  SBOM and signing deep dives
TESTING.md  TEST_RESULTS.md                          how to validate + honest results
```

## What is implemented (and locally validated)

- Java compile + test, with all compiler lints promoted to errors
- Hardened multi-stage Docker image: JDK never ships, dedicated non-root user
  (uid 10001), OCI provenance labels, traceable `version-gitsha` tags
- Local image build and run
- Trivy filesystem and image vulnerability scans (fail-closed on HIGH/CRITICAL)
- Syft CycloneDX SBOM generation
- Gitleaks secret scanning
- `artifacts/` evidence folder wired through the workflow upload step
- Required-checks policy (`ci/security-policy.yml`) matching the actual steps
- Supply-chain threat model with per-threat mitigations

Actual command results are in [TEST_RESULTS.md](TEST_RESULTS.md) — only what
was really run is claimed. Notably, the image scan gate **caught 5 real HIGH
CVEs** in the base image's OS packages during validation; the Dockerfile now
applies Alpine security updates and the rescan passes clean.

## What is example-only (documented, not executed)

- Registry push (the workflow builds with no push, by design)
- Real keyless signing and SBOM attestation (needs a pushed digest + OIDC)
- Production deployment and deploy-time signature verification
- Branch protection / required status checks
- Registry tag immutability

## Security honesty notes

- **Actions are versioned, not SHA-pinned.** `actions/checkout@v4` etc. are
  used for readability; production should pin full commit SHAs and update them
  through a controlled dependency process.
- **Base images are versioned, not digest-pinned.** Production should pin
  `FROM image@sha256:...` and bump digests through review.
- **Image tags are traceable, not inherently immutable.** The demo uses
  traceable `version-gitsha` tags for local builds; registries can allow tags
  to be repointed. In production, releases should be promoted and signed by
  **immutable image digest**.
- **No real secrets anywhere.** Real runs would use the CI secret store or
  OIDC federation, never literals.

## How to validate locally

See [TESTING.md](TESTING.md) for the full list. Quick version:

```bash
javac -Xlint:all -Werror -d out src/securecicdpipeline/*.java
javac -Xlint:all -Werror -cp out -d test-out test/securecicdpipeline/*.java
java -cp out:test-out securecicdpipeline.BuildInfoTest
docker build -t secure-cicd-java-app:0.1.0 .
```

## Running the workflow

[`.github/workflows/secure-pipeline.yml`](.github/workflows/secure-pipeline.yml)
ships **inside this project folder**, so GitHub does not discover it until you
copy it to the repository root's `.github/workflows/` and set `PROJECT_DIR` to
this folder's path relative to the repo root. It is manual-only
(`workflow_dispatch`), runs with `contents: read` permissions, pushes nothing,
and signs nothing. [`ci/github-actions.example.yml`](ci/github-actions.example.yml)
is the same pipeline with full annotations, kept inert under `ci/`.

## What NOT to run

Do not push images or run `cosign sign`/`cosign attest` unless you have
deliberately configured a disposable registry and OIDC trust — see
[docs/image-signing.md](docs/image-signing.md). Nothing in this project does
so by default.

## Resume Value

Designed a security-gated CI/CD example covering Java tests, secret scanning, dependency/container scanning, SBOM generation, image build evidence, least-privilege workflow settings, and documented signing design.

## What I learned

- Why **dependency + image scanning** catch different vulnerability classes.
- What an **SBOM** is and why signed attestations make it trustworthy.
- **Keyless signing** (OIDC → Fulcio → Rekor) and why it beats long-lived keys.
- Pipeline hygiene: least privilege, fail-closed gates, artifact evidence, and
  being precise about what is actually pinned, immutable, or signed.
