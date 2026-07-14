# Testing — GitOps Deployment Platform

Exact commands to validate this project locally. Results actually observed
with these commands are recorded honestly in [TEST_RESULTS.md](TEST_RESULTS.md).
Commands use POSIX shell syntax; on Windows use Git Bash (or `curl.exe` from
PowerShell). Run everything from this project folder.

## A) Java-only validation

Requires JDK 21 (or run inside an `eclipse-temurin:21-jdk` container):

```bash
javac --add-modules jdk.httpserver -d out app/src/gitopsdeploymentplatform/*.java

APP_ENVIRONMENT=dev APP_VERSION=0.1.0 APP_PORT=8080 \
  java --add-modules jdk.httpserver -cp out gitopsdeploymentplatform.Main
```

In another terminal:

```bash
curl -i http://localhost:8080/health
curl -i http://localhost:8080/ready
curl -i http://localhost:8080/config
curl -i http://localhost:8080/
curl -i http://localhost:8080/unknown
curl -i http://localhost:8080/health/test
curl -i -X POST http://localhost:8080/health
```

Expected:

- `/health` → 200 `{"status":"UP"}`
- `/ready` → 200 `{"status":"READY"}`
- `/config` → 200 `{"environment":"dev","version":"0.1.0"}`
- `/`, `/unknown`, `/health/test` (and `/ready/test`, `/config/test`) →
  404 JSON — routes are exact-matched
- `POST /health` → 405 with `Allow: GET`

## B) Docker build

The build context is the **project root** (the Dockerfile copies `app/src`):

```bash
docker build -f docker/Dockerfile -t gitops-java-app:0.1.0 .

# optional smoke test
docker run --rm -d --name gitops-smoke -p 8080:8080 \
  -e APP_ENVIRONMENT=dev -e APP_VERSION=0.1.0 gitops-java-app:0.1.0
curl -i http://localhost:8080/config
docker stop gitops-smoke
```

## C) Kustomize validation

```bash
kubectl kustomize k8s/overlays/dev
kubectl kustomize k8s/overlays/prod
```

Expected: each renders a Namespace (`java-app-dev` /
`java-app-prod-design-only`) plus ConfigMap, Service, and Deployment in that
namespace; dev has 1 replica, prod has 3; both use
`image: gitops-java-app:0.1.0`.

With a reachable cluster context you can additionally run
`kubectl apply --dry-run=client -k k8s/overlays/dev` (creates nothing; needs
a cluster only to download validation schemas).

## D) Helm validation

```bash
helm lint helm/java-app
helm template java-app helm/java-app
helm template java-app helm/java-app -f helm/java-app/values-dev.yaml
helm template java-app helm/java-app -f helm/java-app/values-prod.yaml
```

Expected: lint passes; templates render the same workload shape as the
Kustomize output, with replicas/config varying by values file.

## E) Argo CD manifest review

Argo CD sync is **not run by default** — the examples in `gitops/argocd/`
point at a placeholder repo (`example.invalid`) and are meant for review, not
application. To try them for real you need your own Argo CD instance: push
this project to a repo you own, update `repoURL` and `path` in the
Application manifests, apply `appproject.example.yaml`, then the two
Applications. Only record sync results in TEST_RESULTS.md if you actually do
this.

Structural review without a cluster:

```bash
kubectl kustomize k8s/overlays/dev >/dev/null && echo "dev path renders"
kubectl kustomize k8s/overlays/prod >/dev/null && echo "prod path renders"
```

## F) Cleanup

```bash
rm -rf out
docker rmi gitops-java-app:0.1.0   # optional
```
