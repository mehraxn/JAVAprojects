# Kubernetes Deployment Java App

*A Kubernetes deployment lab for a Java HTTP app — Dockerized non-root
container, Deployment/Service/ConfigMap, optional Secret handling that never
exposes values, probes, resource limits, read-only root filesystem, and a
Kustomize workflow — deployed and verified on a real local kind cluster.*

## What this project is

A beginner-friendly but complete Kubernetes deployment lab: a dependency-free
Java 21 HTTP app packaged into a hardened container and deployed with plain,
readable manifests. Everything claimed here was actually run — see
[TEST_RESULTS.md](TEST_RESULTS.md).

## What it demonstrates

- **Dockerized Java app** — multi-stage build, JDK never ships, runs as uid 10001
- **Deployment** (2 replicas) + **ClusterIP Service** + **ConfigMap-driven config**
- **Optional Secret** — the Deployment references `APP_DEMO_TOKEN` with
  `optional: true`, so pods start with or without it; the app reports **only**
  `"secretConfigured": true/false` and never prints the value
- **Readiness/liveness probes** on `/ready` and `/health`
- **Resource requests/limits** and a hardened security context (non-root,
  read-only root filesystem with a writable `/tmp` emptyDir, dropped
  capabilities, seccomp)
- **Kustomize workflow**: `kubectl apply -k k8s/`

## App endpoints (exact-matched)

| Endpoint | Response |
| --- | --- |
| `GET /health` | 200 `{"status":"UP"}` |
| `GET /ready` | 200 `{"status":"READY"}` |
| `GET /config` | 200 environment/version/message + `secretConfigured` flag |
| anything else (`/unknown`, `/health/test`, …) | 404 JSON |
| non-GET on known routes | 405 |

## Project structure

```text
app/src/kubernetesdeploymentjavaapp/   Java app (Main + AppConfig)
app/Dockerfile                         multi-stage non-root image
k8s/kustomization.yaml                 safe defaults: kubectl apply -k k8s/
k8s/deployment.yaml                    probes, resources, security, optional secret
k8s/service.yaml                       ClusterIP (port 80 → 8080)
k8s/configmap.yaml                     APP_PORT / APP_ENVIRONMENT / APP_VERSION / APP_MESSAGE
k8s/ingress.yaml                       optional — NOT applied by default
examples/secret.example.yaml           example Secret — NOT applied by default
docs/kubernetes-explanation.md         concept walkthrough
README.md  TESTING.md  TEST_RESULTS.md
```

The example Secret lives under `examples/` (not `k8s/`) precisely so that
`kubectl apply -k k8s/` can never apply it by accident.

## Quick start (kind)

```bash
docker build -f app/Dockerfile -t kubernetes-java-app:0.1.0 app

kind create cluster --name k8s-java-app
kind load docker-image kubernetes-java-app:0.1.0 --name k8s-java-app
kubectl apply -k k8s/
kubectl get pods
kubectl port-forward svc/java-app 8080:80
curl http://localhost:8080/health
curl http://localhost:8080/config     # "secretConfigured": false

# optional: add the example secret, then restart pods to pick it up
kubectl apply -f examples/secret.example.yaml
kubectl rollout restart deployment/java-app
curl http://localhost:8080/config     # "secretConfigured": true

# cleanup
kubectl delete -k k8s/
kind delete cluster --name k8s-java-app
```

The image is a versioned local tag (`kubernetes-java-app:0.1.0`, never
`latest`) loaded straight into kind — no registry involved.

## What is implemented (and verified)

All of it was run on 2026-07-10 ([TEST_RESULTS.md](TEST_RESULTS.md)): compile,
every endpoint behavior (including 404/405 and the secret-presence flag),
Docker build, kustomize render + client dry-run, and the full kind workflow —
2/2 pods Running under the hardened context, endpoints answered through the
Service, and `secretConfigured` flipped to `true` after applying the example
Secret. The cluster was deleted afterward.

## What is not production-grade

- No real database or state; the app is a demo.
- Ingress is an optional example (needs a controller); no TLS.
- No real secret manager — the example Secret is a lab-only stand-in, and
  production should use External Secrets/sealed-secrets/a vault.
- No cloud deployment; the image exists only locally.

## How to validate

Exact commands with expected output: [TESTING.md](TESTING.md). Honest recorded
results: [TEST_RESULTS.md](TEST_RESULTS.md).
