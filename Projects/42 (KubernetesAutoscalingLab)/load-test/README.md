# Load Testing (Conceptual)

This directory explains **how** you would drive load at the app to trigger
autoscaling. **No load test is run in this repository, and none should be run
against any endpoint you do not own.** The commands below are documented for
learning and are marked **NOT executed**.

## Why load testing matters here

The Horizontal Pod Autoscaler scales on **average CPU utilization** across the
Deployment's pods (see [../docs/autoscaling-explanation.md](../docs/autoscaling-explanation.md)).
CPU only rises if something is actually doing work. The app exposes a
`GET /work?ms=NNN` endpoint that deliberately burns CPU in a busy loop. A load
generator sends many concurrent `/work` requests, average CPU climbs past the
HPA target (60%), and the HPA adds pods. When the load stops, CPU falls and the
HPA scales back down after its stabilization window.

```
load generator ──HTTP /work──> Service ──> Pods (CPU rises)
                                              │
                                     metrics-server samples CPU
                                              │
                                             HPA compares to 60% target
                                              │
                                   scales Deployment 1 → … → up to 5 pods
```

## Two common tools

### hey — simplest

[`hey`](https://github.com/rakyll/hey) is a tiny HTTP load generator. Good for a
quick "send N requests with C concurrency" burst.

```bash
# NOT executed. Requires a running cluster, the app deployed, and a reachable URL.
# -z  duration, -c concurrency. Target the CPU-burning endpoint.
hey -z 3m -c 50 http://<app-url>/work?ms=200
```

### k6 — scriptable

[`k6`](https://k6.io) lets you script ramping stages, thresholds, and custom
checks in JavaScript. A ramping profile is the realistic way to watch the HPA
step up and then step down.

`k6-script.js` (illustrative — **NOT executed**):

```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '1m', target: 20 },  // ramp up:   warm up, CPU begins to rise
    { duration: '3m', target: 60 },  // steady:    hold load above the HPA target
    { duration: '1m', target: 0 },   // ramp down: let CPU fall, watch scale-down
  ],
};

export default function () {
  const res = http.get('http://<app-url>/work?ms=200');
  check(res, { 'status is 200': (r) => r.status === 200 });
  sleep(0.5);
}
```

```bash
# NOT executed.
k6 run load-test/k6-script.js
```

## Watching the scaling while a test runs

In a separate terminal you would observe the HPA and pods. **NOT executed:**

```bash
# NOT executed — no cluster exists in this repo.
kubectl get hpa autoscaling-java-app --watch
kubectl get pods -l app=autoscaling-java-app --watch
kubectl top pods -l app=autoscaling-java-app     # needs metrics-server
```

## Safety rules

- Only target a **disposable, in-cluster** URL that you own.
- Use a **bounded** duration and concurrency, and a manual stop plan.
- `MAX_WORK_MS` (ConfigMap) caps a single `/work` request so no pod is pinned
  indefinitely.
- Never point a load generator at a public or production endpoint — that is
  indistinguishable from a denial-of-service attack.
