# Helm Chart Java App

## Description

An educational Helm chart that parameterizes the Kubernetes deployment model of a small Java HTTP application. Safe defaults, reusable helpers, and optional resources demonstrate how charts reduce copied YAML while retaining reviewable output.

## Goal

The goal is to understand how `Chart.yaml`, `values.yaml`, helpers, and templates combine into rendered Kubernetes objects, and why rendered output must be inspected before any installation or upgrade.

## Technologies and concepts used

- Helm chart API version 2
- Go-template expressions and functions
- Reusable naming/label helpers
- Parameterized Deployment, Service, and ConfigMap
- Optional Secret and Ingress rendering
- ConfigMap checksum-driven rollout concept
- Kubernetes probes, resources, and security contexts
- Release, upgrade, rollback, and uninstall concepts

## Project structure

```text
helm/java-app/
  Chart.yaml
  values.yaml
  .helmignore
  templates/
    _helpers.tpl
    deployment.yaml
    service.yaml
    configmap.yaml
    secret.example.yaml
    ingress.yaml
docs/VALUES.md
README.md
TESTING.md
```

## Important files explained

- `Chart.yaml` defines chart identity, type, chart version, and app version.
- `values.yaml` holds safe defaults for image, replicas, ports, configuration, probes, resources, security, Secret behavior, and Ingress behavior.
- `_helpers.tpl` centralizes generated names, selector labels, standard labels, and Secret-name selection.
- `deployment.yaml` consumes values and adds a ConfigMap checksum annotation for rollout changes.
- `service.yaml` uses the same selector helper as the Deployment.
- `configmap.yaml` renders non-sensitive application configuration.
- `secret.example.yaml` renders only when explicitly enabled and requires a non-empty learning value.
- `ingress.yaml` is disabled by default.

## Intended real-environment workflow

In an approved environment, a developer would lint the chart, render defaults, render each intended override combination, inspect all generated YAML, validate it against Kubernetes schemas, confirm the active cluster context/namespace, and only then install into a disposable namespace. Upgrades should be diffed and rollback/uninstall behavior understood before use.

Real secrets must not be placed in `values.yaml`, `--set` arguments, shell history, or committed override files. The preferred learning path is an approved separately managed Secret referenced by name.

## Prepared but not executed

- Chart metadata, values, helpers, five resource templates, provisioning defaults, and documentation were prepared.
- Helm, kubectl, and Kubernetes were not installed or executed.
- The chart was not linted, rendered, packaged, installed, upgraded, rolled back, or uninstalled.
- No cluster object or Helm release data was created, and no successful deployment is claimed.

## Manual validation checklist

- [ ] Confirm all template value paths exist in `values.yaml`.
- [ ] Confirm Deployment and Service share selector labels.
- [ ] Confirm rendered names remain valid Kubernetes names.
- [ ] Render default, Ingress-enabled, chart-Secret, and external-Secret combinations.
- [ ] Confirm empty chart-created secret values fail rendering.
- [ ] Confirm ConfigMap changes alter the checksum annotation.
- [ ] Review complete rendered YAML before any cluster operation.

## Common mistakes avoided

- Secrets and Ingress are disabled by default.
- Placeholder image and host values are explicit.
- Repeated names/labels are centralized in helpers.
- App and container ports derive from one value.
- Chart-created and external Secret choices are separated.
- A chart passing source review is not described as rendered, valid, or deployed.

## Possible future improvements

- Add `values.schema.json` for stronger input validation.
- Add automated chart lint/render tests after Helm is approved.
- Add configurable service accounts and NetworkPolicy.
- Add immutable image-digest support.
- Add release-diff and rollback exercises in a disposable cluster.
- Document an approved external secret workflow.
