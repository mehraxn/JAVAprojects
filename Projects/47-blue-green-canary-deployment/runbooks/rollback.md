# Rollback Runbook

Purpose: stop a bad rollout quickly, restore traffic to the last verified
version, and preserve enough evidence to debug the failure.

## Blue-green rollback

The blue version remains running while green is tested. To return traffic to v1:

```bash
kubectl apply -f k8s/blue-green/service.yaml
```

This re-selects `track: blue` on the stable Service. No pods are recreated.

## Canary rollback

For replica-ratio canary, scale the canary to zero:

```bash
kubectl scale deployment java-app-canary --replicas=0
```

For NGINX ingress-weight canary, set the canary annotation to `0` or remove the
canary ingress.

## Operator checklist

1. Pause any promotion job or manual rollout.
2. Redirect traffic to the last known-good version.
3. Confirm `/version`, `/health`, and `/ready` return the expected result.
4. Check error rate and latency returned to baseline.
5. Save logs, metrics screenshots, and the failed image tag/digest.
6. Communicate user impact and the rollback action.
7. Investigate before retrying the release.

## Important safety notes

Rollback is safest when database changes are backward compatible, the service is
stateless, and the old version has enough capacity to receive traffic again.
