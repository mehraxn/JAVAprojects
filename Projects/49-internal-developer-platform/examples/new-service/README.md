# payments-api

A Java HTTP service generated from the Internal Developer Platform golden path.
Everything needed to build, run, containerize, and deploy the service is included
and consistent: source, Dockerfile, Helm chart, Argo CD Applications, and catalog
metadata.

| Setting | Value |
| --- | --- |
| Service name | `payments-api` |
| Owner | `payments-team` |
| Port | `8080` |
| Image | `registry.example.invalid/payments-api` |

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
| GET | `/` | `{"service":"payments-api","message":"hello from payments-api"}` |
| GET | `/health` | `{"status":"ok","service":"payments-api"}` |
| GET | `/ready` | `{"status":"ready","service":"payments-api"}` |

## Run it locally

```bash
# Compile
javac -d out src/app/*.java

# Run (reads SERVICE_NAME and SERVICE_PORT from the environment)
SERVICE_NAME=payments-api SERVICE_PORT=8080 java -cp out app.Main

# Test the endpoints
curl http://localhost:8080/
curl http://localhost:8080/health
curl http://localhost:8080/ready
```

## Optional: Helm and Docker

```bash
# Render the Kubernetes manifests (no cluster needed)
helm template payments-api helm

# Build the image (requires a running Docker daemon)
docker build -t registry.example.invalid/payments-api:0.1.0 .
```

## Deployment

The `gitops/` folder holds one Argo CD `Application` per environment. In a real
setup these live in a config repo that Argo CD watches: dev auto-syncs for fast
feedback, prod syncs only when a human promotes it. Nothing here is deployed
automatically — these manifests are the desired state, not a live cluster.
