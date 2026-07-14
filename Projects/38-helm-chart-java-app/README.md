# Helm Chart Java App

*A reusable, schema-validated Helm chart for deploying a Java HTTP app — safe
defaults, parameterized Deployment/Service/ConfigMap/Secret/Ingress, service
account support, rollout checksums, example values files, and honest
lint/template validation results.*

## What this project is

An educational-but-complete Helm chart showing how `Chart.yaml`,
`values.yaml`, `values.schema.json`, helpers, and templates combine into
rendered Kubernetes objects — and how to validate a chart before any cluster
sees it.

## What it demonstrates

- **Chart structure**: `Chart.yaml`, `values.yaml`, `.helmignore`,
  `_helpers.tpl`, `NOTES.txt`
- **Schema validation** (`values.schema.json`): wrong types/enums fail
  `helm lint`/`template` — e.g. `replicaCount: "two"` is rejected
- **Parameterized resources**: Deployment, Service, ConfigMap, optional
  Secret, optional multi-host Ingress (class/annotations/TLS), ServiceAccount
- **Rollout checksums**: `checksum/config` always, `checksum/secret` when the
  chart creates the Secret (external secrets can't be hashed — documented)
- **Security defaults**: non-root, read-only rootfs, dropped capabilities,
  seccomp, no API token mounted (`serviceAccount.automount: false`)
- **Probes + resources**, and an install/upgrade/rollback workflow
- **Example values files** for dev, prod, ingress, and external-secret use

## Project structure

```text
helm/java-app/
  Chart.yaml  values.yaml  values.schema.json  .helmignore
  templates/
    _helpers.tpl  NOTES.txt
    deployment.yaml  service.yaml  configmap.yaml
    secret.yaml  ingress.yaml  serviceaccount.yaml
examples/
  values-dev.yaml  values-prod.yaml
  values-ingress.yaml  values-external-secret.yaml
docs/values.md      full values reference
README.md  TESTING.md  TEST_RESULTS.md
```

## Quick validation

```bash
helm lint helm/java-app
helm template java-app helm/java-app
helm template java-app helm/java-app -f examples/values-dev.yaml
helm template java-app helm/java-app -f examples/values-prod.yaml
helm template java-app helm/java-app -f examples/values-ingress.yaml
helm template java-app helm/java-app -f examples/values-external-secret.yaml
```

All of these (plus the negative schema test, `helm package`, and a kubectl
client dry-run) were actually run and passed on 2026-07-10 — see
[TEST_RESULTS.md](TEST_RESULTS.md). Full command list with expected results:
[TESTING.md](TESTING.md). Values reference: [docs/values.md](docs/values.md).

## Install / upgrade / rollback (optional, real cluster)

```bash
helm install java-app helm/java-app
helm upgrade java-app helm/java-app -f examples/values-prod.yaml
helm rollback java-app 1
helm uninstall java-app
```

Only for a disposable local cluster; no cluster install was performed for
this repo's recorded results.

## What is implemented vs example-only

**Implemented and validated:** the chart templates, schema validation, helper
logic, example values, and safe defaults — all rendered and checked with real
Helm/kubectl commands.

**Example-only / not production-grade:**

- The default image `helm-java-app:0.1.0` is a versioned local placeholder —
  **no real app image is built or pushed** by this project. Production should
  use a real registry reference and promote by immutable image digest.
- No real cluster install unless you run the optional workflow yourself.
- The chart Secret is a learning demo; production secrets belong in a secret
  manager (or at minimum a separately managed Secret referenced by
  `secret.existingSecret`).
- Resource sizes and probe timings are learning defaults, not tuned values.

## Resume Value

Created a configurable Helm chart with schema validation, secure workload defaults, checksummed configuration, environment values, render/lint tests, and optional cluster workflows.

## Possible future improvements

- Automated chart tests (`helm unittest` / chart-testing) in CI
- NetworkPolicy and PodDisruptionBudget options
- Autoscaling (HPA) support
- Immutable image-digest values plumbing
