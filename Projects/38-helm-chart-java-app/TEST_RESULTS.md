# Test Results — Helm Chart Java App

Validation performed on **2026-07-10** on Windows 11 with Helm and kubectl
installed. Everything below was actually run. No real cluster install was
performed.

## helm lint result — PASS

```
helm lint helm/java-app
==> Linting helm/java-app
[INFO] Chart.yaml: icon is recommended
1 chart(s) linted, 0 chart(s) failed
```

## Default helm template result — PASS

Rendered ServiceAccount, ConfigMap, Service, and Deployment (no Secret, no
Ingress). Verified in the output: `image: "helm-java-app:0.1.0"`,
`checksum/config` annotation, `serviceAccountName: java-app-java-app`,
`automountServiceAccountToken: false`.

## Dev values template result — PASS

`-f examples/values-dev.yaml` rendered with 1 replica and dev config.

## Prod values template result — PASS

`-f examples/values-prod.yaml` rendered with 3 replicas and prod config.

## Ingress values template result — PASS

`-f examples/values-ingress.yaml` rendered an Ingress with
`ingressClassName: nginx`, the `proxy-body-size` annotation, host
`java-app.local`, and `pathType: Prefix`.

## External secret values template result — PASS

`-f examples/values-external-secret.yaml`: **no Secret rendered**; the
container references `java-app-existing-secret` via `secretKeyRef`; and —
by design — **no `checksum/secret` annotation** (Helm cannot hash a Secret
it does not render; documented in docs/values.md).

The chart-created variant (`--set secret.create=true --set
secret.demoToken=demo-only`) rendered the Secret **and** the
`checksum/secret` annotation.

## Invalid values schema test result — PASS (correctly rejected)

With `replicaCount: "two"`, `containerPort: "abc"`, `service.type:
WrongType`, `helm template` exited 1 with:

```
- at '/containerPort': got string, want integer
- at '/service/type': value must be one of 'ClusterIP', 'NodePort', 'LoadBalancer'
- at '/replicaCount': got string, want integer
```

## helm package result — PASS

`helm package helm/java-app --destination $TMP` produced
`java-app-0.1.0.tgz` (deleted afterward; `*.tgz` is gitignored).

## kubectl dry-run result — PASS

`helm template java-app helm/java-app | kubectl apply --dry-run=client -f -`
against a local kind context validated all four resources
(`created (dry run)` each); nothing was created on the cluster.

## Real cluster install/upgrade/rollback result — NOT RUN

No `helm install`, `upgrade`, `rollback`, or `uninstall` was executed. The
optional workflow is documented in TESTING.md section G.

## Tools unavailable

None relevant — Helm and kubectl were both available.

## Known limitations

- The default image `helm-java-app:0.1.0` is a versioned local placeholder;
  no image was built or pushed, so an actual install would need an image with
  that tag loaded into the cluster.
- Rendering and dry-run validation prove the chart's output is well-formed —
  not that the workload runs; runtime behavior was not exercised.
- Resource sizes, probe timings, and the demo Secret flow are learning
  defaults, not production guidance.
- Results are a point-in-time snapshot of one validation run.
