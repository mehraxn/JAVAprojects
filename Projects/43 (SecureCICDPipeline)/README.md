# Secure CI/CD Pipeline

Starter structure for a Java delivery pipeline designed around least privilege, repeatable builds, security evidence, and explicit approval boundaries.

## Structure

```text
src/securecicdpipeline/
docker/Dockerfile.example
ci/github-actions.example.yml
ci/security-policy.yml
docs/pipeline-security.md
docs/threat-model.md
README.md
TESTING.md
```

## Status

Skeleton only. The example workflow is stored under `ci/`, is disabled, and is not discoverable as an active GitHub workflow. No build, scan, signing, publishing, or deployment stage ran.

## Required confirmations

- CI provider and repository-level workflow location
- Approved test, SAST, dependency, secret, SBOM, image, and signing tools
- Artifact registry, identities, permissions, and retention
- Manual approval and deployment-environment boundaries
