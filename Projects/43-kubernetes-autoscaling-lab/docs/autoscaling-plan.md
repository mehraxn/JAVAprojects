# Autoscaling Plan

How autoscaling is configured in this lab, why each piece is required, and
where the boundaries are. The concept deep-dive lives in
[autoscaling-explanation.md](autoscaling-explanation.md); this file is the
plan of record for the manifests in [`../k8s/`](../k8s/).

## What the HPA does

The Horizontal Pod Autoscaler (`k8s/hpa.yaml`, `autoscaling/v2`) is a control
loop that runs about every 15 seconds: it reads each pod's live CPU usage from
the `metrics.k8s.io` API, averages it across the Deployment's pods, compares
that to the target, and resizes the Deployment within
`minReplicas: 1` … `maxReplicas: 5` using:

```
desiredReplicas = ceil( currentReplicas × currentUtilization / targetUtilization )
```

## Why CPU requests are required

HPA CPU utilization is a **percentage of the container's CPU request**, not of
the node or the limit. The Deployment requests `cpu: 250m`, so "60%
utilization" means ~150m of actual usage per pod. A container with no CPU
request gives the HPA no denominator — the HPA reports `<unknown>` and never
scales. This is why `resources.requests.cpu` in `k8s/deployment.yaml` is the
single most load-bearing line in the lab.

## How target average utilization works

`averageUtilization: 60` tells the HPA to keep the **average across all pods**
at 60% of the request. It is deliberately below 100% so there is headroom to
absorb a spike while new pods start. Worked examples: 1 pod at 150% →
`ceil(1 × 150/60)` = 3 pods; 3 pods at 15% → `ceil(3 × 15/60)` = 1 pod.

## metrics-server requirement

The HPA has no CPU data of its own — **metrics-server must be installed** or
the HPA shows `<unknown>/60%` and holds the replica count forever. On kind and
other local clusters with self-signed kubelet certs it also needs the
`--kubelet-insecure-tls` flag. Install and patch commands are in
[../TESTING.md](../TESTING.md); background in
[metrics-server.md](metrics-server.md).

## Scale-up behavior

```yaml
scaleUp:
  stabilizationWindowSeconds: 0     # react immediately
  policies:
    - type: Percent
      value: 100                    # may double the replica count…
      periodSeconds: 30             # …every 30 seconds
```

Scale-up is intentionally aggressive: under load, waiting costs latency. From
1 replica the ramp can go 1 → 2 → 4 → 5 in about 90 seconds of sustained
overload.

## Scale-down behavior

```yaml
scaleDown:
  stabilizationWindowSeconds: 300   # wait 5 minutes of calm first
  policies:
    - type: Pods
      value: 1                      # then remove at most 1 pod…
      periodSeconds: 60             # …per minute
```

Scale-down is intentionally slow: the HPA uses the **highest** desired count
seen in the past 300s, so a brief dip in traffic does not flap pods away, and
even after the window it steps down one pod per minute.

## Limitations of CPU-based autoscaling

- CPU is a proxy, not the goal — users care about latency/queue depth, which
  can degrade before or without CPU saturation (e.g. I/O-bound services).
- It reacts *after* load arrives; a sharp spike still hits the old capacity
  until new pods pass readiness.
- It cannot scale to zero (plain HPA minimum is 1).
- JVM warm-up briefly burns CPU on start, which can look like load.
- Memory, custom metrics (queue length, RPS), or event-driven scaling (KEDA)
  are often better signals for real workloads.

## What is implemented in this lab

Deployment (CPU 250m request / 500m limit, probes, non-root, read-only rootfs
with a writable `/tmp` emptyDir), ClusterIP Service, ConfigMap (`PORT`,
`MAX_WORK_MS`), the HPA above, a kustomization for one-command apply, a CPU-
burning `/work` endpoint to drive load, and a documented kind + metrics-server
+ load-test validation workflow. Actual observed results are recorded in
[../TEST_RESULTS.md](../TEST_RESULTS.md).

## What is not production-grade

- `250m/500m` and `60%` are sensible lab defaults, not tuned against real
  traffic.
- Single metric (CPU) only; no memory/custom/external metrics.
- No PodDisruptionBudget, no monitoring/alerting stack, no multi-node
  scheduling concerns — the lab runs on a single-node local cluster.
- `resource-limits-example.yaml` and `vpa.example.yaml` are reference examples
  and are not applied by default (VPA additionally requires its own
  controller/CRDs).
