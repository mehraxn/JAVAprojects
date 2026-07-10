# Test Results — Kubernetes Autoscaling Lab

Validation performed on **2026-07-10** on Windows 11 with Docker Desktop
(engine 29.4.2), kubectl, and kind. No JDK was installed on the host, so
compilation ran inside the official `eclipse-temurin:21-jdk` container. The
full lab — kind cluster, metrics-server, HPA, load test — **was actually run**
and the scaling below was genuinely observed, then the cluster was deleted.

## Java compile result — PASS

```
javac -Xlint:all -Werror -d out src/kubernetesautoscalinglab/*.java
```

Compiled cleanly (all lints enabled, warnings as errors) in the
`eclipse-temurin:21-jdk` container.

## Java endpoint tests — PASS

Tested against the built image running locally (`docker run -p 18080:8080`):

| Request | Result |
| --- | --- |
| `GET /` | 200, endpoint index |
| `GET /health` | 200 `ok` |
| `GET /ready` | 200 `ready` |
| `GET /work?ms=5` | 200 `worked_ms=5 spins=70680 …` |
| `GET /work?ms=999999` | 200 `worked_ms=2000 …` — **capped by MAX_WORK_MS=2000** |
| `GET /metrics` | 200, Prometheus-style `app_requests_total` / `app_work_total` |
| `GET /unknown` | 404 |

## /metrics result — PASS

Returns valid Prometheus text exposition (`# HELP` / `# TYPE` + counters).
As documented, this endpoint is illustrative only — the HPA uses
metrics-server, not this endpoint.

## Docker build result — PASS

```
docker build -f docker/Dockerfile -t autoscaling-java-app:0.1.0 .
```

Multi-stage build succeeded; container verified running as non-root
`uid=10001(app)`.

## Kubernetes dry-run result — PASS

- `kubectl kustomize k8s/` renders all four resources (ConfigMap, Service,
  Deployment, HPA) with no errors.
- `kubectl apply --dry-run=server -k k8s/` against the kind cluster: all four
  resources validated by the API server.

## kind deployment result — PASS

```
kind create cluster --name autoscaling-lab        # Ready after 15s
kind load docker-image autoscaling-java-app:0.1.0 --name autoscaling-lab
kubectl apply -k k8s/
```

Pod reached `1/1 Running` with the full security context (non-root,
read-only rootfs + `/tmp` emptyDir — the JVM started fine). Service got a
ClusterIP; port-forward to it answered `/health` with 200.

## metrics-server result — PASS

Installed from the official pinned `v0.8.1` components.yaml manifest, patched with
`--kubelet-insecure-tls` for kind. The HPA showed `cpu: <unknown>/60%` for
roughly a minute (exactly the documented gotcha), then:

```
NAME                   REFERENCE                         TARGETS       MINPODS MAXPODS REPLICAS
autoscaling-java-app   Deployment/autoscaling-java-app   cpu: 1%/60%   1       5       1
```

`kubectl top pods` reported real numbers (2m CPU / 22Mi).

## HPA status before load — RECORDED

`cpu: 1%/60%`, 1 replica (output above).

## Load test command/result — PASS

Two load sources were actually run:

1. **k6 (via Docker, script smoke test)** — through
   `kubectl port-forward svc/autoscaling-java-app 8080:80`:

   ```
   docker run --rm -i -e BASE_URL=http://host.docker.internal:8080 \
     grafana/k6 run --vus 5 --duration 30s - < load-test/k6-script.js
   ```

   Result: 194 requests, **checks_succeeded 100%** (`status is 200`), exit 0.

2. **In-cluster generator (drove the actual scaling)** — 8 parallel wget
   loops against the Service for ~5.5 minutes:

   ```
   kubectl run load-generator --image=busybox:1.36 --restart=Never -- /bin/sh -c \
     'for i in 1 2 3 4 5 6 7 8; do (while true; do wget -q -O /dev/null "http://autoscaling-java-app/work?ms=200"; done) & done; sleep 330'
   ```

`hey` was not installed and was not run; its command is documented in
TESTING.md.

## HPA scale-up observation — OBSERVED (1 → 2 → 4 → 5)

From `kubectl describe hpa autoscaling-java-app` events (Kubernetes' own
timestamps, ~1 minute apart, matching the "double every 30s" scale-up policy):

```
Normal  SuccessfulRescale  New size: 2; reason: cpu resource utilization (percentage of request) above target
Normal  SuccessfulRescale  New size: 4; reason: cpu resource utilization (percentage of request) above target
Normal  SuccessfulRescale  New size: 5; reason: cpu resource utilization (percentage of request) above target
```

Scale-up stopped at 5 = `maxReplicas`, as configured.

## HPA scale-down observation — OBSERVED (5 → 4 → 3 → 2 → 1)

After the load generator finished, the HPA held 5 replicas for the 300-second
stabilization window, then removed exactly one pod per minute (matching the
scale-down policy):

```
Normal  SuccessfulRescale  New size: 4; reason: All metrics below target
Normal  SuccessfulRescale  New size: 3; reason: All metrics below target
Normal  SuccessfulRescale  New size: 2; reason: All metrics below target
Normal  SuccessfulRescale  New size: 1; reason: All metrics below target
```

Final state: 1 replica at `cpu: 0%/60%` — a complete scale-up/scale-down
cycle, ending where it started.

## Cleanup — DONE

`kubectl delete -k k8s/` removed all four resources;
`kind delete cluster --name autoscaling-lab` deleted the cluster.

## Tools unavailable

- `hey` — not installed; documented but not run.
- `minikube` — not installed; the kind path was used instead.
- k6 was not installed on the host; it ran via the official `grafana/k6`
  Docker image.

## Known limitations

- The k6 smoke test validated the script end-to-end but used reduced
  parameters (5 VUs / 30s); the sustained scaling load came from the
  in-cluster busybox generator.
- The precise scale-up was observed via the HPA event log rather than a live
  `kubectl get hpa -w` capture (the events are authoritative and timestamped
  by Kubernetes).
- Single-node kind cluster on one machine; no multi-node scheduling, no cloud
  infrastructure, no production monitoring stack.
- Results are from one run; `250m/500m` requests/limits and the `60%` target
  are lab defaults, not tuned values.


## Final packaging re-check — PASS

After the documentation cleanup for the GitHub-ready ZIP:

- The metrics-server command was pinned to `v0.8.1` instead of the moving `latest` release manifest.
- Dockerized k6 documentation now notes that `host.docker.internal` is Docker Desktop-specific; Linux users should prefer the in-cluster BusyBox load generator if needed.
- Java was recompiled with `javac -Xlint:all -Werror`; `/`, `/health`, `/ready`, `/work`, `/metrics`, and `/unknown` were rechecked successfully.
- Kubernetes YAML files were parsed successfully. Docker/kind/HPA were not rerun during this final packaging check; the HPA evidence above is from the recorded full kind run.
