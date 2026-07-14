# GitOps

Platform-level GitOps wiring. **These are example manifests — no Argo CD is
running here and nothing was synced or deployed.**

## Files

```text
appproject.example.yaml   an Argo CD AppProject the generated services deploy into
```

Each generated service emits its own per-environment Argo CD `Application`
manifests under `<service>/gitops/app-dev.yaml` and `app-prod.yaml` (see the
golden-path template in [../template/gitops/](../template/gitops/) and the rendered
[../examples/new-service/gitops/](../examples/new-service/gitops/)). Those
Applications reference this project via `spec.project: platform-learning`.

## How it deploys (conceptually)

```
developer merges a config change
        │
        ▼
Git repo (desired state)
        │  Argo CD watches
        ▼
Argo CD compares desired vs live  ──► syncs the Helm chart into the namespace
        │
        ▼
dev: applied automatically   |   prod: applied when a human syncs
```

## Dev vs prod

| | dev | prod |
| --- | --- | --- |
| values | `values.yaml` + `values-dev.yaml` | `values.yaml` + `values-prod.yaml` |
| namespace | `<service>-dev` | `<service>-prod` |
| sync | automated (prune, selfHeal) | manual (human-gated) |

## About the finalizer

Both generated Applications carry the `resources-finalizer.argocd.argoproj.io`
finalizer. This makes Argo CD **clean up the resources it manages** when the
Application is deleted (a cascading delete of the managed workloads) — it is
convenience, not a safety brake.

Real deletion protection for production is handled elsewhere: RBAC on who may
delete Applications, pull-request review, and branch protection on the config
repo. The finalizer does not prevent deletion; it just makes deletion tidy.
