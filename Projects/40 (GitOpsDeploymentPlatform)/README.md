# GitOps Deployment Platform

*Declarative Kubernetes delivery for a Java service using Kustomize environment overlays and Argo CD — Git as the single source of truth, with dev auto-sync and manual, human-gated production.*

## Problem this project solves

Deploying by hand (`kubectl apply` from a laptop) is unauditable, drifts from
what's in Git, and is hard to roll back. This project shows the **GitOps**
alternative: the desired state of every environment lives in reviewed Git files,
and a controller continuously reconciles the cluster to match. Every change is a
commit — reviewable, revertible, and the same for everyone.

## Technologies & concepts

- **Java 21** (built-in `HttpServer`, no framework) — a config-aware service
- **Docker** — multi-stage, non-root image
- **Kubernetes** — Deployment, Service, ConfigMap, probes, resources, security context
- **Kustomize** — one base + `dev`/`prod` overlays (no duplicated YAML)
- **Helm** — an equivalent chart, shown as an alternative packaging
- **Argo CD** — `Application` per environment; drift detection, reconciliation, Git-first rollback

## Architecture overview

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
app/src/gitopsdeploymentplatform/   Java app (/health, /ready, /config)
docker/Dockerfile                   multi-stage non-root image
k8s/base/                           shared Deployment/Service/ConfigMap
k8s/overlays/dev/                   dev replicas/tag/config patches
k8s/overlays/prod/                  prod (design-only) patches, manual sync
helm/java-app/                      equivalent Helm chart (alternative packaging)
gitops/argocd/                      dev + prod Argo CD Application examples
docs/gitops-flow.md  docs/rollback.md
README.md  TESTING.md
```

## Important files explained

- **k8s/base/** — the common workload; overlays only carry justified per-env deltas.
- **k8s/overlays/{dev,prod}/** — dev = 1 replica + dev tag/config; prod = 3 replicas, separate config, **no automated sync**.
- **gitops/argocd/*-application.example.yaml** — point Argo CD at a placeholder repo + the overlay path; dev self-heals, prod is manual.
- **helm/java-app/** — parameterized Deployment/Service/ConfigMap with a config-checksum rollout; kept as an alternative so there's only one *active* delivery path (Kustomize).
- **docs/gitops-flow.md / rollback.md** — the reconciliation flow and the Git-first rollback runbook.

## How it would work in a real environment

CI builds and publishes an **immutable image**, then a commit updates the image
tag in the target overlay. Argo CD detects the change, renders the overlay,
diffs against the cluster, and syncs — automatically in dev, on a human's click
in prod. Drift (a manual cluster edit) is reverted by self-heal in dev. Rollback
is `git revert` of the desired-state commit.

## What was prepared but NOT executed

Prepared and statically reviewable: Java app, Dockerfile, Kustomize base +
overlays, Helm chart, Argo CD Applications, and rollback docs. **Not executed:**
no `javac`/`docker`/`kubectl`/`kustomize`/`helm`/`argocd` ran; nothing was built,
rendered, synced, deployed, or rolled back. Repo URLs, images, and namespaces are
placeholders.

## Security notes

- No secrets, tokens, registry, or cluster credentials anywhere; repo URLs are `example.invalid`.
- Container runs **non-root** with a read-only root filesystem and dropped capabilities.
- **Prod sync is manual** (human-gated); dev auto-sync runs with **prune disabled** to avoid accidental deletion.
- The app needs no secret; `/config` exposes only non-sensitive values.

## Limitations

- Static review can't verify Kustomize/Helm rendering, CRD availability, sync health, drift correction, or rollout success.
- No image signing, policy checks, or measured rollout alerts.
- Rollback reliability also depends on DB/schema compatibility and image retention — out of scope here.

## Future improvements

- Immutable image-digest promotion + Argo CD Projects with least-privilege RBAC.
- Policy checks (Kyverno/OPA) and signed-artifact verification (Cosign).
- Sync windows, notifications, and rollout health alerts.
- A separately reviewed secret-management workflow (sealed-secrets / External Secrets).
- Drift and rollback drills in a disposable cluster.

## What I learned

- Why **Git as the source of truth** makes deployments auditable and reversible.
- Structuring environments with a **Kustomize base + overlays** instead of copy-pasted YAML.
- The trade-off between **auto-sync (dev)** and **manual gates (prod)**, and why prune/self-heal need care.
- That "in Git" ≠ "healthy": images, schemas, and rollout health still need separate verification.
