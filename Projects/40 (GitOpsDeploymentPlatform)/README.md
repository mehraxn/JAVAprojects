# GitOps Deployment Platform

A complete learning example for deploying a small Java HTTP application through declarative Kubernetes configuration, environment overlays, and Argo CD Application definitions.

## Project goal

The project demonstrates how application source, a container image contract, Kubernetes manifests, packaging, environment configuration, and controller reconciliation can be represented as reviewed files in Git.

## What GitOps means

GitOps uses version-controlled declarative configuration as the source of truth for desired system state. Changes should enter through reviewed commits rather than undocumented manual cluster edits. A GitOps controller compares the selected Git revision with live state and reports or reconciles differences according to an explicit sync policy.

Git records desired state and change history; it does not automatically prove that an image is safe, a rollout is healthy, or a database change is reversible.

## Technologies and concepts

- Standard Java 21 and built-in `HttpServer`
- Multi-stage non-root container definition
- Kubernetes Deployment, Service, ConfigMap, probes, resources, and security settings
- Kustomize base with dev and production-design overlays
- Helm chart as an alternative packaging example
- Argo CD Application examples
- Drift detection, reconciliation, environment promotion, and Git-first rollback concepts

## Project structure

```text
app/src/gitopsdeploymentplatform/       Java application
docker/Dockerfile                       Container definition
k8s/base/                               Shared Kubernetes resources
k8s/overlays/dev/                       Development overlay
k8s/overlays/prod/                      Production-design overlay
helm/java-app/                          Alternative Helm packaging
gitops/argocd/                          Dev/prod Application examples
docs/gitops-flow.md                     Delivery and reconciliation flow
docs/rollback.md                        Git-first rollback runbook
README.md
TESTING.md
```

## Application contract

The Java application exposes:

- `GET /health` for liveness;
- `GET /ready` for readiness; and
- `GET /config` for non-sensitive environment/version visibility.

Configuration comes from `APP_PORT`, `APP_ENVIRONMENT`, and `APP_VERSION`. No application secret is required or included.

## Git as the source of truth

The Argo CD examples point to placeholder repository URLs and the environment overlay paths. The intended workflow publishes an immutable image first, then updates the reviewed image reference in Git. The controller should observe only the committed configuration selected by its Application resource.

The examples do not include credentials, repository tokens, cluster credentials, or Argo CD installation instructions.

## Dev and prod separation

- Dev uses one replica, a dev placeholder image tag, dev configuration, and an Application demonstrating self-healing with pruning disabled.
- Prod is explicitly design-only, uses three replicas and separate placeholder configuration, and has no automated sync policy.
- Each environment has its own overlay and Argo CD Application. No real production endpoint or cluster is referenced.

The shared base holds common workload behavior. Environment overlays should contain only justified differences.

## Helm chart

`helm/java-app` packages equivalent Deployment, Service, and ConfigMap behavior with parameterized values and a ConfigMap checksum rollout. The current Argo CD examples use Kustomize overlays, not Helm, to avoid two active deployment paths for one environment.

## Rollback approach

Rollback is Git-first: identify a reviewed healthy revision, revert the faulty desired-state commit, review the resulting diff, synchronize according to environment policy, and observe rollout health. Direct cluster changes are temporary and may be reverted by self-healing.

Database/schema compatibility, image retention, and controller availability must also be addressed before rollback can be considered reliable.

## Prepared but not executed

- Java source, Dockerfile, Kubernetes base/overlays, Helm chart, Argo CD Applications, and rollback documentation were prepared.
- Repository URLs, images, namespaces, and environment values are placeholders.
- Java, Docker, kubectl, Kustomize, Helm, Argo CD, and Kubernetes were not executed.
- Nothing was built, rendered, validated by those tools, synchronized, deployed, rolled back, or tested in a cluster.

## Possible future improvements

- Add Java unit tests and immutable image-digest promotion.
- Add repository/application projects and least-privilege Argo CD access.
- Add policy checks and signed artifact verification.
- Add sync windows, notifications, and measured rollout alerts.
- Add a separately reviewed secret-management workflow.
- Perform drift and rollback exercises in a disposable cluster.
