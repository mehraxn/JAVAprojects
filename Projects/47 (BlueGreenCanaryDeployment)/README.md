# Blue-Green and Canary Deployment

Starter structure for comparing blue-green switching with controller-specific canary traffic shifting for a version-reporting Java service.

## Structure

```text
src/bluegreencanarydeployment/
docker/Dockerfile
k8s/blue/deployment.yaml
k8s/green/deployment.yaml
k8s/service-active.yaml
k8s/service-preview.yaml
k8s/canary/service.yaml
k8s/canary/ingress.example.yaml
monitoring/rollout-alerts.example.yml
docs/strategy.md
runbooks/rollback.md
README.md
TESTING.md
```

## Status

Skeleton only. Images, manifests, traffic switching, canary weighting, metrics, rollback, and Kubernetes behavior were not executed.

## Required confirmations

- Ingress or progressive-delivery controller
- Version, health, readiness, and compatibility contract
- Traffic metrics, promotion thresholds, rollback authority, and session behavior
- Database/schema compatibility during overlapping versions
