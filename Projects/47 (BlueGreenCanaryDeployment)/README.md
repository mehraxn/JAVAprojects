# Blue-Green & Canary Deployment

*Two progressive-delivery strategies for a Java service — instant blue-green switching and gradual canary rollout — with Kubernetes manifests, an optional Helm chart, and rollback runbooks.*

## Problem this project solves

Shipping a new version to 100% of users at once is how outages happen. This
project demonstrates two safer release strategies: **blue-green** (run both
versions, flip traffic atomically, roll back instantly) and **canary** (expose a
small percentage first, grow it while metrics stay healthy). It makes the
trade-offs concrete with a version-reporting app so shifts are observable.

## Technologies & concepts

- **Java 21** — `app-v1` / `app-v2`, each exposing `/version` so traffic is visible
- **Kubernetes** — Deployments, Services, Ingress (selector-based traffic control)
- **Blue-green** — Service selector flip; **canary** — replica ratio *and* ingress weight
- **Helm** — optional chart making the switch a one-line values change
- **Rollback**, RED-method metrics, schema-compatibility risks

## Architecture overview

```
BLUE-GREEN                              CANARY
users → Service(track=blue) → v1        users → Service/Ingress
        ── flip selector ──▶                 ├─ 90% stable (v1)
users → Service(track=green)→ v2              └─ 10% canary (v2) → grow → promote
   rollback = re-select blue             rollback = canary → 0%
```

## Project structure

```text
app-v1/  app-v2/                two versions (report /version) + Dockerfiles
k8s/blue-green/
  blue-deployment.yaml green-deployment.yaml service.yaml switch-service-to-green.yaml
k8s/canary/
  stable-deployment.yaml canary-deployment.yaml service.yaml ingress-canary-example.yaml
helm/rollout/                   optional chart: switch via activeTrack value
monitoring/rollout-alerts.example.yml
docs/blue-green.md  docs/canary.md  docs/rollback.md
README.md  TESTING.md
```

## Important files explained

- **k8s/blue-green/service.yaml** vs **switch-service-to-green.yaml** — the same Service with the `track` selector flipped; applying the second is the atomic switch (re-applying the first is instant rollback).
- **k8s/canary/{stable,canary}-deployment.yaml** — 9:1 replicas ≈ 10% traffic; **service.yaml** selects both tracks (replica-ratio); **ingress-canary-example.yaml** sets an exact NGINX `canary-weight`.
- **helm/rollout/** — renders both tracks; the stable Service follows `activeTrack`, so promotion/rollback is a `helm upgrade --set`.
- **docs/rollback.md** — commands + risks (schema compatibility, sessions, capacity) + pre-promotion safety checks.

## How it would work in a real environment

Build `my-java-app:v1`/`:v2`. Blue-green: deploy green alongside blue, validate
it privately, apply `switch-service-to-green.yaml` to move all traffic, roll back
by re-applying `service.yaml`. Canary: run stable + a small canary, watch canary
vs stable error rate/latency, raise the percentage step by step, then promote —
or drop the canary to 0% to roll back.

## What was prepared but NOT executed

Prepared: both app versions, all blue-green and canary manifests, the Helm chart,
alerts, and three docs. **Not executed:** no image built, no `kubectl`/`helm`, no
cluster, no traffic switched or shifted. **Traffic switching was not tested.**

## Security notes

- **No real secrets/credentials**; all images (`my-java-app:v1/v2`) and hosts (`*.example.invalid`) are placeholders.
- Deployments run **non-root**, read-only rootfs, dropped capabilities.
- `MAX`-style safety: canary starts tiny; prod-style promotion is gated on metrics, not a timer.

## Limitations

- No cluster; no scaling, switching, or metric observation happened.
- Replica-ratio canary granularity is limited by pod counts; ingress-weight needs a compatible controller.
- Java compilation not run (no JDK on the authoring machine).

## Future improvements

- Automate progressive delivery with **Argo Rollouts** / **Flagger** (analysis + auto-rollback).
- Add real request-mirroring and session-affinity handling.
- Wire canary analysis to the Prometheus alerts for automated promotion/abort.

## What I learned

- The trade-offs: blue-green (instant, all-or-nothing, ~2× capacity) vs canary (gradual, smaller blast radius, more moving parts).
- Two ways to split traffic — **replica ratio** vs **ingress weight**.
- Why keeping the old version running makes **rollback instant**.
- The real risk in both: **database/schema compatibility** while two versions run.
