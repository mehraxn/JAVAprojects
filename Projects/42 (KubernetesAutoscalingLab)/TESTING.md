# Testing — Kubernetes Autoscaling Lab

> **Nothing was executed.** No Docker build, `kubectl`, cluster, metrics-server
> install, or load test ran. **Autoscaling was not tested.** This documents the
> static review and the checks a disposable cluster *would* use.

## 1. Static validation checklist

- [ ] App compiles conceptually; `/health`, `/ready`, `/work`, `/metrics` exist in source.
- [ ] HPA `scaleTargetRef` name/kind/apiVersion match the Deployment.
- [ ] Deployment sets `resources.requests.cpu` (HPA utilization denominator).
- [ ] `minReplicas (1) ≤ replicas (1) ≤ maxReplicas (5)`.
- [ ] Ports align: ConfigMap `PORT` = containerPort = probe ports = Service targetPort.
- [ ] ResourceQuota budget covers `maxReplicas × request` (5 × 250m = 1250m).

## 2. File existence checks

- [ ] `src/kubernetesautoscalinglab/Main.java`, `docker/Dockerfile`
- [ ] `k8s/deployment.yaml`, `service.yaml`, `hpa.yaml`, `configmap.yaml`, `resource-limits-example.yaml`
- [ ] `load-test/README.md`, `docs/autoscaling-explanation.md`, `docs/metrics-server.md`
- [ ] `README.md`, `TESTING.md`

## 3. YAML / config review checklist

- [ ] All YAML well-formed.
- [ ] HPA `averageUtilization: 60` matches the docs' worked examples.
- [ ] `behavior` block: fast scale-up, 300s scale-down window.
- [ ] `configmap.yaml` supplies `PORT` + `MAX_WORK_MS`; Deployment `envFrom` references it.

## 4. Security checks

- [ ] **No real secrets** — none required or present.
- [ ] **No real credentials** — no registry/cluster creds.
- [ ] **No production endpoints** — placeholder image; no external targets.
- [ ] Non-root, read-only rootfs, dropped capabilities in the Deployment.

## 5. Commands normally used — NOT executed

```bash
# NOT executed
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
kubectl apply -f k8s/configmap.yaml -f k8s/deployment.yaml -f k8s/service.yaml -f k8s/hpa.yaml
kubectl get hpa autoscaling-java-app --watch
kubectl top pods -l app=autoscaling-java-app
hey -z 3m -c 50 http://<app-url>/work?ms=200        # or: k6 run load-test/k6-script.js
```

## 6. Expected results in a proper environment

- With metrics-server present, `kubectl get hpa` shows a numeric target (not `<unknown>/60%`).
- Under load, per-pod CPU exceeds 60% of the request; the HPA adds pods up to 5.
- After load stops, CPU drops and (post-300s window) the HPA scales back toward 1.
- `resource-limits-example.yaml` enforces namespace defaults/quota.

## 7. Manual review checklist (portfolio quality)

- [ ] README explains request-vs-limit and the HPA formula clearly.
- [ ] metrics-server dependency is called out (the classic gotcha).
- [ ] Load-testing story is concrete (`/work` endpoint ↔ scaling).
- [ ] Every command marked NOT executed; no fake badges/screenshots.
- [ ] Honest that scaling was not observed.
