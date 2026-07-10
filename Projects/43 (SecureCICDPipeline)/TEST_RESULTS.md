# Test Results — Secure CI/CD Pipeline

Validation performed on **2026-07-09/10** on Windows 11 with Docker Desktop
(engine 29.4.2). No JDK, Trivy, Syft, Gitleaks, or Cosign was installed on the
host, so Java and all scanners ran inside their official Docker containers —
the same tools the pipeline uses, with real results. Generated evidence is
written to `artifacts/` (gitignored; regenerate with [TESTING.md](TESTING.md)).

## Java compile result — PASS

Run inside `eclipse-temurin:21-jdk`:

```
javac -Xlint:all -Werror -d out src/securecicdpipeline/*.java
```

Compiled cleanly with all lints enabled and warnings promoted to errors.

## Java test result — PASS

```
javac -Xlint:all -Werror -cp out -d test-out test/securecicdpipeline/*.java
java -cp out:test-out securecicdpipeline.BuildInfoTest
```

Output: `All BuildInfo checks passed.`

## Main app run result — PASS

```
java -cp out securecicdpipeline.Main
```

Output:

```
Secure CI/CD pipeline application
version: 0.1.0
commit:  0000000
would-build image: ghcr.io/example/secure-cicd-java-app:0.1.0-0000000
```

## Docker build result — PASS

```
docker build --build-arg APP_VERSION=0.1.0 --build-arg GIT_SHA=2585f6a \
  -t secure-cicd-java-app:0.1.0 .
```

Multi-stage build succeeded (including the `apk -U upgrade` hardening step
added after the image scan below found patched-upstream CVEs). The built
container was run and verified:

- app runs and prints its build info (`version: 0.1.0`, `commit: 2585f6a`)
- runs as the dedicated non-root user: `uid=10001(app) gid=101(app)`

## Trivy filesystem/dependency scan result — PASS (no findings)

Run via `aquasec/trivy:0.58.1` container:

```
trivy fs --severity HIGH,CRITICAL --exit-code 1 \
  --format sarif --output artifacts/trivy-fs.sarif .
```

Exit code 0 — no HIGH/CRITICAL findings. SARIF written to
`artifacts/trivy-fs.sarif`.

## Trivy image scan result — PASS (after fixing real findings)

Run via `aquasec/trivy:0.58.1` container:

```
trivy image --timeout 15m --severity HIGH,CRITICAL --exit-code 1 \
  --format sarif --output artifacts/trivy-image.sarif secure-cicd-java-app:0.1.0
```

The **first scan failed the gate** (exit code 1) with 5 HIGH findings in the
`eclipse-temurin:21-jre-alpine` base image's OS packages: CVE-2026-56131,
CVE-2026-56407, CVE-2026-56408 (libexpat < 2.8.2) and CVE-2026-2100
(p11-kit / p11-kit-trust). Alpine had already published patched packages, so
`RUN apk -U upgrade --no-cache` was added to the Dockerfile's runtime stage.
After rebuilding, the **rescan passed** (exit code 0, no HIGH/CRITICAL
findings), and the container was re-verified to run correctly as uid 10001.
SARIF written to `artifacts/trivy-image.sarif`.

This is the fail-closed gate working as designed: a vulnerable image was
blocked, fixed, and only then passed.

Two earlier scan attempts failed for infrastructure reasons (a connection
reset and then Trivy's default 5-minute timeout while downloading its ~600 MB
Java vulnerability DB on a slow connection); neither produced a result. The
cached DB made the successful runs fast.

## Syft SBOM generation result — PASS

Run via `anchore/syft:v1.18.1` container:

```
syft secure-cicd-java-app:0.1.0 -o cyclonedx-json=artifacts/sbom.cyclonedx.json
```

Exit code 0. Regenerated after the `apk upgrade` fix so it describes the
final image. Output validated as CycloneDX **1.6** JSON with **76 components**
(image OS packages + JRE contents).

## Gitleaks secret scan result — PASS

Run via `zricethezav/gitleaks:v8.18.4` container:

```
gitleaks detect --source . --no-git --redact
```

Output: `no leaks found` (exit code 0).

## Workflow YAML parse / static review result — PASS

All three YAML files parse successfully with PyYAML:

- `.github/workflows/secure-pipeline.yml`
- `ci/github-actions.example.yml`
- `ci/security-policy.yml`

Static review confirmed: `workflow_dispatch`-only trigger, `contents: read`
permissions, no push step, no real signing step, artifact paths all under
`artifacts/`, and every required check in `ci/security-policy.yml` maps to a
named workflow step.

## GitHub Actions run result — NOT RUN

The workflow was **not** executed on GitHub. It ships inside this project
folder (not the repository root `.github/workflows/`), so GitHub does not
discover it. No pipeline status is claimed.

## Cosign / signing result — NOT RUN

Cosign is not installed and was not run. No image was pushed, so no registry
digest exists to sign. Signing and attestation are documented as a
release-stage design in [docs/image-signing.md](docs/image-signing.md); **no
signed artifact exists**.

## Known limitations

- All tools ran via Docker containers rather than host installs; results are
  equivalent but versions are the container tags listed above.
- The GitHub Actions workflow itself has only been YAML-parsed and reviewed,
  not executed on a GitHub runner.
- Vulnerability scan results are a point-in-time snapshot (2026-07); new CVEs
  in `eclipse-temurin:21-jre-alpine` may appear later.
- Nothing was pushed to any registry; nothing was signed; no deployment
  occurred.
