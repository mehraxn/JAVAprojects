# metrics-server

The Horizontal Pod Autoscaler in this lab scales on CPU utilization. It has no
way to measure CPU by itself — it reads the **`metrics.k8s.io`** API, and that
API is served by a component called **metrics-server**. Without it the HPA is
blind. During validation on a kind cluster, both states below were observed:
`<unknown>/60%` before metrics-server was ready, and a numeric reading after
(see [../TEST_RESULTS.md](../TEST_RESULTS.md)).

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

When metrics-server is missing or not yet ready, the HPA cannot read CPU
(this exact output was observed during validation, in the minute before
metrics-server produced its first samples):

```
kubectl get hpa
NAME                   TARGETS         MINPODS   MAXPODS   REPLICAS
autoscaling-java-app   <unknown>/60%   1         5         1
```

`<unknown>/60%` = "I have a 60% target but no current reading." The HPA holds
the replica count and does not scale.

## Installing it

Pinned release manifest used by this lab:

```bash
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/download/v0.8.1/components.yaml
```

Pinning avoids the moving `latest` manifest and makes the validation workflow easier to reproduce. Update the version deliberately if your Kubernetes cluster requires a newer metrics-server release.

On local dev clusters that use self-signed kubelet certs (kind, minikube,
k3d), metrics-server also needs the `--kubelet-insecure-tls` flag:

```bash
kubectl patch deployment metrics-server -n kube-system \
  --type='json' \
  -p='[{"op":"add","path":"/spec/template/spec/containers/0/args/-","value":"--kubelet-insecure-tls"}]'
```

(minikube users can instead run `minikube addons enable metrics-server`.)

Expect a minute or two before the first samples appear.

## Verifying it works

```bash
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
