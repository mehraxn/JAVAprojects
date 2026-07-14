# `rollout` Helm chart

Optional blue-green chart. It renders blue and green Deployments plus one stable
Service whose selector follows `activeTrack`. Canary is intentionally not modeled
in this chart; use the raw manifests in `k8s/canary/` for canary examples.

```bash
# Deploy both tracks, Service points at blue:
helm install demo helm/rollout --set activeTrack=blue

# Promote green:
helm upgrade demo helm/rollout --set activeTrack=green

# Roll back to blue:
helm upgrade demo helm/rollout --set activeTrack=blue
```

The image placeholders are `registry.example.invalid/blue-green-canary-app:v1` and `registry.example.invalid/blue-green-canary-app:v2`. Replace them with images
you build and push to your own registry. This chart was not installed in a real
cluster in this repository.
