# GitOps Environment Template

The per-service GitOps wiring. For each new service the generator emits one Argo
CD `Application` per environment. Git is the single source of truth: a controller
(Argo CD or Flux) continuously reconciles the cluster to match these files.
**No controller exists here and nothing was synced or deployed.**

```text
environments/
  dev/application.yaml    auto-sync (prune + selfHeal) → fast feedback
  prod/application.yaml   manual sync → human-gated promotion
```

## How it deploys (conceptually)

```
developer merges config change
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

## Dev vs prod differences

| | dev | prod |
| --- | --- | --- |
| values | `values.yaml` + `values-dev.yaml` | `values.yaml` + `values-prod.yaml` |
| namespace | `__SERVICE_NAME__-dev` | `__SERVICE_NAME__-prod` |
| sync | automated (prune, selfHeal) | manual (gated) |

Both use `finalizers` for deletion protection and `CreateNamespace=true`. All
repo URLs are `example.invalid` placeholders.
