# Testing — Kubernetes Deployment Java App

Exact commands to validate this lab. Results actually observed with these
commands are recorded honestly in [TEST_RESULTS.md](TEST_RESULTS.md).
Commands use POSIX shell syntax; on Windows use Git Bash (or `curl.exe` from
PowerShell). Run everything from this project folder.

## A) Java-only validation

Requires JDK 21 (or run inside an `eclipse-temurin:21-jdk` container):

```bash
javac --add-modules jdk.httpserver -d out app/src/kubernetesdeploymentjavaapp/*.java

APP_ENVIRONMENT=dev APP_VERSION=0.1.0 APP_PORT=8080 \
APP_DEMO_TOKEN=local-demo-token \
  java --add-modules jdk.httpserver -cp out kubernetesdeploymentjavaapp.Main
```

In another terminal:

```bash
curl -i http://localhost:8080/health
curl -i http://localhost:8080/ready
curl -i http://localhost:8080/config
curl -i http://localhost:8080/unknown
curl -i http://localhost:8080/health/test
curl -i -X POST http://localhost:8080/health
```

Expected:

- `/health` → 200 `{"status":"UP"}`; `/ready` → 200 `{"status":"READY"}`
- `/config` → 200 with environment, version, message, and
  `"secretConfigured":true` — **only the flag; the token value is never
  returned**. Start without `APP_DEMO_TOKEN` and it reports `false`.
- `/unknown`, `/health/test`, `/ready/test`, `/config/test` → 404 (exact routes)
- `POST /health` / `/ready` / `/config` → 405 with `Allow: GET`

## B) Docker build

The Dockerfile lives under `app/` and the build context is the `app/` folder:

```bash
docker build -f app/Dockerfile -t kubernetes-java-app:0.1.0 app
```

## C) Kubernetes render validation (no cluster changes)

```bash
kubectl kustomize k8s/                  # renders ConfigMap, Deployment, Service
kubectl apply --dry-run=client -k k8s/  # validates; creates nothing
```

Note: the example Secret and the optional Ingress are deliberately not part
of the kustomization.

## D) Optional local cluster workflow (kind)

```bash
kind create cluster --name k8s-java-app
kind load docker-image kubernetes-java-app:0.1.0 --name k8s-java-app
kubectl apply -k k8s/
kubectl get pods                        # expect 2/2 Running
kubectl get svc
kubectl port-forward svc/java-app 8080:80
curl http://localhost:8080/health
curl http://localhost:8080/config       # "secretConfigured": false

# cleanup
kubectl delete -k k8s/
kind delete cluster --name k8s-java-app
```

## E) Optional Secret example

The example Secret is **not applied by default** — it lives in `examples/`,
outside the kustomization. To try it:

```bash
kubectl apply -f examples/secret.example.yaml
kubectl rollout restart deployment/java-app   # pods re-read env on start
kubectl rollout status deployment/java-app
curl http://localhost:8080/config             # "secretConfigured": true
```

(A restart is needed because environment variables are injected at container
start. Note the port-forward pins to a single pod — restart it after a
rollout.) The Deployment marks the secret reference `optional: true`, so pods
run fine without it. Never put real values in the example file.

## F) Cleanup

```bash
rm -rf out
docker rmi kubernetes-java-app:0.1.0   # optional
```
