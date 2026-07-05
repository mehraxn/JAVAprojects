# Canary Deployment

Covers the manifests in [`../k8s/canary/`](../k8s/canary/). **Nothing was
deployed and no traffic was shifted.**

## The idea

Instead of switching everyone at once (blue-green), expose the new version to a
**small percentage** of traffic first, watch it, and increase the percentage only
while metrics stay healthy. A bad release hurts a few percent of users, not all.

- **Stable** = proven version (v1), most of the traffic.
- **Canary** = new version (v2), a small slice that grows over time.

## The files

| File | Role |
| --- | --- |
| `stable-deployment.yaml` | v1 pods, `track: stable`, 9 replicas |
| `canary-deployment.yaml` | v2 pods, `track: canary`, 1 replica |
| `service.yaml` | Service selecting `app` only (both tracks) — replica-ratio split |
| `ingress-canary-example.yaml` | NGINX weight-based split (exact %, controller needed) |

## Two ways to set the percentage

### 1. Replica ratio (no special controller)

A plain Service that selects `app: java-app` (omitting `track`) load-balances
across **all** matching pods. The split is just the replica ratio:

```
9 stable pods : 1 canary pod  ≈  90% v1 : 10% v2
```

Increase the split by scaling the canary Deployment up (and stable down). Simple,
but granularity is limited by pod counts and it needs spare capacity.

### 2. Ingress weight (precise)

`ingress-canary-example.yaml` uses the NGINX canary annotations to set an **exact**
weight independent of pod counts:

```yaml
nginx.ingress.kubernetes.io/canary: "true"
nginx.ingress.kubernetes.io/canary-weight: "10"   # exactly 10% to v2
```

This needs a compatible ingress controller (or a service mesh / Argo Rollouts for
richer control). Not installed here.

## Canary percentage: the progression

```
 10%  →  observe  →  25%  →  observe  →  50%  →  observe  →  100% (promote)
   │                                                          │
   └──── at any step, if metrics regress → roll back to 0% ───┘
```

"Observe" means comparing the canary's **error rate and latency** against stable
over a bake time (see `../monitoring/rollout-alerts.example.yml`). Promotion
should be gated on those signals, automatically or by a human — never on a timer
alone.

## The flow (NOT executed)

1. Deploy stable (v1) at full scale.
2. Deploy canary (v2) small (1 replica or 10% weight).
3. Send a small slice of live traffic to the canary.
4. Compare canary vs stable metrics over a bake period.
5. Healthy → raise the percentage and repeat; unhealthy → drop canary to 0%
   ([rollback.md](rollback.md)).
6. At 100%, promote: canary becomes the new stable and the old version is retired.

## Trade-offs

- **Pro:** limits blast radius; real production traffic validates v2 gradually.
- **Con:** more moving parts than blue-green; both versions run at once (needs
  version/schema compatibility); requires trustworthy metrics to decide.
