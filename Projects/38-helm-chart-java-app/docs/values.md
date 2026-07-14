# Values Guide

`values.yaml` is the chart's default configuration contract, and
`values.schema.json` enforces types and enums — invalid values (for example
`replicaCount: "two"`) fail `helm lint`/`helm template` before anything
renders. Defaults are safe, understandable, and non-sensitive.

## Naming

`nameOverride` changes the chart-name portion of generated names.
`fullnameOverride` replaces the generated release/chart name entirely.
Leaving both empty produces a name such as `my-release-java-app`.

## Workload

- `replicaCount` — desired Pods (schema: integer ≥ 1).
- `image.repository` / `image.tag` / `image.pullPolicy` — the container
  image. Defaults to the versioned local tag `helm-java-app:0.1.0`, never
  `latest`; production should use a real registry reference and promote by
  immutable image digest.
- `containerPort` — the Java application's HTTP port (schema: 1–65535).
- `resources` — CPU/memory requests and limits (learning defaults, not
  load-tested).
- `podSecurityContext` / `securityContext` — non-root and
  container-hardening settings; the Deployment mounts a size-limited
  emptyDir at `/tmp` because the root filesystem is read-only.

## Service account

```yaml
serviceAccount:
  create: true
  name: ""
  automount: false
  annotations: {}
```

Name resolution (see `java-app.serviceAccountName` in `_helpers.tpl`):

| create | name | ServiceAccount used |
| --- | --- | --- |
| true | empty | the chart fullname (created by the chart) |
| true | set | that name (created by the chart) |
| false | set | that existing name (not created) |
| false | empty | the namespace's `default` |

`automount` defaults to `false` because the app never talks to the
Kubernetes API — no token is mounted into the pod.

## Application configuration

The `config` section becomes ConfigMap data: `containerPort` → `APP_PORT`,
`environment` → `APP_ENVIRONMENT`, `message` → `APP_MESSAGE`.

## Rollout checksums

The Deployment pod template carries checksum annotations so `helm upgrade`
rolls pods when configuration content changes:

- `checksum/config` — always present; hashes the rendered ConfigMap.
- `checksum/secret` — present only when the chart itself creates the Secret
  (`secret.create=true` without `existingSecret`).

An externally managed `existingSecret` **cannot** be hashed by Helm — the
chart only sees its name, not its content — so rotating an external secret
does not roll pods automatically; use a tool like Reloader or
`kubectl rollout restart` after rotation.

## Service and probes

`service.type` defaults to `ClusterIP` (schema enum: ClusterIP, NodePort,
LoadBalancer) and `service.port` to 80. Readiness/liveness paths and timings
live under `probes`; defaults assume the image serves `/ready` and `/health`.

## Secret choices

By default `secret.create=false` and `secret.existingSecret` is empty, so no
Secret is rendered or referenced.

- Set only `existingSecret` to reference a separately managed Secret
  containing `APP_DEMO_TOKEN` (see
  [../examples/values-external-secret.yaml](../examples/values-external-secret.yaml)).
- Set `create=true` to render the chart's demo Secret; it requires
  `demoToken` and refuses to render with an empty value.
- If both are set, the existing Secret wins and the chart Secret is not
  rendered.

Chart-created secrets are for learning only. Real values must never be
committed, passed via `--set`, or left in shell history — and remember Helm
release data stores supplied values.

## Ingress

```yaml
ingress:
  enabled: false
  className: ""
  annotations: {}
  hosts:
    - host: java-app.local
      paths:
        - path: /
          pathType: Prefix
  tls: []
```

Disabled by default. Supports multiple hosts/paths, class name, annotations,
and TLS blocks — see
[../examples/values-ingress.yaml](../examples/values-ingress.yaml). Schema
validates `pathType` (Prefix/Exact/ImplementationSpecific). Rendering an
Ingress still requires an installed controller to do anything.

## Example values files

| File | Shows |
| --- | --- |
| `examples/values-dev.yaml` | 1 replica, dev config, lighter resources |
| `examples/values-prod.yaml` | 3 replicas, prod config, stronger resources |
| `examples/values-ingress.yaml` | enabled Ingress with class/annotations/host |
| `examples/values-external-secret.yaml` | referencing a pre-existing Secret |

## Verification status

`helm lint`, default and per-example `helm template` renders, the negative
schema test, `helm package`, and a `kubectl apply --dry-run=client` of the
rendered output were all actually run — see
[../TEST_RESULTS.md](../TEST_RESULTS.md). No real cluster install was
performed.
