# Test Results — GitOps Deployment Platform

Validation performed on **2026-07-10** on Windows 11 with Docker Desktop
(engine 29.4.2), kubectl, and Helm. No JDK was installed on the host, so
compilation ran inside the official `eclipse-temurin:21-jdk` container.
Everything below was actually run; Argo CD sync was not.

## Java compile result — PASS

```
javac -Xlint:all -Werror --add-modules jdk.httpserver -d out app/src/gitopsdeploymentplatform/*.java
```

Compiled cleanly with all lints enabled and warnings promoted to errors.

## Java endpoint results — PASS

Tested against the built image (`docker run -p 18080:8080` with
`APP_ENVIRONMENT=dev APP_VERSION=0.1.0`):

| Request | Result |
| --- | --- |
| `GET /health` | 200 `{"status":"UP"}` |
| `GET /ready` | 200 `{"status":"READY"}` |
| `GET /config` | 200 `{"environment":"dev","version":"0.1.0"}` |
| `GET /` | 404 JSON |
| `GET /unknown` | 404 JSON |
| `GET /health/test`, `/ready/test`, `/config/test` | 404 JSON (exact routes) |
| `POST /health` | 405 |

Container verified running as non-root `uid=10001(appuser)`.

## Docker build result — PASS

```
docker build -f docker/Dockerfile -t gitops-java-app:0.1.0 .
```

Multi-stage build succeeded with the project root as context and the root
`.dockerignore` in effect.

## kubectl kustomize dev result — PASS

`kubectl kustomize k8s/overlays/dev` rendered: Namespace `java-app-dev`, plus
ConfigMap/Service/Deployment in that namespace, 1 replica,
`image: gitops-java-app:0.1.0`, `environment: dev` labels (including
selectors), `APP_ENVIRONMENT: dev`.

## kubectl kustomize prod result — PASS

`kubectl kustomize k8s/overlays/prod` rendered: Namespace
`java-app-prod-design-only`, plus ConfigMap/Service/Deployment in that
namespace, 3 replicas, `image: gitops-java-app:0.1.0`,
`environment: prod-design-only` labels, `APP_ENVIRONMENT: prod-design-only`.

## Helm lint result — PASS

`helm lint helm/java-app`: `1 chart(s) linted, 0 chart(s) failed` (one INFO
note that a chart icon is recommended).

## Helm template result — PASS

- `helm template java-app helm/java-app` — renders with
  `image: "gitops-java-app:0.1.0"`, 1 replica, `APP_ENVIRONMENT: "learning"`.
- `-f helm/java-app/values-dev.yaml` — 1 replica, `APP_ENVIRONMENT: "dev"`.
- `-f helm/java-app/values-prod.yaml` — 3 replicas,
  `APP_ENVIRONMENT: "prod-design-only"`.

## Argo CD manifest review result — PASS (review only)

The two Application examples and the AppProject example were reviewed for
consistency: project name matches (`gitops-platform`), destinations match the
overlay namespaces, paths point at the overlay folders that were verified to
render above, dev has `automated: {prune: false, selfHeal: true}`, prod has
no `automated:` block (manual gate), and `CreateNamespace=false` is honest
because the namespaces are declared in Git.

## Argo CD sync result — NOT RUN

No Argo CD instance was connected. No sync, drift detection, self-heal, or
rollback was executed. The repo URL in the examples is a placeholder.

## Tools unavailable

- JDK on the host — compile ran in the `eclipse-temurin:21-jdk` container.
- Argo CD — not installed; examples are review-only.

## Known limitations

- The prod overlay is design-only; nothing was deployed to any cluster.
- The image exists only locally; nothing was pushed to a registry.
- Tag-based image references are traceable, not immutable; digest promotion
  is documented as the production approach but not implemented.
- Results are a point-in-time snapshot of one validation run.
