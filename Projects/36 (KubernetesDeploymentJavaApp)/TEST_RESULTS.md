# Test Results — Kubernetes Deployment Java App

Validation performed on **2026-07-10** on Windows 11 with Docker Desktop
(engine 29.4.2), kubectl, and kind. No JDK was installed on the host, so
compilation ran in the official `eclipse-temurin:21-jdk` container.
Everything below was actually run, including a real kind deployment.

## Java compile result — PASS

```
javac -Xlint:all -Werror --add-modules jdk.httpserver -d out app/src/kubernetesdeploymentjavaapp/*.java
```

Compiled cleanly (all lints, warnings as errors).

## Java endpoint test results — PASS

Tested against the built image (`docker run -p 18080:8080` with
`APP_ENVIRONMENT=dev APP_VERSION=0.1.0 APP_DEMO_TOKEN=local-demo-token`):

| Request | Result |
| --- | --- |
| `GET /health` | 200 `{"status":"UP"}` |
| `GET /ready` | 200 `{"status":"READY"}` |
| `GET /config` | 200 `{"environment":"dev","version":"0.1.0","message":"Hello from Kubernetes","secretConfigured":true}` |
| `GET /unknown` | 404 |
| `GET /health/test`, `/ready/test`, `/config/test` | 404 (exact routes) |
| `POST /health`, `/ready`, `/config` | 405 |

A second run **without** `APP_DEMO_TOKEN` returned
`"secretConfigured":false`. The token value itself is never emitted — only
the boolean flag.

## Docker build result — PASS

```
docker build -f app/Dockerfile -t kubernetes-java-app:0.1.0 app
```

Multi-stage build succeeded; container runs as non-root uid 10001.

## kubectl kustomize result — PASS

`kubectl kustomize k8s/` rendered exactly the three safe defaults —
ConfigMap, Deployment (2 replicas, `image: kubernetes-java-app:0.1.0`),
Service — with no Secret and no Ingress.

## kubectl dry-run result — PASS

`kubectl apply --dry-run=client -k k8s/` validated all three resources
(`created (dry run)` each); nothing was created.

## Real Kubernetes deployment result — PASS (kind, then deleted)

Full workflow executed on a disposable kind cluster (`k8s-java-app`):

- `kind create cluster` + `kind load docker-image kubernetes-java-app:0.1.0`
- `kubectl apply -k k8s/` → deployment reached available, **2/2 pods
  Running** under the full security context (non-root, read-only rootfs with
  `/tmp` emptyDir, probes passing)
- Through `kubectl port-forward svc/java-app`: `/health` 200, `/ready` 200,
  `/unknown` 404, and `/config` returned the ConfigMap-driven values
- **Optional Secret verified end-to-end**: with
  `examples/secret.example.yaml` applied, `/config` returned
  `"secretConfigured":true`; without it, `false`. (First attempt's
  port-forward died during a `rollout restart` — expected, it pins to a pod —
  so the flow was re-run cleanly with the Secret applied before the
  Deployment.)
- Cleanup: `kubectl delete -k k8s/`, secret deleted,
  `kind delete cluster --name k8s-java-app`

## Tools unavailable

- JDK on the host — compile ran in the Temurin container instead.

## Known limitations

- Local/demo app only; no real database or business logic.
- No production Ingress/TLS — `k8s/ingress.yaml` is an optional example
  needing a controller, and is not applied by default.
- No real secret manager — the example Secret is a lab-only stand-in and is
  **not applied by default** (it lives in `examples/`, outside the
  kustomization).
- No cloud deployment; the image exists only locally and was loaded into
  kind, never pushed.
- Results are a point-in-time snapshot of one validation run.
