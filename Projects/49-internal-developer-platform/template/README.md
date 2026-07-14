# __SERVICE_NAME__

A Java HTTP service generated from the Internal Developer Platform golden path.
Everything needed to build, run, containerize, and deploy the service is included
and consistent: source, Dockerfile, Helm chart, Argo CD Applications, and catalog
metadata.

| Setting | Value |
| --- | --- |
| Service name | `__SERVICE_NAME__` |
| Owner | `__SERVICE_OWNER__` |
| Port | `__SERVICE_PORT__` |
| Image | `__SERVICE_IMAGE__` |

## Layout

```text
README.md              this file
catalog-info.yaml      catalog metadata (owner, lifecycle, image repo)
Dockerfile             multi-stage, non-root image
src/app/Main.java      HTTP app with /, /health, /ready
helm/                  Helm chart (Chart.yaml, values*, templates/)
gitops/                Argo CD Applications (app-dev.yaml, app-prod.yaml)
```

## Endpoints

| Method | Path | Response |
| --- | --- | --- |
| GET | `/` | `{"service":"__SERVICE_NAME__","message":"hello from __SERVICE_NAME__"}` |
| GET | `/health` | `{"status":"ok","service":"__SERVICE_NAME__"}` |
| GET | `/ready` | `{"status":"ready","service":"__SERVICE_NAME__"}` |

## Run it locally

```bash
# Compile
javac -d out src/app/*.java

# Run (reads SERVICE_NAME and SERVICE_PORT from the environment)
SERVICE_NAME=__SERVICE_NAME__ SERVICE_PORT=__SERVICE_PORT__ java -cp out app.Main

# Test the endpoints
curl http://localhost:__SERVICE_PORT__/
curl http://localhost:__SERVICE_PORT__/health
curl http://localhost:__SERVICE_PORT__/ready
```

## Optional: Helm and Docker

```bash
# Render the Kubernetes manifests (no cluster needed)
helm template __SERVICE_NAME__ helm

# Build the image (requires a running Docker daemon)
docker build -t __SERVICE_IMAGE__:0.1.0 .
```

## Deployment

The `gitops/` folder holds one Argo CD `Application` per environment. In a real
setup these live in a config repo that Argo CD watches: dev auto-syncs for fast
feedback, prod syncs only when a human promotes it. Nothing here is deployed
automatically — these manifests are the desired state, not a live cluster.
