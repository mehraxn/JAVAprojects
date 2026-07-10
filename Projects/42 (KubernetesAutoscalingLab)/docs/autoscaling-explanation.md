# Autoscaling Explanation

This document explains the concepts behind the manifests in [`../k8s/`](../k8s/).
The behavior described here was verified on a local kind cluster — see
[../TEST_RESULTS.md](../TEST_RESULTS.md) for the observed 1→5→1 scaling cycle.

## 1. CPU requests and limits

Every container declares two CPU numbers (`k8s/deployment.yaml`):

| Field   | Meaning                                                                 | This lab |
| ------- | ----------------------------------------------------------------------- | -------- |
| request | Guaranteed floor. The scheduler reserves this and only places the pod on a node that has it free. | `250m`   |
| limit   | Hard ceiling. CPU above the limit is **throttled** (the process is slowed, not killed).           | `500m`   |

`250m` means 250 millicores = 0.25 of one CPU core. `1000m` = 1 full core.

Memory works the same way except the failure mode differs: memory over the
**limit** is not throttled — the container is **OOMKilled** and restarted.

**Why the request matters for autoscaling:** the HPA's CPU utilization is a
*percentage of the request*, not of the node or the limit. This makes the
request the single most important number in the whole lab.

## 2. Horizontal Pod Autoscaler (HPA)

The HPA (`k8s/hpa.yaml`) is a controller that periodically (about every 15s):

1. Asks **metrics-server** for the current CPU usage of each pod.
2. Computes the average utilization = `actual usage / cpu request`.
3. Computes the desired replica count and, if it changed, scales the
   Deployment.

The core formula:

```
desiredReplicas = ceil( currentReplicas × currentUtilization / targetUtilization )
```

Example with this lab's `targetUtilization = 60`:

- 1 pod running at 150% of its request → `ceil(1 × 150 / 60)` = **3 pods**.
- 3 pods settling at 55% → `ceil(3 × 55 / 60)` = `ceil(2.75)` = **3 pods** (stable).
- 3 pods dropping to 15% → `ceil(3 × 15 / 60)` = `ceil(0.75)` = **1 pod**.

## 3. minReplicas and maxReplicas

```yaml
minReplicas: 1   # never scale below this, even at idle
maxReplicas: 5   # never scale above this, even under extreme load
```

- `minReplicas` guarantees availability and prevents scaling to zero (plain HPA
  cannot go to zero anyway).
- `maxReplicas` is a **safety ceiling** and a cost control. Under a runaway load
  test the app is capped at 5 pods rather than consuming the whole cluster.

## 4. targetCPUUtilizationPercentage

In the `autoscaling/v2` API this is expressed as:

```yaml
metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 60
```

`60` means: **keep the average pod CPU at 60% of its request (250m), i.e. about
150m per pod.** Pick it below 100% so there is headroom to absorb a spike while
new pods start.

- Set it **too low** (e.g. 20) → over-scales, wastes resources, flaps.
- Set it **too high** (e.g. 95) → pods saturate before new ones arrive; latency
  spikes during scale-up.

The `behavior` block adds stabilization windows: scale **up** immediately
(`stabilizationWindowSeconds: 0`) but scale **down** slowly (`300s`) so a brief
dip in traffic does not immediately remove pods.

## 5. metrics-server requirement

The HPA has **no CPU data of its own**. It reads the `metrics.k8s.io` API,
which is served by **metrics-server**. If metrics-server is not installed:

```
kubectl get hpa
NAME                   TARGETS         MINPODS   MAXPODS   REPLICAS
autoscaling-java-app   <unknown>/60%   1         5         1
```

`<unknown>` means the HPA cannot see CPU and **will not scale**. See
[metrics-server.md](metrics-server.md).

## 6. How load testing would trigger scaling

Full loop (see [../load-test/README.md](../load-test/README.md)):

1. A load generator (k6 or hey) sends many concurrent `GET /work` requests.
2. The `/work` busy loop pushes each pod's CPU toward its `500m` limit.
3. metrics-server samples that CPU every ~15s.
4. The HPA computes average utilization; it exceeds the 60% target.
5. The HPA raises the Deployment's replica count (up to `maxReplicas: 5`).
6. New pods pass their readiness probe and the Service load-balances across them.
7. Average per-pod CPU falls back toward 60% and the count stabilizes.
8. Load stops → CPU drops → after the 300s scale-down window, the HPA removes
   pods back toward `minReplicas: 1`.

## 7. The commands that exercise all of this

```bash
kubectl apply -k k8s/

kubectl get hpa autoscaling-java-app --watch
kubectl get pods -l app=autoscaling-java-app --watch
kubectl top pods -l app=autoscaling-java-app
kubectl describe hpa autoscaling-java-app
```

The full workflow (kind cluster, metrics-server install, load test) is in
[../TESTING.md](../TESTING.md); the recorded outcome of actually running it is
in [../TEST_RESULTS.md](../TEST_RESULTS.md).
