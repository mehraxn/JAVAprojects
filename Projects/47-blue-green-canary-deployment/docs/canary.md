# Canary Deployment

Covers the manifests in `../k8s/canary/`. These are example manifests; no real
cluster traffic was shifted in this repository.

## The idea

Expose the new version to a small percentage of traffic first, watch it, then
increase the percentage only while metrics stay healthy.

- **Stable** = proven version v1.
- **Canary** = new version v2.

## Files

| File | Role |
| --- | --- |
| `stable-deployment.yaml` | v1 pods, 9 replicas |
| `canary-deployment.yaml` | v2 pods, 1 replica |
| `service.yaml` | Service selecting both tracks for replica-ratio split |
| `ingress-canary-example.yaml` | NGINX weighted canary example |

## Two traffic split methods

### Replica ratio

One Service selects `app: java-app` and omits `track`, so it load-balances across
stable and canary pods. With 9 stable pods and 1 canary pod, the approximate
split is 90% stable and 10% canary.

### Ingress weight

The NGINX ingress example uses:

```yaml
nginx.ingress.kubernetes.io/canary: "true"
nginx.ingress.kubernetes.io/canary-weight: "10"
```

This gives a more precise percentage, but it requires a compatible ingress
controller and real metrics.

## Promotion pattern

```text
10% -> observe -> 25% -> observe -> 50% -> observe -> 100%
```

If error rate or latency regresses, roll back by scaling the canary to zero or
setting the canary ingress weight to zero.

## Trade-offs

- **Pros:** smaller blast radius and real traffic validation.
- **Cons:** more moving parts and requires trustworthy metrics.
- **Risk:** both versions may run at the same time, so schema changes must be
  backward compatible.
