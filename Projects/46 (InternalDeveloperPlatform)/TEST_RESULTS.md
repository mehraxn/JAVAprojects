# Test Results

Real output from running the tests in [TESTING.md](TESTING.md). Sections that
required a tool not available in the authoring environment are marked so you can
fill them in from your own run. **Nothing here is invented.**

Environment used for the recorded runs:

- Shell: GNU bash 5.3 (Cygwin) on Windows
- Helm: v4.2.2
- JDK: **not installed** (Java compile/run steps not executed here)
- Docker: CLI present, **daemon not running** (image build not executed here)

---

## 1. Generator command

```
$ ./scripts/new-service.sh \
    --name payments-api --owner payments-team --port 8080 \
    --image registry.example.invalid/payments-api \
    --out examples/new-service --force

Generated service: payments-api
Owner: payments-team
Port: 8080
Image: registry.example.invalid/payments-api
Output: examples/new-service
```

## 2. Generated file tree

```
examples/new-service/Dockerfile
examples/new-service/README.md
examples/new-service/catalog-info.yaml
examples/new-service/gitops/app-dev.yaml
examples/new-service/gitops/app-prod.yaml
examples/new-service/helm/.helmignore
examples/new-service/helm/Chart.yaml
examples/new-service/helm/templates/_helpers.tpl
examples/new-service/helm/templates/configmap.yaml
examples/new-service/helm/templates/deployment.yaml
examples/new-service/helm/templates/service.yaml
examples/new-service/helm/values-dev.yaml
examples/new-service/helm/values-prod.yaml
examples/new-service/helm/values.yaml
examples/new-service/src/app/Main.java
```

## 3. Placeholder check

```
$ grep -R "__SERVICE_" examples/new-service || echo "(no matches)"
(no matches)
```

Empty-value checks (`SERVICE_NAME=$`, `SERVICE_PORT=$`, `EXPOSE $`, empty
`name:` / `owner:`) also returned no matches.

## 4. Java compile result

Tool unavailable: no JDK on the authoring machine. Run locally and paste output:

```
$ javac -d /tmp/payments-api-out /tmp/payments-api/src/app/*.java
(paste real output here after running the command)
```

## 5. App run result

Tool unavailable (no JDK). Run locally and paste output:

```
$ SERVICE_NAME=payments-api SERVICE_PORT=8080 java -cp /tmp/payments-api-out app.Main
(paste real output here — expected: "payments-api listening on port 8080")
```

## 6. `/` endpoint output

```
$ curl http://localhost:8080/
(paste real output here — expected: {"service":"payments-api","message":"hello from payments-api"})
```

## 7. `/health` output

```
$ curl http://localhost:8080/health
(paste real output here — expected: {"status":"ok","service":"payments-api"})
```

## 8. `/ready` output

```
$ curl http://localhost:8080/ready
(paste real output here — expected: {"status":"ready","service":"payments-api"})
```

## 9. Helm template result

Run with Helm v4.2.2. `helm lint` passed and `helm template` rendered valid
manifests. Rendered ConfigMap (dev values):

```
$ helm lint examples/new-service/helm
1 chart(s) linted, 0 chart(s) failed

$ helm template payments-api examples/new-service/helm \
    -f examples/new-service/helm/values.yaml \
    -f examples/new-service/helm/values-dev.yaml
# Source: payments-api/templates/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: payments-api
data:
  SERVICE_NAME: "payments-api"
  SERVICE_PORT: "8080"
  APP_ENV: "dev"
  LOG_LEVEL: "DEBUG"
# ... Service and Deployment also rendered (probes /health & /ready,
#     image registry.example.invalid/payments-api:0.1.0, non-root, tmp volume) ...
```

## 10. Docker build result

Tool unavailable: Docker daemon not running in the authoring environment. Run
locally and paste output:

```
$ docker build -t registry.example.invalid/payments-api:0.1.0 /tmp/payments-api
(paste real output here after running the command)
```

## Known limitations

- No JDK or running Docker daemon was available here, so Java compile/run and the
  image build were not executed. The commands are correct; paste your own output.
- Helm renders manifests locally only — no Kubernetes cluster or Argo CD was
  contacted, and nothing was deployed or synced.
- All registries and repo URLs are `example.invalid` placeholders.
