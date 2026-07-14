# Testing — Secure CI/CD Pipeline

Safe local validation commands for this project. Everything below runs
locally: nothing is pushed to a registry, nothing is signed, and no secrets
are needed. Actual results from running these commands are recorded honestly
in [TEST_RESULTS.md](TEST_RESULTS.md).

Run all commands from this project folder. Commands use POSIX shell syntax;
on Windows, use Git Bash or adapt paths for PowerShell (`out;test-out` for the
classpath separator).

## A) Java compile and test

Requires JDK 21 (or run inside a `eclipse-temurin:21-jdk` container).

```bash
# Static analysis + compile: all lints on, warnings are errors
javac -Xlint:all -Werror -d out src/securecicdpipeline/*.java

# Compile and run the dependency-free test
javac -Xlint:all -Werror -cp out -d test-out test/securecicdpipeline/*.java
java -cp out:test-out securecicdpipeline.BuildInfoTest
```

Expected: `All BuildInfo checks passed.`

No local JDK? Run the same thing in a container:

```bash
docker run --rm -v "$PWD:/w" -w /w eclipse-temurin:21-jdk sh -c \
  "javac -Xlint:all -Werror -d out src/securecicdpipeline/*.java && \
   javac -Xlint:all -Werror -cp out -d test-out test/securecicdpipeline/*.java && \
   java -cp out:test-out securecicdpipeline.BuildInfoTest"
```

## B) Run the main app

```bash
java -cp out securecicdpipeline.Main
```

Expected: it prints the app version, commit, and the traceable
`version-gitsha` image reference it would build.

## C) Docker build (local only — never pushed)

```bash
docker build \
  --build-arg APP_VERSION=0.1.0 \
  --build-arg GIT_SHA=$(git rev-parse --short HEAD) \
  -t secure-cicd-java-app:0.1.0 .

# Verify it runs and runs as non-root
docker run --rm secure-cicd-java-app:0.1.0
docker run --rm --entrypoint id secure-cicd-java-app:0.1.0   # expect uid=10001
```

## D) Trivy filesystem/dependency scan (optional)

```bash
mkdir -p artifacts
trivy fs --severity HIGH,CRITICAL --exit-code 1 \
  --format sarif --output artifacts/trivy-fs.sarif .
```

No local Trivy? Use the container:

```bash
docker run --rm -v "$PWD:/scan" aquasec/trivy:0.58.1 fs \
  --severity HIGH,CRITICAL --exit-code 1 \
  --format sarif --output /scan/artifacts/trivy-fs.sarif /scan
```

## E) Trivy image scan (optional, after C)

```bash
trivy image --severity HIGH,CRITICAL --exit-code 1 \
  --format sarif --output artifacts/trivy-image.sarif \
  secure-cicd-java-app:0.1.0
```

Container variant (needs the Docker socket to see the local image):

```bash
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
  -v "$PWD/artifacts:/out" aquasec/trivy:0.58.1 image \
  --severity HIGH,CRITICAL --exit-code 1 \
  --format sarif --output /out/trivy-image.sarif \
  secure-cicd-java-app:0.1.0
```

## F) Syft SBOM generation (optional, after C)

```bash
syft secure-cicd-java-app:0.1.0 -o cyclonedx-json=artifacts/sbom.cyclonedx.json
```

Container variant:

```bash
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
  -v "$PWD/artifacts:/out" anchore/syft:v1.18.1 \
  secure-cicd-java-app:0.1.0 -o cyclonedx-json=/out/sbom.cyclonedx.json
```

## G) Gitleaks secret scan (optional)

```bash
gitleaks detect --source . --no-git --redact
```

Container variant:

```bash
docker run --rm -v "$PWD:/scan:ro" zricethezav/gitleaks:v8.18.4 \
  detect --source /scan --no-git --redact
```

Expected: `no leaks found`.

## H) Workflow validation

The runnable workflow is [.github/workflows/secure-pipeline.yml](.github/workflows/secure-pipeline.yml)
inside this project folder. GitHub only discovers workflows in the
**repository-root** `.github/workflows/`, so to run it:

1. Copy it to `<repo-root>/.github/workflows/secure-pipeline.yml`.
2. Set its `PROJECT_DIR` env var to this folder's path relative to the repo
   root (for this repository, `Projects/44-secure-ci-cd-pipeline`).
3. On GitHub: **Actions → secure-cicd-pipeline → Run workflow** (it is
   `workflow_dispatch`-only and will never trigger on push).

It builds and scans everything on the runner, pushes nothing, and uploads the
`artifacts/` folder as the `security-evidence` build artifact.

To lint the YAML locally without running it:

```bash
docker run --rm -v "$PWD:/data" pipelinecomponents/yamllint \
  yamllint -d relaxed ".github/workflows/secure-pipeline.yml" "ci/"
```

## I) Cleanup

```bash
rm -rf out test-out artifacts
docker rmi secure-cicd-java-app:0.1.0   # optional
```

## What NOT to run

Do not run `cosign sign` / `cosign attest` or push any image unless you have
deliberately set up a disposable registry and OIDC — see
[docs/image-signing.md](docs/image-signing.md). Nothing in this file publishes
anything.
