# dev environment

| Property | Value |
| --- | --- |
| Namespace | `app-dev` |
| Replicas | 1 |
| Resources | 50m/64Mi → 250m/128Mi |
| Config | `LOG_LEVEL=DEBUG`, `FEATURE_NEW_UI=true` |
| Sync | **automated** (prune + selfHeal) |
| Approval | none — merge to `main` deploys here |

First stop in the promotion chain. Config lives in
[../../k8s/overlays/dev/](../../k8s/overlays/dev/); delivery is
[application.yaml](application.yaml); secrets come from a real Secret modeled by
[secret.example.yaml](secret.example.yaml) (placeholders only). See
[../../docs/promotion-model.md](../../docs/promotion-model.md).
