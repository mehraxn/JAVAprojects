# Multi-Environment Cloud-Native App

*A resume-grade, end-to-end multi-environment structure for a Java service — one immutable image promoted through dev → staging → prod with per-environment config, GitOps delivery, and documented rollback.*

## Problem this project solves

Shipping straight to production, or rebuilding a different image per environment,
is how "worked in staging, broke in prod" happens. This project shows the
disciplined alternative: **build once, promote the same artifact**, with each
environment differing only in **config, scale, and secrets**, delivered by
**GitOps** and reversible by `git revert`.

## Technologies & concepts

- **Java 21** config-aware service (`/health`, `/ready`, `/config`) + multi-stage Dockerfile
- **Kustomize** base + `dev`/`staging`/`prod` overlays (per-env replicas/resources/config)
- **Helm** — an equivalent chart with `values-<env>.yaml` (alternative packaging)
- **Argo CD GitOps** — app-of-apps / ApplicationSet; dev+staging auto, prod manual
- **Promotion by immutable digest**; example secrets; rollback via Git

## Architecture overview

```
 build once (one image digest)
   → dev  (auto-sync)  → staging (auto-sync) → prod (manual approval + sync)
        └──────────── same immutable image the whole way ───────────┘
 Git = source of truth → Argo CD reconciles each namespace
```

## Project structure

```text
app/  Dockerfile                one service, one immutable image
k8s/base/                       shared manifests
k8s/overlays/{dev,staging,prod}/   per-env replicas, resources, config (Kustomize)
helm/app/                       equivalent chart (+ values-<env>.yaml)
environments/{dev,staging,prod}/   Argo CD Application + secret.example.yaml + README
gitops/                         app-of-apps root + ApplicationSet
ci/                             build-once + promotion pipeline templates
docs/environment-strategy.md  promotion-model.md  rollback.md  secrets.md  gitops.md
README.md  TESTING.md
```

## Important files explained

- **k8s/overlays/{dev,staging,prod}/** — patches for replicas (1/2/4), resources, and config (DEBUG/INFO/WARN, feature flag), all pinning the **same image tag**.
- **environments/<env>/** — per-env Argo CD `Application` (prod has no `automated:` block), `secret.example.yaml` (placeholders), and a properties README.
- **gitops/app-of-apps.yaml** / **applicationset.example.yaml** — two equivalent ways to generate the three env apps.
- **helm/app/** — the same app as a chart; `values-<env>.yaml` mirror the overlays.
- **ci/build.example.yml** (build once) + **promotion.example.yml** (promote the digest dev→staging→prod).

## How it would work in a real environment

CI builds one image and records its digest. A promotion PR sets the target
overlay's image tag to that digest; on merge Argo CD reconciles — dev and staging
automatically, prod on a human's manual sync. Config and scale for each
environment already live in its overlay, so a promotion never accidentally
changes them. Rollback is `git revert` (or `argocd app rollback`).

## What was prepared but NOT executed

Prepared: app + Dockerfile, Kustomize base/overlays, equivalent Helm chart,
per-env delivery + example secrets, GitOps app-of-apps/ApplicationSet, and
build/promote CI templates. **Not executed:** no image built, no
`kubectl`/`kustomize`/`helm` render or apply, no CI run, no Argo CD sync, no
secret created. **No environment was deployed.**

## Security notes

- **No real secrets** — only `*.example.yaml` with `REPLACE_ME` placeholders; `.gitignore` blocks real `secret.yaml`.
- **No real credentials / production endpoints** — registries/hosts are `example.invalid`.
- Non-root container, read-only rootfs, dropped capabilities.
- Real secrets would come from a sealed-secret / External Secrets Operator, never Git ([docs/secrets.md](docs/secrets.md)).

## Limitations

- Nothing was built, rendered, applied, or synced; no environment exists.
- Both Kustomize overlays and a Helm chart are provided (pick one for real use); GitOps here wires the overlays.
- Java compilation not run (no JDK on the authoring machine).

## Future improvements

- Real digest-based promotion automation and Argo CD Projects with least-privilege RBAC.
- Progressive delivery (Argo Rollouts) for staging→prod; policy-as-code gates.
- External Secrets Operator + sealed secrets; DB migration (expand/contract) automation.

## What I learned

- **Build once, promote the artifact** — why rebuilding per environment is a trap.
- Structuring per-environment differences cleanly with **Kustomize overlays** (and the Helm equivalent).
- **GitOps promotion** as reviewable Git changes, with dev-auto / prod-manual gates.
- Keeping **secrets out of Git** while staying GitOps-friendly.
