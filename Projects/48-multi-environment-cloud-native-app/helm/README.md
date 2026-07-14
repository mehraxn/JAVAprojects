# helm/

A Helm chart that is **functionally equivalent** to the Kustomize base+overlays
in [../k8s/](../k8s/). It is included to show the same multi-environment app as a
packaged chart; pick **one** mechanism for real use — this repo's GitOps wires up
the Kustomize overlays. **Nothing here was rendered or installed.**

Per-environment differences live in `app/values-<env>.yaml` (replicas, resources,
config) — the Helm equivalent of the overlays.

```bash
# Render each environment locally:
helm template app helm/app -f helm/app/values-dev.yaml
helm template app helm/app -f helm/app/values-staging.yaml
helm template app helm/app -f helm/app/values-prod.yaml
```

The chart uses `registry.example.invalid/cloud-native-app` and renders the
environment-specific digest placeholder from each values file. Replace it with a
real CI-produced digest before deployment.

Secret consumption is disabled by default. Set both `secret.enabled=true` and
`secret.create=true` only to render the placeholder example Secret. In a real
environment, enable consumption but provision `app-secret` with Sealed Secrets
or External Secrets rather than putting plaintext in values.
