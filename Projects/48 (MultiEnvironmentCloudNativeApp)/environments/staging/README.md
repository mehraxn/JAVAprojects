# staging environment

| Property | Value |
| --- | --- |
| Namespace | `app-staging` |
| Replicas | 2 |
| Resources | 100m/128Mi → 500m/256Mi |
| Config | `LOG_LEVEL=INFO`, `FEATURE_NEW_UI=true` |
| Sync | **automated** (prune + selfHeal) |
| Approval | promoted from dev after checks pass |

Production-like rehearsal. The **same immutable image** that ran in dev is
promoted here (the exact same digest). Config: [../../k8s/overlays/staging/](../../k8s/overlays/staging/);
delivery: [application.yaml](application.yaml).
