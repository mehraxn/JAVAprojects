# Load Test Plan

How to drive CPU load at the lab app and watch the HPA react. Safety first:
only ever target a **disposable, in-cluster URL you own** (here: a
port-forward to your own kind/minikube cluster) with bounded duration and
concurrency. Never point a load generator at a public or production endpoint.

## How CPU load is generated

The app exposes `GET /work?ms=NNN`, which spins in a busy loop of real math
for ~NNN milliseconds on one thread. Each in-flight `/work` request therefore
occupies CPU continuously, and concurrent requests compete for the pod's
`500m` CPU limit — usage climbs toward the limit, which is 200% of the `250m`
request. `ms` is capped by `MAX_WORK_MS` (ConfigMap, default 2000) so a single
request can never pin a pod indefinitely; requests over the cap are clamped,
not rejected.

The math: the HPA target is 60% of 250m = **150m average per pod**. A stream
of concurrent `/work?ms=200` requests easily pushes a single pod past that,
so the HPA must add pods to bring the average back down.

## Reaching the Service

The Service is ClusterIP (not exposed). From your machine, port-forward:

```bash
kubectl port-forward svc/autoscaling-java-app 8080:80
```

Leave that running; the app is now at `http://localhost:8080`.

## Running hey

[`hey`](https://github.com/rakyll/hey) — quickest option:

```bash
hey -z 3m -c 50 "http://localhost:8080/work?ms=200"
```

`-z 3m` = run for 3 minutes, `-c 50` = 50 concurrent workers. Bounded by
design: it stops on its own.

## Running k6

[`k6`](https://k6.io) with the ramping profile in
[`../load-test/k6-script.js`](../load-test/k6-script.js) (1m ramp-up → 3m
hold at 60 virtual users → 1m ramp-down):

```bash
k6 run load-test/k6-script.js
```

No k6 installed? Use the official image (`host.docker.internal` reaches the
port-forward on your host):

```bash
docker run --rm -i -e BASE_URL=http://host.docker.internal:8080 \
  grafana/k6 run - < load-test/k6-script.js
```

Note: `host.docker.internal` works on Docker Desktop. On Linux, prefer the in-cluster load generator if the Dockerized k6 container cannot reach the host port-forward.

Alternative with no extra tools at all — an in-cluster busybox load generator
(several parallel request loops inside the cluster, no port-forward needed):

```bash
kubectl run load-generator --image=busybox:1.36 --restart=Never -- /bin/sh -c \
  'for i in 1 2 3 4 5 6 7 8; do (while true; do wget -q -O /dev/null "http://autoscaling-java-app/work?ms=200"; done) & done; sleep 300'
kubectl delete pod load-generator   # stop/cleanup
```

## Watching the HPA

In separate terminals while the load runs:

```bash
kubectl get hpa -w
kubectl get pods -w
kubectl top pods                       # needs metrics-server
kubectl describe hpa autoscaling-java-app   # events: why it scaled
```

## Expected scale-up behavior

Within ~30–60s of sustained load, `kubectl get hpa` shows utilization well
above `60%` and `REPLICAS` steps up (the scale-up policy allows doubling every
30s, so 1 → 2 → 4 → 5). New pods appear, pass readiness, and share the load;
average utilization falls back toward the target.

## Expected scale-down behavior

After the load stops, utilization drops to a few percent — but replicas hold
for the **300-second stabilization window**, then step down **one pod per
minute** toward `minReplicas: 1`. Full recovery takes roughly 6–9 minutes;
that slowness is deliberate anti-flapping.

## What evidence to record

Record in [../TEST_RESULTS.md](../TEST_RESULTS.md), honestly:

- `kubectl get hpa` output **before** load (target reading, e.g. `2%/60%`, 1 replica)
- the exact load command used
- `kubectl get hpa` / `kubectl get pods` output **during** load showing
  utilization above target and the increased replica count
- `kubectl describe hpa` scaling events (`New size: N; reason: cpu resource
  utilization above target`)
- `kubectl get hpa` output after the stabilization window showing replicas
  back at 1
- anything that did NOT work or was not run

Do not record replica counts you did not actually see.
