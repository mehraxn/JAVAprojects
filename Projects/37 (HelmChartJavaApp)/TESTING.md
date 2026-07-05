# Testing Helm Chart Java App

No Helm, kubectl, Kubernetes, cluster, template-rendering, release, or application command was executed while preparing this project.

## Static validation checklist

- [ ] Review Go-template delimiters, indentation, and whitespace control.
- [ ] Confirm helper definitions and includes use matching names.
- [ ] Confirm all `.Values` references exist.
- [ ] Confirm Deployment and Service selectors share one helper.
- [ ] Confirm optional template conditions are mutually understandable.
- [ ] Review checksum, probes, resources, volumes, and security fields.

## File existence checks

- [ ] `Chart.yaml`, `values.yaml`, and `.helmignore` exist.
- [ ] Deployment, Service, ConfigMap, Secret example, and Ingress templates exist.
- [ ] `_helpers.tpl` exists.
- [ ] `docs/VALUES.md`, `README.md`, and `TESTING.md` exist.

## Configuration review checklist

- [ ] Chart and application versions are explicit.
- [ ] Default image is the documented placeholder.
- [ ] `containerPort` drives both the Pod port and `APP_PORT`.
- [ ] Secret/Ingress defaults render no optional object.
- [ ] Existing-Secret precedence is documented.
- [ ] Ingress backend references the rendered Service and named port.

## Security checks

- [ ] No real secret, credential, certificate, or repository token is present.
- [ ] No production endpoint, cluster, namespace, registry, or hostname is present.
- [ ] Non-root, capability, privilege, and read-only-root settings are retained.
- [ ] Documentation warns that Helm release data can expose supplied secrets.

## Commands normally used - NOT executed

```text
helm lint helm/java-app
helm template learning-release helm/java-app
helm template learning-release helm/java-app --set ingress.enabled=true
helm install learning-release helm/java-app --namespace learning --create-namespace
helm upgrade learning-release helm/java-app --namespace learning
helm rollback learning-release 1 --namespace learning
helm uninstall learning-release --namespace learning
```

All commands are examples only and require installed tooling plus an approved disposable cluster for release operations.

## Expected results in a proper environment

- Helm lint accepts the chart.
- Default rendering produces Deployment, Service, and ConfigMap only.
- Ingress renders only when enabled.
- Chart-created Secret rendering fails when its required value is empty.
- External-Secret selection creates a reference without rendering a Secret.
- A ConfigMap value change changes the Deployment checksum and triggers a rollout on upgrade.
