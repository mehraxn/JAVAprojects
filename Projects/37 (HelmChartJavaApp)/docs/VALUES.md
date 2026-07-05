# Values Guide

`values.yaml` is the chart's default configuration contract. Users can supply a separate values file or individual overrides when rendering or installing. Defaults should remain safe, understandable, and non-sensitive.

## Naming

`nameOverride` changes the chart-name portion of generated names. `fullnameOverride` replaces the generated release/chart name entirely. Leaving both empty produces a name such as `learning-release-java-app`.

## Workload

- `replicaCount` controls desired Pods.
- `image.repository`, `image.tag`, and `image.pullPolicy` describe the container image.
- `containerPort` is the Java application's HTTP port.
- `resources` supplies CPU and memory requests and limits.
- `podSecurityContext` and `securityContext` define non-root and container-hardening settings.

The Deployment also mounts a size-limited ephemeral volume at `/tmp` so the JVM has temporary space while the container root filesystem remains read-only.

The defaults are learning examples and are not based on load testing.

## Application configuration

The `config` section becomes ConfigMap data:

- `containerPort` becomes `APP_PORT`, keeping the Pod port and application port aligned.
- `environment` becomes `APP_ENVIRONMENT`.
- `message` becomes `APP_MESSAGE`.

A checksum annotation connects ConfigMap rendering to the Deployment Pod template so a Helm upgrade can roll Pods when configuration changes.

## Service and probes

`service.type` defaults to `ClusterIP`, and `service.port` defaults to 80. Readiness and liveness paths and timings are under `probes`. The defaults assume the image serves `/ready` and `/health` on the configured container port.

## Secret choices

By default, `secret.create=false` and `secret.existingSecret` is empty, so the Deployment has no Secret reference.

- Set only `existingSecret` to reference a separately managed Secret containing `APP_DEMO_TOKEN`.
- Setting `create=true` demonstrates chart-created Secret rendering and requires `demoToken`.
- If both are set, the existing Secret takes precedence and the example Secret is not rendered.

Chart-created secrets are included for learning only. Real values must not be committed or passed through shell history.

## Ingress

`ingress.enabled=false` omits the resource. When enabled, class, host, path, and path type come from the `ingress` section. The default host ends in `.example.invalid` and cannot represent a real deployment.

## Verification status

No value combination was rendered or validated with Helm. This guide describes the intended template behavior based on static inspection.
