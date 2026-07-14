# prod environment

| Property | Value |
| --- | --- |
| Namespace | `app-prod` |
| Replicas | 4 |
| Resources | 250m/256Mi → 1000m/512Mi |
| Config | `LOG_LEVEL=WARN`, `FEATURE_NEW_UI=false` |
| Sync | **manual** (human-gated) |
| Approval | required; promoted only after staging is verified |

The final stop. Same immutable image as dev/staging. New features stay **off**
until proven in staging. Sync is manual so a person deliberately releases. Config:
[../../k8s/overlays/prod/](../../k8s/overlays/prod/); delivery:
[application.yaml](application.yaml). Rollback: [../../docs/rollback.md](../../docs/rollback.md).
