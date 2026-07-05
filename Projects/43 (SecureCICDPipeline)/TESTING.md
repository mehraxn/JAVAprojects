# Testing — Secure CI/CD Pipeline

> **Nothing was executed.** No pipeline run, `docker build`, Trivy/Syft/Cosign,
> or push occurred; **no "passing" result is claimed.** This documents static
> review and what a real runner *would* produce.

## 1. Static validation checklist

- [ ] App + test compile conceptually (`package securecicdpipeline`; fixed).
- [ ] All 10 stages present in order (checkout → … → artifact upload).
- [ ] Scan gates use `severity: HIGH,CRITICAL` and `exit-code: "1"`.
- [ ] Build step sets `push: false`; signing step only echoes commands.
- [ ] Action/tool versions are pinned.

## 2. File existence checks

- [ ] `src/securecicdpipeline/Main.java` + `BuildInfo.java`; `test/.../BuildInfoTest.java`
- [ ] `Dockerfile`
- [ ] `ci/github-actions.example.yml`, `ci/security-policy.yml`
- [ ] `docs/security-pipeline.md`, `docs/sbom.md`, `docs/image-signing.md`
- [ ] `README.md`, `TESTING.md`

## 3. YAML / config review checklist

- [ ] Workflow YAML well-formed; parked under `ci/` (not `.github/workflows/`).
- [ ] Trigger is `workflow_dispatch` only.
- [ ] Top-level `permissions: contents: read`; job elevates only where needed (`packages: write`, `id-token: write`).

## 4. Security checks

- [ ] **No real secrets** — no tokens/keys embedded.
- [ ] **No real credentials** — real runs would use OIDC + repo secrets.
- [ ] **No production endpoints** — registry is `example.invalid`/placeholder.
- [ ] Dockerfile: multi-stage, non-root user, OCI labels.

## 5. Commands normally used — NOT executed

```bash
# NOT executed
javac -d out src/securecicdpipeline/*.java
javac -cp out -d test-out test/securecicdpipeline/*.java && java -cp "out:test-out" securecicdpipeline.BuildInfoTest
docker build -t ghcr.io/example/secure-cicd-app:dev .
trivy fs . && trivy image ghcr.io/example/secure-cicd-app:dev
syft ghcr.io/example/secure-cicd-app:dev -o cyclonedx-json=sbom.cyclonedx.json
cosign sign --yes ghcr.io/example/secure-cicd-app@sha256:<digest>
```

## 6. Expected results in a proper environment

- Compile + `BuildInfoTest` pass; image builds locally (not pushed).
- Trivy fails the build on any HIGH/CRITICAL finding, blocking later stages.
- Syft emits a valid CycloneDX SBOM; it uploads as a build artifact.
- With a pushed image + OIDC, Cosign signs by digest and `cosign verify` succeeds.

## 7. Manual review checklist (portfolio quality)

- [ ] README explains scanning, SBOM, and signing in plain terms.
- [ ] Pipeline demonstrates least privilege + pinned actions.
- [ ] Clear that the workflow is a non-running template (no badge, no "passed").
- [ ] Every command marked NOT executed; no fake screenshots.
- [ ] Honest about missing SAST/secret-scanning and unperformed signing.
