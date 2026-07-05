# Secure CI/CD Pipeline

*A security-hardened CI/CD pipeline for a Java container — compile, test, build, dependency scan, image scan, SBOM, and keyless signing, with security evidence at every stage.*

## Problem this project solves

Most pipelines stop at "build and push." That ships known-vulnerable
dependencies and base images, with no record of what's inside and no proof the
image is genuine. This project shows a **secure supply chain**: gate the build on
vulnerability scans, generate a **Software Bill of Materials (SBOM)**, and
**cryptographically sign** the image — so consumers can verify integrity and
provenance.

## Technologies & concepts

- **GitHub Actions** — a 10-stage pipeline (template, non-executed)
- **Trivy** — dependency (SCA) + container image vulnerability scanning
- **Syft** — CycloneDX SBOM generation
- **Cosign (Sigstore)** — keyless image signing via OIDC (no long-lived keys)
- **Java 21** + a hardened multi-stage **Dockerfile**; least-privilege, pinned actions

## Architecture overview

```
checkout → setup Java → compile → test → build image (push:false)
   → dependency scan (Trivy fs)   ─┐  HIGH/CRITICAL fails the build
   → image scan (Trivy image)     ─┘
   → SBOM (Syft, CycloneDX)
   → image signing concept (Cosign keyless, OIDC)
   → artifact upload (SBOM + scan results)
```

## Project structure

```text
src/securecicdpipeline/Main.java + BuildInfo.java   app + testable logic
test/securecicdpipeline/BuildInfoTest.java          dependency-free assertions
Dockerfile                                          multi-stage, non-root, OCI labels
ci/github-actions.example.yml                       the 10-stage pipeline (parked, inert)
ci/security-policy.yml                              required-checks / approval policy
docs/security-pipeline.md  docs/sbom.md  docs/image-signing.md
README.md  TESTING.md
```

## Important files explained

- **ci/github-actions.example.yml** — the full pipeline. Lives under `ci/` (not `.github/workflows/`) so GitHub never runs it, and is `workflow_dispatch`-only as a second safety layer.
- **BuildInfo.java / BuildInfoTest.java** — small pure logic (semver + traceable `version-gitsha` image ref) so the compile/test stages exercise something real.
- **Dockerfile** — multi-stage (JDK never ships), non-root user, OCI provenance labels.
- **docs/** — `security-pipeline.md` (stage-by-stage), `sbom.md` (what/why), `image-signing.md` (Cosign keyless flow + verification).

## How it would work in a real environment

On push, CI compiles and tests, builds the image, then runs two **failing gates**
(Trivy `fs` + `image`, HIGH/CRITICAL). If clean, Syft emits a CycloneDX SBOM,
Cosign signs the pushed image **by digest** using the workflow's OIDC identity
(logged in Rekor), and the SBOM + scan results are uploaded as evidence.

## What was prepared but NOT executed

Prepared: the app + test, hardened Dockerfile, the 10-stage workflow, a
required-checks policy, and three security docs. **Not executed:** no pipeline
run, no `docker build`, no Trivy/Syft/Cosign, no push, no registry/OIDC. **No
"passing" result is claimed and there is no status badge.**

## Security notes

- **No real secrets/tokens** — real runs would use OIDC + repo secrets, never literals.
- Scans run as **build-failing gates** (`severity: HIGH,CRITICAL`, `exit-code: 1`).
- Least-privilege `permissions:` (elevated only where needed); pinned action versions.
- `push: false` means nothing is published, so there's deliberately no real image to sign.
- Immutable, traceable `version-gitsha` image tags.

## Limitations

- The pipeline was never run; scans/SBOM/signing produced no real artifacts.
- No SAST/secret-scanning stage yet; signing is documented, not performed (`push:false`).
- Local Java compilation was not run (no JDK on the authoring machine).

## Future improvements

- Add SAST + secret scanning; enforce signatures at deploy time (Kyverno / Sigstore Policy Controller).
- SLSA provenance attestations alongside the SBOM.
- Promote by digest across environments; break-glass exception workflow.

## What I learned

- Why **dependency + image scanning** catch different vulnerability classes.
- What an **SBOM** is and why signed attestations make it trustworthy.
- **Keyless signing** (OIDC → Fulcio → Rekor) and why it beats long-lived keys.
- Pipeline security hygiene: least privilege, pinned actions, no embedded secrets.
