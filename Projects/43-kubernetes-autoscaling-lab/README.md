# Kubernetes Autoscaling Lab

*A local Kubernetes autoscaling lab for a Java CPU-load service — CPU
requests/limits, the Horizontal Pod Autoscaler, metrics-server, and a load
test that genuinely scales pods 1 → 5 → 1 on a kind cluster.*

## Problem this project solves

A fixed replica count either wastes money at idle or falls over under a spike.
This lab shows how Kubernetes **scales automatically**: the Horizontal Pod
Autoscaler (HPA) watches CPU utilization and adds/removes pods within bounds.
It makes the moving parts concrete — the CPU **request** as the utilization
denominator, the **metrics-server** dependency, and how a **load test**
actually triggers scaling — and the full cycle was run and observed on a local
kind cluster (see [TEST_RESULTS.md](TEST_RESULTS.md)).

## What it demonstrates

- **Deployment** with CPU **requests/limits** (250m/500m), probes, non-root +
  read-only-rootfs security context (with a writable `/tmp` emptyDir for the JVM)
- **Service** (ClusterIP) and **ConfigMap** (`PORT`, `MAX_WORK_MS`)
- **HPA** (`autoscaling/v2`): min 1 / max 5, CPU target 60%, fast scale-up,
  300s scale-down stabilization
- **metrics-server** as the HPA's hard dependency (including the
  `<unknown>/60%` gotcha and the kind `--kubelet-insecure-tls` patch)
- **CPU load generation** via a deliberate `/work` busy-loop endpoint
- Observed **scale-up (1→2→4→5)** and **scale-down (5→4→3→2→1)** behavior

## Architecture

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
src/kubernetesautoscalinglab/Main.java   app: / /health /ready /work /metrics
docker/Dockerfile                        multi-stage, non-root image
k8s/kustomization.yaml                   apply everything: kubectl apply -k k8s/
k8s/deployment.yaml                      CPU requests/limits, probes, security, /tmp
k8s/service.yaml                         ClusterIP (port 80 → containerPort 8080)
k8s/hpa.yaml                             HPA: min 1 / max 5 / CPU 60% + behavior
k8s/configmap.yaml                       PORT, MAX_WORK_MS
k8s/resource-limits-example.yaml         reference: LimitRange/ResourceQuota (not applied)
k8s/vpa.example.yaml                     reference: VPA, needs its own controller (not applied)
load-test/k6-script.js                   ramping k6 profile against /work
load-test/README.md                      hey + k6 + in-cluster load commands
docs/                                    autoscaling, metrics-server, plans
README.md  TESTING.md  TEST_RESULTS.md
```

## What is implemented

- Java 21 app (built-in `HttpServer`, no dependencies) with `/`, `/health`,
  `/ready`, `/work?ms=`, `/metrics`; `MAX_WORK_MS` caps a single `/work` call
- Multi-stage Dockerfile (JDK never ships; runs as uid 10001)
- All Kubernetes manifests plus a kustomization for one-command apply/delete
- HPA manifest with explained scale-up/scale-down behavior
- k6 load script + hey/in-cluster alternatives
- Validation docs with the exact kind + metrics-server workflow

## What was verified vs what is not proven

**Verified on a local kind cluster (2026-07-10, see [TEST_RESULTS.md](TEST_RESULTS.md)):**
compile, all endpoints, Docker build, server-side dry-run, deployment,
metrics-server, and a full HPA cycle — scale-up 1→2→4→5 under load and
scale-down 5→4→3→2→1 after it, matching the configured behavior exactly.

**Not proven / out of scope:** autoscaling on any other cluster (re-run the
workflow below to reproduce it yourself — evidence is per-run, not permanent),
real cloud deployment, production monitoring, real traffic patterns, and the
reference-only manifests (`resource-limits-example.yaml`, `vpa.example.yaml`).

## Local Java validation

Requires JDK 21 (or run the same commands in an `eclipse-temurin:21-jdk`
container):

```bash
javac -d out src/kubernetesautoscalinglab/*.java
PORT=8080 MAX_WORK_MS=5000 java -cp out kubernetesautoscalinglab.Main
# in another terminal:
curl -i http://localhost:8080/health          # 200 ok
curl -i "http://localhost:8080/work?ms=5"     # 200, worked_ms=5
curl -i "http://localhost:8080/work?ms=999999" # capped at MAX_WORK_MS
```

Full endpoint matrix in [TESTING.md](TESTING.md).

## Docker build

```bash
docker build -f docker/Dockerfile -t autoscaling-java-app:0.1.0 .
```

The image is local-only and versioned — `latest` is deliberately not used.

The metrics-server install command is pinned to `v0.8.1` instead of the moving `latest` release manifest so the lab is more reproducible. If a future Kubernetes version requires a newer metrics-server, update the version deliberately and record the change in `TEST_RESULTS.md`.

## Kubernetes workflow (kind)

```bash
# 1. cluster + image
kind create cluster --name autoscaling-lab
kind load docker-image autoscaling-java-app:0.1.0 --name autoscaling-lab

# 2. metrics-server (the HPA is blind without it)
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/download/v0.8.1/components.yaml
kubectl patch deployment metrics-server -n kube-system --type='json' \
  -p='[{"op":"add","path":"/spec/template/spec/containers/0/args/-","value":"--kubelet-insecure-tls"}]'

# 3. the lab
kubectl apply -k k8s/

# 4. verify (HPA shows <unknown>/60% for a minute or two, then a number)
kubectl get pods && kubectl get svc && kubectl get hpa
kubectl top pods

# 5. load test (see TESTING.md / load-test/README.md)
kubectl port-forward svc/autoscaling-java-app 8080:80
hey -z 3m -c 50 "http://localhost:8080/work?ms=200"   # or k6, see below

# 6. watch it scale
kubectl get hpa -w

```

Cleanup commands are intentionally omitted. Confirm the active Kubernetes context before removing only the disposable local lab resources you created.

## What evidence to record

When you run the lab, record in `TEST_RESULTS.md`: the HPA reading before
load, the exact load command, the `kubectl get hpa`/`kubectl describe hpa`
output showing replicas increasing with `cpu … above target` events, and the
post-stabilization scale-down back to 1. Never record replica counts you did
not actually see.

## Security notes

- **No secrets or credentials** — the app needs none; the image is local-only.
- Deployment runs **non-root**, read-only rootfs, dropped capabilities,
  `RuntimeDefault` seccomp; `/tmp` is the only writable mount.
- `MAX_WORK_MS` caps a single `/work` request so a load test can't pin a pod
  indefinitely.
- Only load-test **your own disposable cluster** — never a public endpoint.

## Resume Value

Created a Kubernetes autoscaling lab with a measurable Java workload, resource requests, HPA configuration, metrics-server setup, repeatable load generation, and recorded local kind scaling evidence.

## Future improvements

- Memory-based and custom/external metrics (KEDA) alongside CPU.
- Tune target and stabilization windows from a measured baseline.
- A `PodDisruptionBudget` and graceful-shutdown handling for scale-down.
- Wire `/metrics` into Prometheus and alert on saturation.

## What I learned

- The HPA scales on CPU as a **percentage of the request** — the request is
  the key number, and a missing request means no scaling at all.
- **metrics-server is a hard prerequisite**: the HPA reads `metrics.k8s.io`,
  shows `<unknown>` without it, and on kind needs `--kubelet-insecure-tls`.
- The `behavior` block is visible in practice: replicas doubled every ~30s
  under load, then held for exactly 300s before stepping down 1/minute.
- `minReplicas`/`maxReplicas` act as availability and cost guardrails — the
  observed ramp stopped precisely at 5.
