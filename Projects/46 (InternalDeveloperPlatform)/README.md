# Internal Developer Platform

Starter structure for a small platform-engineering portfolio project centered on a reviewed Java-service golden path rather than a custom production portal.

## Structure

```text
src/templates/javaservice/
helm/java-service/
gitops/applications/java-service.example.yaml
ci/pipeline-template.example.yml
k8s/policies/resource-requirements.example.yaml
docs/platform-architecture.md
docs/golden-path.md
README.md
TESTING.md
```

## Status

Skeleton only. The template, chart, policy, pipeline, and GitOps application were not rendered, generated, synchronized, or deployed.

## Required confirmations

- Intended developer persona and self-service boundary
- Template input contract and ownership
- CI, registry, cluster, GitOps controller, policy engine, and secret strategy
- Support model, versioning, upgrade, exception, and deprecation processes
