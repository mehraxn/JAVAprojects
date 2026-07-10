# Load Testing

How to drive load at the app to trigger autoscaling. These commands were used
to produce the observed 1→5→1 scaling cycle recorded in
[../TEST_RESULTS.md](../TEST_RESULTS.md); the full plan (expected behavior,
what evidence to record) is in [../docs/load-test-plan.md](../docs/load-test-plan.md).

**Safety: only target a disposable cluster you own.** Never point a load
generator at a public or production endpoint — that is indistinguishable from
a denial-of-service attack.

## Why load testing matters here

The HPA scales on **average CPU utilization** across the Deployment's pods.
CPU only rises if something is doing work. The app's `GET /work?ms=NNN`
endpoint burns CPU in a busy loop; a load generator sends many concurrent
`/work` requests, average CPU climbs past the HPA target (60% of the 250m
request), and the HPA adds pods. When the load stops, CPU falls and the HPA
scales back down after its 300s stabilization window.

```
load generator ──HTTP /work──> Service ──> Pods (CPU rises)
                                              │
                                     metrics-server samples CPU
                                              │
                                     HPA compares to 60% target
                                              │
                                scales Deployment 1 ◀──▶ up to 5 pods
```

First, port-forward the Service (for hey/k6 from your machine):

```bash
kubectl port-forward svc/autoscaling-java-app 8080:80
```

## hey — simplest

[`hey`](https://github.com/rakyll/hey): `-z` duration, `-c` concurrency.

```bash
hey -z 3m -c 50 "http://localhost:8080/work?ms=200"
```

## k6 — scriptable

[`k6`](https://k6.io) runs the ramping profile in
[`k6-script.js`](k6-script.js) (1m ramp-up → 3m hold at 60 VUs → 1m
ramp-down):

```bash
k6 run load-test/k6-script.js
```

Without a local k6 install, use the official image
(`host.docker.internal` reaches your host's port-forward):

```bash
docker run --rm -i -e BASE_URL=http://host.docker.internal:8080 \
  grafana/k6 run - < load-test/k6-script.js
```

Note: `host.docker.internal` works on Docker Desktop. On Linux, prefer the in-cluster load generator if the Dockerized k6 container cannot reach the host port-forward.

## In-cluster generator — no tools needed, best load balancing

A port-forward pins all traffic to a single pod, so once the HPA adds pods the
new ones stay idle. Generating load **inside the cluster** against the Service
DNS name spreads requests across all pods — this is what produced the recorded
scaling cycle:

```bash
kubectl run load-generator --image=busybox:1.36 --restart=Never -- /bin/sh -c \
  'for i in 1 2 3 4 5 6 7 8; do (while true; do wget -q -O /dev/null "http://autoscaling-java-app/work?ms=200"; done) & done; sleep 330'

kubectl delete pod load-generator   # stop early / cleanup
```

## Watching the scaling while a test runs

```bash
kubectl get hpa autoscaling-java-app --watch
kubectl get pods -l app=autoscaling-java-app --watch
kubectl top pods -l app=autoscaling-java-app     # needs metrics-server
kubectl describe hpa autoscaling-java-app        # events explain each rescale
```

## Safety rules

- Only target a **disposable, in-cluster** URL that you own.
- Use a **bounded** duration and concurrency, and know how to stop
  (`Ctrl-C` / `kubectl delete pod load-generator`).
- `MAX_WORK_MS` (ConfigMap) caps a single `/work` request so no pod is pinned
  indefinitely.
