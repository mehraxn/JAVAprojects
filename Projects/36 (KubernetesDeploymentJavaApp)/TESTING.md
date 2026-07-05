# Testing Kubernetes Deployment Java App

## Static checks

- Review selectors, labels, ports, and probe paths.
- Confirm no real secrets or image registries are present.
- Confirm Deployment and Service labels match.

## Deferred checks

- Compile the Java application and verify health endpoints.
- Build and scan the image.
- Validate manifests with approved tooling.
- Apply only to an approved disposable cluster.

No container or Kubernetes command was executed.
