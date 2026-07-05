# Secure CI/CD Pipeline

This document explains the pipeline defined in
[`../ci/github-actions.example.yml`](../ci/github-actions.example.yml). **The
pipeline was never executed** — it lives under `ci/` (not the repo-root
`.github/workflows/`) so GitHub does not discover or run it, and its trigger is
manual `workflow_dispatch` as a second safety layer.

## Goal

Ship a Java container image while producing **verifiable evidence** at every
step: the code compiles and passes tests, its dependencies and image are free of
known High/Critical vulnerabilities, a Software Bill of Materials (SBOM) records
exactly what is inside, and the released image is cryptographically signed so
consumers can prove it came from this pipeline and was not tampered with.

## The ten stages

| # | Stage | Tool / action | What it protects against |
| - | ----- | ------------- | ------------------------ |
| 1 | checkout | `actions/checkout` | — (fetch source) |
| 2 | setup Java | `actions/setup-java` (Temurin 21) | inconsistent/unpinned toolchain |
| 3 | compile | `javac` | broken builds reaching later stages |
| 4 | test | plain-Java assertions | regressions shipping |
| 5 | build image | `docker/build-push-action` (`push: false`) | — (produce artifact locally) |
| 6 | dependency scan | Trivy `fs` | known-vulnerable libraries (SCA) |
| 7 | image vuln scan | Trivy `image` | vulnerable OS/app packages in the image |
| 8 | SBOM | Syft (CycloneDX) | "what's actually inside?" opacity |
| 9 | image signing | Cosign (keyless) | tampered / spoofed images |
| 10 | artifact upload | `actions/upload-artifact` | losing the audit evidence |

Stages 6 and 7 are **gates**: they run with `exit-code: "1"` and
`severity: HIGH,CRITICAL`, so a High or Critical finding **fails the build** and
blocks stages 8–10. See [sbom.md](sbom.md) and [image-signing.md](image-signing.md)
for stages 8 and 9.

## Security principles baked in

- **Least privilege.** The workflow's top-level `permissions:` is `contents: read`.
  The job widens to `packages: write` (registry push) and `id-token: write`
  (keyless signing OIDC) **only** where needed.
- **No embedded secrets.** No tokens, registry credentials, or production
  endpoints appear anywhere. Real runs would use GitHub OIDC and repo/environment
  secrets, not literals.
- **Pinned versions.** Actions and scanner versions are pinned so a supply-chain
  change upstream cannot silently alter the build.
- **Immutable, traceable tags.** Images are tagged `version-gitsha` (see
  `BuildInfo.imageReference`) so every image traces to an exact commit.
- **Push disabled in the template.** `push: false` means nothing leaves the
  runner; there is deliberately no real image to sign or publish.

## Why scanning matters

Most vulnerabilities in a container come from **transitive dependencies** and the
**base image**, not from first-party code. Two complementary scans catch both:

- **Dependency (SCA) scan** reads the source tree / lockfiles and flags libraries
  with known CVEs — the class of issue behind incidents like Log4Shell.
- **Image scan** inspects the assembled image's OS packages and layers, catching
  a vulnerable base image even when your own dependencies are clean.

Failing the build on High/Critical turns "we should patch someday" into a hard
gate, so vulnerable images never get published or signed.

## What was prepared but NOT executed

- No workflow run occurred (file parked under `ci/`, manual trigger only).
- No Docker image was built.
- No Trivy scan ran against any real filesystem or image.
- No SBOM was generated.
- No image was signed or pushed; no registry or OIDC identity was used.
- No status badge is claimed and no "passing" result is asserted.
