# `rollout` Helm chart

Optional convenience chart that renders the blue + green Deployments and a single
Service whose selector follows `activeTrack`. It turns the blue-green switch into
a one-line values change. **Not rendered or installed here.**

```bash
# NOT executed — deploy both tracks, Service points at blue:
helm install demo helm/rollout --set activeTrack=blue

# NOT executed — the switch (promote green):
helm upgrade demo helm/rollout --set activeTrack=green

# NOT executed — instant rollback (green pods still running):
helm upgrade demo helm/rollout --set activeTrack=blue
```

The raw manifests under [../../k8s/](../../k8s/) show the same ideas without Helm.
All images (`my-java-app:v1`, `:v2`) are placeholders.
