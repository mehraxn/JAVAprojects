# metrics-server

The Horizontal Pod Autoscaler in this lab scales on CPU utilization. It has no
way to measure CPU by itself — it reads the **`metrics.k8s.io`** API, and that
API is served by a component called **metrics-server**. Without it the HPA is
blind. **metrics-server is not installed in this repository.**

## What it is

metrics-server is a cluster add-on that:

- scrapes the **kubelet** on each node (via the Summary API) for live CPU and
  memory usage per pod and per node,
- keeps only a short in-memory window (it is **not** a long-term store like
  Prometheus),
- exposes that data through the aggregated `metrics.k8s.io` API.

It powers both `kubectl top` and the HPA's `type: Resource` CPU/memory metrics.

## Why the HPA needs it

```
kubelet (per node)  ──>  metrics-server  ──>  metrics.k8s.io API  ──>  HPA
                                                                  └──>  kubectl top
```

When metrics-server is missing or unhealthy, the HPA cannot read CPU:

```
# Illustrative output — NOT executed
kubectl get hpa
NAME                   TARGETS         MINPODS   MAXPODS   REPLICAS
autoscaling-java-app   <unknown>/60%   1         5         1
```

`<unknown>/60%` = "I have a 60% target but no current reading." The HPA holds
the replica count and does not scale.

## Installing it — NOT executed

These commands are documented for learning only. **None were run.**

```bash
# NOT executed. Standard release manifest:
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml

# NOT executed. On local dev clusters that use self-signed kubelet certs
# (kind, minikube, k3d), metrics-server often needs this flag added:
#   --kubelet-insecure-tls
# via `kubectl edit deployment metrics-server -n kube-system`.

# minikube ships an addon:
# NOT executed
minikube addons enable metrics-server
```

## Verifying it works — NOT executed

```bash
# NOT executed
kubectl get deployment metrics-server -n kube-system
kubectl top nodes
kubectl top pods -l app=autoscaling-java-app
```

Once `kubectl top pods` returns real numbers, the HPA `TARGETS` column changes
from `<unknown>/60%` to an actual percentage such as `35%/60%`, and autoscaling
can function.

## metrics-server vs Prometheus

They are different tools and are easy to confuse:

| | metrics-server | Prometheus |
| --- | --- | --- |
| Purpose | live CPU/mem for HPA + `kubectl top` | general metrics + alerting + history |
| Storage | seconds, in-memory | long-term time series |
| HPA use | `type: Resource` metrics | `type: Pods`/`External` via an adapter |

This lab's HPA uses only **`type: Resource`** CPU, so **metrics-server is the
only requirement**. The app's own `/metrics` endpoint is illustrative and is
**not** wired into the HPA.
