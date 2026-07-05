# Multi-Environment Cloud-Native App

Starter structure for a configuration-aware Java application promoted through dev, staging, and production-design overlays without duplicating the Kubernetes base.

## Structure

```text
src/multienvironmentcloudnativeapp/
k8s/base/
k8s/overlays/dev/
k8s/overlays/staging/
k8s/overlays/prod/
terraform/main.tf
ci/promotion.example.yml
docs/environment-strategy.md
docs/promotion-model.md
README.md
TESTING.md
```

## Status

Skeleton only. The Java app, image, overlays, promotion flow, infrastructure design, and environment behavior were not compiled, rendered, planned, deployed, or tested.

## Required confirmations

- Environment ownership and promotion approvals
- Image immutability and configuration/secrets boundaries
- Cluster/account topology and production access policy
- Database migration, observability, rollback, and disaster-recovery expectations
