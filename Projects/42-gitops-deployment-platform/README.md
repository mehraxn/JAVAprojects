# GitOps Deployment Platform

*A local GitOps deployment platform lab for a Java service — Kubernetes base
manifests, Kustomize dev/prod overlays, a Helm packaging alternative, Argo CD
Application + AppProject examples with a dev auto-sync / prod manual-sync
gate, and a Git-first rollback workflow.*

## Problem this project solves

Deploying by hand (`kubectl apply` from a laptop) is unauditable, drifts from
what's in Git, and is hard to roll back. This project shows the **GitOps**
alternative: the desired state of every environment lives in reviewed Git
files, and a controller continuously reconciles the cluster to match. Every
change is a commit — reviewable, revertible, and the same for everyone.

## What it demonstrates

- **Kubernetes base manifests** — Deployment/Service/ConfigMap with probes,
  resource limits, non-root + read-only rootfs (writable `/tmp` emptyDir)
- **Kustomize overlays** — one base, `dev`/`prod` deltas only (replicas,
  config, namespace); namespaces declared in Git
- **Helm chart** — the same workload as an alternative packaging, with
  `values-dev.yaml` / `values-prod.yaml`
- **Argo CD examples** — one Application per environment plus an AppProject
  guardrail; **dev auto-syncs (self-heal on, prune off), prod is manual** —
  the human click is the release gate
- **Git-first rollback** — revert the desired-state commit, review the diff,
  sync (see [docs/rollback.md](docs/rollback.md))
- **Environment-specific config** — the app reports its environment/version
  at `/config`, so you can see which overlay produced a running pod

## Architecture

```
 developer commits desired state
          │
          ▼
   Git repo (base + overlays)  ◀── single source of truth
          │  Argo CD watches
          ▼
 compare desired vs live  ──►  sync
   ├─ dev   Application  (auto-sync, self-heal, prune off)
   └─ prod  Application  (manual sync = release gate)
          │
          ▼
   Kubernetes rollout → health observed
```

## Project structure

```text
app/src/gitopsdeploymentplatform/   Java app (/health /ready /config, exact routes)
docker/Dockerfile                   multi-stage non-root image
k8s/base/                           shared Deployment/Service/ConfigMap
k8s/overlays/dev/                   namespace java-app-dev, 1 replica, dev config
k8s/overlays/prod/                  namespace java-app-prod-design-only, 3 replicas
helm/java-app/                      equivalent Helm chart + dev/prod values
gitops/argocd/                      Application examples + AppProject guardrail
docs/gitops-flow.md  docs/rollback.md
README.md  TESTING.md  TEST_RESULTS.md
```

## What is implemented (and validated — see TEST_RESULTS.md)

Java app (exact route matching: `/health/test` is 404, wrong methods are
405), Dockerfile, Kustomize base + overlays with Git-declared namespaces,
Helm chart + per-env values, Argo CD Application/AppProject examples, and the
validation commands in [TESTING.md](TESTING.md). Compile, endpoint tests,
Docker build, both kustomize renders, and helm lint/template were all
actually run and passed (2026-07-10).

## What is not automatically proven

- **Argo CD sync was not run.** The Application/AppProject manifests were
  reviewed and rendered but never applied to a real Argo CD instance; drift
  detection, self-heal, and the manual prod gate are design assertions until
  you connect one.
- **No real production cluster** and no deployment to any cluster.
- **No registry push** — the image exists only locally.
- **No digest-based promotion** — see image honesty below.

## Image honesty

- The lab uses the versioned local tag **`gitops-java-app:0.1.0`** for
  readability. Tags are **traceable, not inherently immutable** — a registry
  can allow a tag to be repointed.
- In production, CI should publish an **image digest** and GitOps should
  promote that immutable digest through environments (the kustomize `images`
  transformer supports `newDigest` exactly for this).
- A real registry can enforce tag immutability, but this lab does not depend
  on that.

## Argo CD paths

The example Applications use
`path: Projects/42-gitops-deployment-platform/k8s/overlays/dev` (and `…/prod`), which
assumes the repository root contains this project folder. If this project *is*
the whole repository, shorten to `k8s/overlays/dev`; in a monorepo with a
parent folder, prefix accordingly. The `repoURL` is a placeholder
(`example.invalid`) — point it at your fork to try it for real.

## How to validate

```bash
# Java (exact commands + expected codes in TESTING.md)
javac --add-modules jdk.httpserver -d out app/src/gitopsdeploymentplatform/*.java

# Docker (context is the project root)
docker build -f docker/Dockerfile -t gitops-java-app:0.1.0 .

# Kustomize
kubectl kustomize k8s/overlays/dev
kubectl kustomize k8s/overlays/prod

# Helm
helm lint helm/java-app
helm template java-app helm/java-app -f helm/java-app/values-dev.yaml
```

Full command list: [TESTING.md](TESTING.md). Actual recorded results:
[TEST_RESULTS.md](TEST_RESULTS.md).

## Security notes

- No secrets, tokens, registry, or cluster credentials anywhere; repo URLs
  are `example.invalid`.
- Container runs non-root (uid 10001) with read-only rootfs and dropped
  capabilities.
- Prod sync is manual (human-gated); dev auto-sync runs with prune disabled.
- The AppProject example restricts sources, destinations, and resource kinds;
  production should restrict further (RBAC roles, sync windows).

## Resume Value

Structured a GitOps portfolio project with a containerized Java workload, environment-specific Kustomize overlays, Helm packaging, Argo CD application examples, and validated render paths without claiming cluster synchronization.

## Future improvements

- Immutable image-digest promotion wired into CI.
- Policy checks (Kyverno/OPA) and signed-artifact verification (Cosign).
- Sync windows, notifications, and rollout health alerts.
- A separately reviewed secret-management workflow (sealed-secrets /
  External Secrets).
- Drift and rollback drills against a disposable cluster with a real Argo CD.

## What I learned

- Why **Git as the source of truth** makes deployments auditable and
  reversible.
- Structuring environments with a **Kustomize base + overlays** instead of
  copy-pasted YAML — including declaring namespaces in Git so
  `CreateNamespace` magic isn't needed.
- The trade-off between **auto-sync (dev)** and **manual gates (prod)**, and
  why prune/self-heal need care.
- That "in Git" ≠ "healthy": images, schemas, and rollout health still need
  separate verification.
