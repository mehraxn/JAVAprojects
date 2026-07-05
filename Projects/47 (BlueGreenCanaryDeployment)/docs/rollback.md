# Rollback

Rollback in this project means redirecting traffic to the last verified version.
No real cluster rollback was performed in this repository.

## Blue-green rollback

Re-apply the blue Service selector:

```bash
kubectl apply -f k8s/blue-green/service.yaml
```

Blue pods are still running, so rollback is a Service selector change.

## Canary rollback

Replica-ratio canary rollback:

```bash
kubectl scale deployment java-app-canary --replicas=0
```

Ingress-weight canary rollback: set `canary-weight` to `0` or delete the canary
Ingress.

## Safe rollback checklist

1. Pause promotion.
2. Redirect traffic.
3. Verify `/version`, `/health`, and `/ready`.
4. Watch error rate and latency.
5. Save evidence: logs, metrics, and image tag/digest.
6. Investigate before retrying.

## Risks

- Non-compatible database migrations can make rollback unsafe.
- Stateful sessions can be affected by traffic switching.
- Old version must have enough capacity to receive traffic again.
- A rollback path should be rehearsed before production use.
