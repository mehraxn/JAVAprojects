# Blue-Green Deployment

Covers the manifests in `../k8s/blue-green/` and the explicit promotion file in
`../k8s/blue-green-switch/`. These are example manifests; no real cluster traffic
was switched in this repository.

## The idea

Run two complete tracks side by side and flip all traffic by changing one Service
selector.

- **Blue** = current live version, v1.
- **Green** = new version, v2, deployed at full capacity before promotion.

## Files

| File | Role |
| --- | --- |
| `k8s/blue-green/blue-deployment.yaml` | v1 pods, `track: blue` |
| `k8s/blue-green/green-deployment.yaml` | v2 pods, `track: green` |
| `k8s/blue-green/service.yaml` | stable Service selecting `track: blue` |
| `k8s/blue-green-switch/switch-service-to-green.yaml` | same Service selecting `track: green` |

The switch file is intentionally outside the initial apply folder, so applying
`k8s/blue-green/` cannot promote green by accident.

## Flow

```bash
kubectl apply -f k8s/blue-green/blue-deployment.yaml
kubectl apply -f k8s/blue-green/green-deployment.yaml
kubectl apply -f k8s/blue-green/service.yaml

# Promote green
kubectl apply -f k8s/blue-green-switch/switch-service-to-green.yaml

# Roll back to blue
kubectl apply -f k8s/blue-green/service.yaml
```

## Trade-offs

- **Pros:** instant cutover, instant rollback, easy to reason about.
- **Cons:** needs roughly double capacity and exposes everyone to v2 at once.
- **Risk:** database changes must stay compatible with both versions during the
  overlap.
