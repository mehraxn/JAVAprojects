# helm/

A Helm chart that is **functionally equivalent** to the Kustomize base+overlays
in [../k8s/](../k8s/). It is included to show the same multi-environment app as a
packaged chart; pick **one** mechanism for real use — this repo's GitOps wires up
the Kustomize overlays. **Nothing here was rendered or installed.**

Per-environment differences live in `app/values-<env>.yaml` (replicas, resources,
config) — the Helm equivalent of the overlays.

```bash
# NOT executed — render each environment:
helm template app helm/app -f helm/app/values-dev.yaml
helm template app helm/app -f helm/app/values-staging.yaml
helm template app helm/app -f helm/app/values-prod.yaml
```

Secrets: `templates/secret.example.yaml` is disabled unless `--set
secret.create=true`, and even then only renders placeholders. Real secrets come
from a sealed-secret / External Secrets Operator, never from values.
