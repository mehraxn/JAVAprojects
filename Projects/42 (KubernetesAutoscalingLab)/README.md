# Kubernetes Autoscaling Lab

*A hands-on lab for Kubernetes horizontal autoscaling of a Java service — CPU requests/limits, the Horizontal Pod Autoscaler, metrics-server, and load-driven scale-up/down.*

## Problem this project solves

A fixed replica count either wastes money at idle or falls over under a spike.
This lab shows how Kubernetes **scales automatically**: the Horizontal Pod
Autoscaler (HPA) watches CPU utilization and adds/removes pods within bounds.
It makes the moving parts concrete — the CPU **request** as the utilization
denominator, the **metrics-server** dependency, and how a **load test** actually
triggers scaling.

## Technologies & concepts

- **Java 21** (built-in `HttpServer`) with a deliberate CPU-burning `/work` endpoint
- **Kubernetes** — Deployment, Service, ConfigMap, HPA (`autoscaling/v2`)
- **CPU requests vs limits**; **HPA** min/max replicas + `targetCPUUtilization`
- **metrics-server** (the HPA's data source)
- **Load testing** concepts (k6 / hey) as the scaling trigger

## Architecture overview

```
 load generator ──HTTP /work──▶ Service ──▶ Pods (CPU rises)
                                             │
                                    metrics-server samples CPU
                                             │
                                   HPA compares to target (60%)
                                             │
                        scales Deployment  1 ◀──────▶ up to 5 pods
```

`desiredReplicas = ceil(currentReplicas × currentUtilization / targetUtilization)`

## Project structure

```text
src/kubernetesautoscalinglab/Main.java   app: /health /ready /work /metrics
docker/Dockerfile                         multi-stage non-root image (NOT built)
k8s/deployment.yaml                       CPU requests/limits, probes, security
k8s/service.yaml                          ClusterIP
k8s/hpa.yaml                              HPA: min 1 / max 5 / CPU 60% + behavior
k8s/configmap.yaml                        PORT, MAX_WORK_MS
k8s/resource-limits-example.yaml          LimitRange + ResourceQuota + annotated Pod
load-test/README.md                       k6 / hey concepts (NOT executed)
docs/autoscaling-explanation.md  docs/metrics-server.md
README.md  TESTING.md
```

## Important files explained

- **k8s/deployment.yaml** — CPU `request: 250m` (the utilization denominator) and `limit: 500m`; probes + non-root security context.
- **k8s/hpa.yaml** — `minReplicas: 1`, `maxReplicas: 5`, CPU `averageUtilization: 60`, plus a `behavior` block (fast scale-up, 300s scale-down window).
- **k8s/resource-limits-example.yaml** — namespace `LimitRange` + `ResourceQuota` and an annotated Pod explaining request-vs-limit anatomy.
- **src/…/Main.java** — `/work?ms=` burns CPU (capped by `MAX_WORK_MS`) so a load test can raise utilization; `/metrics` is illustrative only.
- **docs/metrics-server.md** — why the HPA shows `<unknown>` and won't scale without metrics-server.

## How it would work in a real environment

Install metrics-server, apply the manifests, then hammer `/work` with k6 or hey.
CPU climbs past 60% of the request; metrics-server reports it; the HPA adds pods
up to 5. When load stops, CPU falls and — after the 300s stabilization window —
the HPA scales back toward 1.

## What was prepared but NOT executed

Prepared: the app, Dockerfile, all manifests, and the load-test/metrics-server
docs. **Not executed:** no Docker build, no `kubectl`, no cluster, no
metrics-server install, and no load test. **Autoscaling was not observed** — only
the config that would produce it exists.

## Security notes

- **No secrets or credentials** — the app needs none; all images are placeholders.
- Deployment runs **non-root**, read-only rootfs, dropped capabilities, `RuntimeDefault` seccomp.
- `MAX_WORK_MS` caps a single `/work` request so a load test can't pin a pod indefinitely.
- `/metrics` exposes only illustrative counters, not the HPA's data path.

## Limitations

- Autoscaling, scale-up, stabilization, and scale-down were **not measured**.
- The image was not built; metrics-server was not installed (HPA data source absent).
- `250m/500m` and `60%` are reasonable defaults, **not** tuned against real traffic.

## Future improvements

- Add memory-based and custom/external metrics (KEDA) alongside CPU.
- Tune target and stabilization windows from a measured baseline.
- Add a `PodDisruptionBudget` and a real readiness gate for graceful scale-down.
- Wire `/metrics` into Prometheus and alert on saturation.

## What I learned

- The HPA scales on CPU as a **percentage of the request** — the request is the key number.
- Why **metrics-server is a hard prerequisite** (no metrics → no scaling).
- How a load test translates into scaling, and why **stabilization windows** prevent flapping.
- Setting `minReplicas`/`maxReplicas` as availability and cost guardrails.
