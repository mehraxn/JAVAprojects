# Blue-Green Deployment

Covers the manifests in [`../k8s/blue-green/`](../k8s/blue-green/). **Nothing was
deployed and no traffic was switched.**

## The idea

Run **two complete production environments** side by side and flip all traffic
between them instantly by repointing one Service selector.

- **Blue** = the version currently serving live traffic (here **v1**).
- **Green** = the new version (here **v2**), deployed at full capacity but idle
  until you switch. It gets validated privately first.

Only one track is "live" at a time; the other is a warm standby.

## The files

| File | Role |
| --- | --- |
| `blue-deployment.yaml` | v1 pods, `track: blue`, 3 replicas (live) |
| `green-deployment.yaml` | v2 pods, `track: green`, 3 replicas (idle standby) |
| `service.yaml` | stable Service `java-app`, selector `track: blue` |
| `switch-service-to-green.yaml` | the SAME Service with selector `track: green` |

## Service switching

The Service selects pods by label. Blue and green pods differ only in their
`track` label, so the Service's `track` selector is the single switch:

```
users → Service java-app (selector track=blue)  → blue/v1 pods   (green idle)
                         ── apply switch ──▶
users → Service java-app (selector track=green) → green/v2 pods   (blue idle)
```

Switching is **atomic** (one selector change, no pod restarts) and near-instant:

```bash
# NOT executed:
kubectl apply -f k8s/blue-green/switch-service-to-green.yaml
```

## The flow (NOT executed)

1. Blue (v1) serves all traffic.
2. Deploy green (v2) at full capacity; wait for its readiness probes to pass.
3. Validate green privately (e.g. a preview Service or port-forward) — check
   `/version` returns v2 and smoke tests pass.
4. **Switch**: apply `switch-service-to-green.yaml`. All traffic moves to v2.
5. Watch metrics. If bad, **roll back instantly** by re-applying `service.yaml`
   (blue is still running). See [rollback.md](rollback.md).
6. Once confident, blue becomes the standby for the next release.

## Trade-offs

- **Pro:** instant switch, instant rollback, no in-between state, easy to reason
  about.
- **Con:** needs ~2× capacity during the overlap; the switch is all-or-nothing
  (every user hits v2 at once — no gradual exposure). For gradual exposure use a
  [canary](canary.md).
- **Watch:** database/schema changes must be compatible with **both** versions
  during the overlap (see [rollback.md](rollback.md#risks)).
