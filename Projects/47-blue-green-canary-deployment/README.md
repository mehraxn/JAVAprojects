# Blue-Green & Canary Deployment

## Overview

This educational progressive-delivery project provides two versioned Java services plus Kubernetes and Helm configurations for blue-green and canary rollout exercises. It documents traffic switching, observation, rollback, and the boundary between locally executable validation and example-only cluster operations.

A progressive-delivery portfolio project for a small Java HTTP service. It shows
two safer release strategies: **blue-green** for instant cutover/rollback and
**canary** for gradual exposure with metrics-based decisions.

## What this project demonstrates

- Two Java versions: `app-v1` and `app-v2`
- Version, health, and readiness endpoints
- Dockerfiles for both versions
- Kubernetes blue-green manifests
- Kubernetes canary manifests using replica ratio
- NGINX ingress-weight canary example
- Optional Helm chart for blue-green switching
- Rollback runbooks and monitoring examples

## Endpoints

Each app exposes:

```text
GET /version  -> {"version":"v1"} or {"version":"v2"}
GET /health   -> ok
GET /ready    -> ready
GET /         -> greeting with the active version
```

## Project structure

```text
app-v1/                         Java v1 app + Dockerfile
app-v2/                         Java v2 app + Dockerfile
k8s/blue-green/                 initial blue-green state: blue active, green idle
k8s/blue-green-switch/          explicit promotion switch to green
k8s/canary/                     replica-ratio and ingress-weight canary examples
helm/rollout/                   optional blue-green Helm chart
monitoring/                     example rollout alerts
docs/                           strategy explanations
runbooks/                       rollback runbook
TESTING.md                      commands to validate locally
TEST_RESULTS.md                 validation results/placeholders
```

## Image placeholders

The manifests use placeholder images:

```text
registry.example.invalid/blue-green-canary-app:v1
registry.example.invalid/blue-green-canary-app:v2
```

Replace them with images you build and push to your own registry. No real
registry credentials or production endpoints are included.

## Blue-green flow

Initial state:

```text
users -> Service java-app selector track=blue -> v1 pods
green/v2 pods are deployed but idle
```

Apply the initial state without accidentally switching traffic:

```bash
kubectl apply -f k8s/blue-green/blue-deployment.yaml
kubectl apply -f k8s/blue-green/green-deployment.yaml
kubectl apply -f k8s/blue-green/service.yaml
```

Promote green separately:

```bash
kubectl apply -f k8s/blue-green-switch/switch-service-to-green.yaml
```

Roll back to blue:

```bash
kubectl apply -f k8s/blue-green/service.yaml
```

The switch file is intentionally outside `k8s/blue-green/` so it is not applied
by accident during the initial setup.

## Canary flow

The replica-ratio canary uses one Service that selects both stable and canary
pods. With 9 stable replicas and 1 canary replica, the approximate split is 90/10.

```bash
kubectl apply -f k8s/canary/stable-deployment.yaml
kubectl apply -f k8s/canary/canary-deployment.yaml
kubectl apply -f k8s/canary/service.yaml
```

The ingress-weight example in `k8s/canary/ingress-canary-example.yaml` shows a
more precise NGINX-based 10% canary. It requires a compatible ingress controller.

## Helm chart

`helm/rollout/` is intentionally blue-green only. It renders both tracks and a
Service controlled by `activeTrack`:

```bash
helm install demo helm/rollout --set activeTrack=blue
helm upgrade demo helm/rollout --set activeTrack=green
helm upgrade demo helm/rollout --set activeTrack=blue
```

## What is executable locally

The Java applications can be compiled and run locally with only a JDK. See
`TESTING.md` for exact commands.

## What is example-only

The Kubernetes, Helm, ingress, and Prometheus files are example manifests. They
were prepared and syntax-reviewed, but they were not deployed to a real cluster
in this repository. Do not claim real traffic was switched unless you test it in
your own cluster and record the result.

## Security notes

- No real secrets or credentials are stored.
- Kubernetes containers run as non-root.
- Container capabilities are dropped.
- Root filesystems are read-only.
- A writable `/tmp` is provided through `emptyDir` for Java runtime safety.

## Main limitations

- No real Kubernetes cluster deployment is included.
- No real production traffic was shifted.
- Canary metrics depend on a real ingress/controller and Prometheus setup.
- Database/schema migration compatibility is documented, not implemented.

## Resume Value

Built a Java progressive-delivery lab showing blue-green and canary deployment
patterns with Kubernetes manifests, secure pod settings, health/readiness probes,
NGINX ingress-weight examples, an optional Helm blue-green chart, Prometheus alert
examples, and rollback runbooks.
