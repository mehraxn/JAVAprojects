# Testing — Kubernetes Autoscaling Lab

Exact commands to validate this lab locally. Results actually observed with
these commands are recorded in [TEST_RESULTS.md](TEST_RESULTS.md). Commands
use POSIX shell syntax; on Windows use Git Bash or adapt for PowerShell.
Run everything from this project folder.

## A) Java-only validation

Requires JDK 21. No JDK? Run the same commands inside
`docker run --rm -it -p 8080:8080 -v "$PWD:/w" -w /w eclipse-temurin:21-jdk bash`.

```bash
javac -d out src/kubernetesautoscalinglab/*.java
PORT=8080 MAX_WORK_MS=5000 java -cp out kubernetesautoscalinglab.Main
```

In another terminal:

```bash
curl -i http://localhost:8080/
curl -i http://localhost:8080/health
curl -i http://localhost:8080/ready
curl -i "http://localhost:8080/work?ms=5"
curl -i "http://localhost:8080/work?ms=999999"
curl -i http://localhost:8080/metrics
curl -i http://localhost:8080/unknown
```

Expected:

- `/` → 200 with an endpoint index
- `/health` → 200 `ok`; `/ready` → 200 `ready`
- `/work?ms=5` → 200 with `worked_ms=5`
- `/work?ms=999999` → 200 with `worked_ms=` capped at `MAX_WORK_MS`
- `/metrics` → 200 Prometheus-style counters
- `/unknown` → 404

## B) Docker build

```bash
docker build -f docker/Dockerfile -t autoscaling-java-app:0.1.0 .

# optional smoke test of the image itself
docker run --rm -d --name autoscaling-smoke -p 8080:8080 autoscaling-java-app:0.1.0
curl -i http://localhost:8080/health
docker stop autoscaling-smoke
```

## C) Kubernetes manifest validation (no cluster needed)

```bash
kubectl kustomize k8s/                      # renders the 4 core resources
kubectl apply --dry-run=client -k k8s/      # client-side validation
```

With a cluster available, the stronger check:

```bash
kubectl apply --dry-run=server -k k8s/
```

## D) kind cluster workflow

```bash
kind create cluster --name autoscaling-lab
kind load docker-image autoscaling-java-app:0.1.0 --name autoscaling-lab
kubectl apply -k k8s/
```

## E) metrics-server

The HPA cannot scale without it (it shows `<unknown>/60%`):

```bash
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/download/v0.8.1/components.yaml

# kind (and most local clusters) use self-signed kubelet certs — patch:
kubectl patch deployment metrics-server -n kube-system \
  --type='json' \
  -p='[{"op":"add","path":"/spec/template/spec/containers/0/args/-","value":"--kubelet-insecure-tls"}]'
```

Give it a minute or two. Ready when `kubectl top pods` returns numbers and the
HPA `TARGETS` column shows a percentage instead of `<unknown>`.

## F) Verify deployment

```bash
kubectl get pods
kubectl get svc
kubectl get hpa
kubectl top pods
```

Expect 1 app pod `1/1 Running` and the HPA at something like `cpu: 1%/60%`.

## G) Port-forward

```bash
kubectl port-forward svc/autoscaling-java-app 8080:80
```

Leave it running; the app is now at `http://localhost:8080`.

## H) Load test

With hey:

```bash
hey -z 3m -c 50 "http://localhost:8080/work?ms=200"
```

With k6:

```bash
k6 run load-test/k6-script.js
```

k6 not installed? Use the official image:

```bash
docker run --rm -i -e BASE_URL=http://host.docker.internal:8080 \
  grafana/k6 run - < load-test/k6-script.js
```

Note: `host.docker.internal` works on Docker Desktop. On Linux, use the in-cluster BusyBox generator below if the Dockerized k6 container cannot reach the host port-forward.

No load tool at all? Generate load from inside the cluster (this also
load-balances across pods properly; see
[load-test/README.md](load-test/README.md)):

```bash
kubectl run load-generator --image=busybox:1.36 --restart=Never -- /bin/sh -c \
  'for i in 1 2 3 4 5 6 7 8; do (while true; do wget -q -O /dev/null "http://autoscaling-java-app/work?ms=200"; done) & done; sleep 330'
```

## I) Watch scaling

In separate terminals while the load runs:

```bash
kubectl get hpa -w
kubectl get pods -w
kubectl describe hpa autoscaling-java-app   # scaling events with reasons
```

Expected: within ~30–60s utilization exceeds 60% and replicas step up
(1 → 2 → 4 → 5, capped at 5).

## J) Scale-down observation

Stop the load (or let it finish), keep watching. Replicas hold for the
**300-second stabilization window**, then drop **one per minute** back to 1.
Budget ~6–9 minutes; that slowness is deliberate anti-flapping.

## K) Cleanup

```bash
kubectl delete -k k8s/
kind delete cluster --name autoscaling-lab
rm -rf out
docker rmi autoscaling-java-app:0.1.0   # optional
```
