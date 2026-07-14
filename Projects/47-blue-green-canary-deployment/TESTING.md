# Testing — Blue-Green & Canary Deployment

This file gives commands to validate the project. Java can be tested locally with
only a JDK. Kubernetes, Helm, Docker, and Prometheus checks are optional and need
those tools installed.

## 1. Java local validation

### Compile v1

```bash
javac -d out-v1 app-v1/src/app/*.java
```

### Run v1

```bash
APP_VERSION=v1 APP_PORT=8081 java -cp out-v1 app.Main
```

In another terminal:

```bash
curl http://localhost:8081/version
curl http://localhost:8081/health
curl http://localhost:8081/ready
curl http://localhost:8081/
```

### Compile v2

```bash
javac -d out-v2 app-v2/src/app/*.java
```

### Run v2

```bash
APP_VERSION=v2 APP_PORT=8082 java -cp out-v2 app.Main
```

In another terminal:

```bash
curl http://localhost:8082/version
curl http://localhost:8082/health
curl http://localhost:8082/ready
curl http://localhost:8082/
```

### Cleanup

```bash
rm -rf out-v1 out-v2
```

## 2. Docker build checks

Docker must be installed and running.

```bash
docker build -t registry.example.invalid/blue-green-canary-app:v1 app-v1
docker build -t registry.example.invalid/blue-green-canary-app:v2 app-v2
```

These tags are placeholders. Do not push them unless you replace the registry
with your own registry.

## 3. Kubernetes manifest review

A cluster is required for real deployment. These commands are examples:

```bash
# Initial blue-green state, without accidentally applying the promotion switch
kubectl apply -f k8s/blue-green/blue-deployment.yaml
kubectl apply -f k8s/blue-green/green-deployment.yaml
kubectl apply -f k8s/blue-green/service.yaml

# Promote green
kubectl apply -f k8s/blue-green-switch/switch-service-to-green.yaml

# Roll back to blue
kubectl apply -f k8s/blue-green/service.yaml
```

Canary example:

```bash
kubectl apply -f k8s/canary/stable-deployment.yaml
kubectl apply -f k8s/canary/canary-deployment.yaml
kubectl apply -f k8s/canary/service.yaml

# Raise or roll back replica-ratio canary
kubectl scale deployment java-app-canary --replicas=3
kubectl scale deployment java-app-canary --replicas=0
```

## 4. Helm render check

Helm must be installed.

```bash
helm template demo helm/rollout
helm template demo helm/rollout --set activeTrack=green
```

## 5. Expected behavior

- v1 `/version` returns `{"version":"v1"}`.
- v2 `/version` returns `{"version":"v2"}`.
- Blue-green starts with Service selector `track: blue`.
- Promotion switch changes the same Service selector to `track: green`.
- Canary replica ratio starts around 9:1 stable/canary.
- Rollback is redirecting traffic, not rebuilding the old version.

## 6. GitHub safety checklist

- No real secrets.
- No generated class files or `out-*` folders committed.
- No old duplicate `src/` or `docker/` folders.
- No visible unfinished placeholder markers.
- No command output invented; only add real results to `TEST_RESULTS.md`.
