# GitOps

How deployment works. **No GitOps controller exists here and nothing was
synced or deployed.**

## The model

Git is the **single source of truth** for desired state. A controller (Argo CD)
runs in the cluster, watches this repo, and continuously **reconciles** the live
cluster to match the manifests. Nobody runs `kubectl apply` by hand; you change
Git and the controller applies it.

```
you change Git  ─►  Argo CD notices  ─►  compares desired vs live  ─►  syncs
```

Benefits: every change is reviewed and audited (it's a commit), the cluster
self-heals back to Git if it drifts, and rollback is `git revert`.

## Wiring (see ../gitops/)

- **AppProject** (`gitops/appproject.yaml`) — defines the allowed example source
  repository and the `argocd`, `app-dev`, `app-staging`, and `app-prod`
  destinations. Apply it before either application bootstrap option.
- **app-of-apps** (`gitops/app-of-apps.yaml`) — one root Application that
  discovers `environments/*/application.yaml` and creates a child app per env.
- **ApplicationSet** (`gitops/applicationset.example.yaml`) — an equivalent
  single object that generates the three env apps from a list.

Each environment Application targets its Kustomize overlay in
[../k8s/overlays/](../k8s/overlays/):

| Env | Sync |
| --- | --- |
| dev | automated (prune + selfHeal) |
| staging | automated |
| prod | **manual** — the release gate |

## Promotion & rollback under GitOps

- **Promotion** = a Git change copying the verified image digest into the target
  overlay (see [promotion-model.md](promotion-model.md)); Argo CD rolls it out.
- **Rollback** = `git revert` that change, or `argocd app rollback` to a prior
  synced revision (see [rollback.md](rollback.md)).

## What was NOT done

- No Argo CD/Flux was installed; no Application was registered.
- No sync, prune, or self-heal occurred; nothing was deployed.
