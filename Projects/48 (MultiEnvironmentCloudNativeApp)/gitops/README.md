# gitops/

The delivery layer. **Git is the single source of truth**; an Argo CD controller
continuously reconciles each cluster/namespace to match the manifests in this
repo. **No controller exists here and nothing was synced or deployed.**

Two equivalent ways to wire it up (pick one):

| File | Model |
| --- | --- |
| `app-of-apps.yaml` | one root Application that discovers `environments/*/application.yaml` and creates a child app per env |
| `applicationset.example.yaml` | one ApplicationSet that generates the three env apps from a list |

Both deploy the **Kustomize overlays** in [../k8s/overlays/](../k8s/overlays/):
dev and staging auto-sync; **prod is manual** (a human-gated release).

```
Git (desired state)
   │  Argo CD watches
   ▼
compare desired vs live  ──►  sync
   ├─ app-dev      (auto)
   ├─ app-staging  (auto)
   └─ app-prod     (manual sync = the release gate)
```

A **promotion** is just a Git change (bump the image tag in an overlay) that Argo
CD then rolls out. A **rollback** is `git revert` of that change (or an Argo CD
rollback to a previous synced revision). See
[../docs/gitops.md](../docs/gitops.md) and [../docs/promotion-model.md](../docs/promotion-model.md).
