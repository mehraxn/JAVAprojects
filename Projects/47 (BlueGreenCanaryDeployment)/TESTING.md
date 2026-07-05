# Testing — Blue-Green & Canary Deployment

> **Nothing was executed.** No Java, Docker, Kubernetes, or Helm command ran; no
> cluster exists and **traffic switching was not tested.** This documents static
> review and expected behavior.

## 1. Static validation checklist

- [ ] Apps compile (fixed `package app;`); both expose `/version`, `/health`, `/ready`.
- [ ] Blue-green: `service.yaml` selects `track: blue`; `switch-service-to-green.yaml` = same Service, `track: green`.
- [ ] Canary: `service.yaml` selects `app` only (both tracks); stable/canary carry `track` labels.
- [ ] Replica ratio (9:1) matches the ~10% described; `canary-weight: "10"` matches.
- [ ] Helm chart Service selector follows `activeTrack`.

## 2. File existence checks

- [ ] `app-v1/`, `app-v2/` (src + Dockerfile)
- [ ] `k8s/blue-green/` (blue, green, service, switch-service-to-green)
- [ ] `k8s/canary/` (stable, canary, service, ingress-canary-example)
- [ ] `helm/rollout/**`, `monitoring/rollout-alerts.example.yml`
- [ ] `docs/blue-green.md`, `canary.md`, `rollback.md`, `README.md`, `TESTING.md`

## 3. YAML / config review checklist

- [ ] All YAML well-formed; multi-doc ingress uses proper `---` separators.
- [ ] Ports align (containerPort 8080, named `http`; Services target `http`).
- [ ] Blue/green selectors/labels line up; canary ingress services select the two tracks.

## 4. Security checks

- [ ] **No real secrets** — none present.
- [ ] **No real credentials** — no registry/cluster creds.
- [ ] **No production endpoints** — `my-java-app:v1/v2`, `*.example.invalid` only.
- [ ] Deployments non-root, read-only rootfs, dropped capabilities.

## 5. Commands normally used — NOT executed

```bash
# NOT executed — blue-green
kubectl apply -f k8s/blue-green/
kubectl apply -f k8s/blue-green/switch-service-to-green.yaml   # switch to v2
kubectl apply -f k8s/blue-green/service.yaml                   # roll back to v1
# NOT executed — canary
kubectl apply -f k8s/canary/
kubectl scale deployment java-app-canary --replicas=3          # raise %
kubectl scale deployment java-app-canary --replicas=0          # roll back
```

## 6. Expected results in a proper environment

- Blue-green: `/version` = v1, apply switch → `/version` = v2, re-apply service → v1 (instant).
- Canary: sampling `/version` repeatedly shows ~90/10 stable/canary; raising replicas shifts the split.
- Ingress-weight canary sends exactly the configured percentage to v2.
- Dropping the canary to 0% returns to 100% stable with no cold start.

## 7. Manual review checklist (portfolio quality)

- [ ] README contrasts blue-green vs canary with clear trade-offs.
- [ ] Both traffic-split mechanisms (replica ratio, ingress weight) are shown.
- [ ] Rollback + schema-compatibility risk are covered.
- [ ] Every command marked NOT executed; no fake badges/screenshots.
- [ ] Honest that no switching was tested.
