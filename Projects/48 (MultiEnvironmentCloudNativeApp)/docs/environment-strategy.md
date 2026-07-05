# Environment Strategy

Why three environments, and how they differ. **Nothing was deployed.**

## Build once, configure per environment

The single most important rule: **one immutable image** (`...:1.4.0`, pinned by
digest) is built once and promoted **unchanged** through dev → staging → prod.
Only *configuration* differs between environments. Rebuilding per environment
would mean prod runs a different artifact than the one you tested — the classic
"works in staging, breaks in prod" trap.

- **Code / image**: identical everywhere.
- **Config** (ConfigMap): per environment (`APP_ENVIRONMENT`, `LOG_LEVEL`,
  feature flags).
- **Secrets** (Secret): per environment, from a secrets manager — never in Git.
- **Scale** (replicas + resources): per environment.

## How the three environments differ

| | dev | staging | prod |
| --- | --- | --- | --- |
| Namespace | `app-dev` | `app-staging` | `app-prod` |
| Replicas | 1 | 2 | 4 |
| CPU req→lim | 50m→250m | 100m→500m | 250m→1000m |
| Mem req→lim | 64Mi→128Mi | 128Mi→256Mi | 256Mi→512Mi |
| Log level | DEBUG | INFO | WARN |
| `FEATURE_NEW_UI` | true | true | false |
| Sync | auto | auto | **manual** |
| Purpose | fast iteration | prod-like rehearsal | real users |

These live as Kustomize overlays in [../k8s/overlays/](../k8s/overlays/) (and,
equivalently, as Helm `values-<env>.yaml`). dev is cheap and chatty; staging
mirrors prod closely to catch problems; prod is the largest, quietest, and most
guarded, with risky features left **off** until proven.

## Where each concern lives

| Concern | Location |
| --- | --- |
| Shared manifests | `k8s/base/` |
| Per-env config/scale | `k8s/overlays/<env>/` (or `helm/app/values-<env>.yaml`) |
| Per-env delivery + secrets | `environments/<env>/` |
| Orchestration | `gitops/` |
| Build + promote | `ci/` |

See [promotion-model.md](promotion-model.md), [rollback.md](rollback.md),
[secrets.md](secrets.md), and [gitops.md](gitops.md).
