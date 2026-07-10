# Testing — Helm Chart Java App

Exact commands to validate this chart. Results actually observed with these
commands are recorded honestly in [TEST_RESULTS.md](TEST_RESULTS.md).
Commands use POSIX shell syntax; on Windows use Git Bash or PowerShell
equivalents. Run everything from this project folder.

## A) Helm lint

```bash
helm lint helm/java-app
```

Expected: `0 chart(s) failed` (an INFO about a missing icon is cosmetic).

## B) Helm template — defaults

```bash
helm template java-app helm/java-app
```

Expected: ServiceAccount, ConfigMap, Service, and Deployment render (no
Secret, no Ingress). The Deployment carries a `checksum/config` annotation,
`serviceAccountName`, `automountServiceAccountToken: false`, and
`image: "helm-java-app:0.1.0"`.

## C) Helm template — example values

```bash
helm template java-app helm/java-app -f examples/values-dev.yaml
helm template java-app helm/java-app -f examples/values-prod.yaml
helm template java-app helm/java-app -f examples/values-ingress.yaml
helm template java-app helm/java-app -f examples/values-external-secret.yaml
```

Expected:

- dev → 1 replica, `APP_ENVIRONMENT: "dev"`, lighter resources
- prod → 3 replicas, `APP_ENVIRONMENT: "prod"`, stronger resources
- ingress → an Ingress with `ingressClassName: nginx`, the annotation, host
  `java-app.local`, `pathType: Prefix`
- external-secret → **no** Secret rendered; the container gets
  `APP_DEMO_TOKEN` via `secretKeyRef` to `java-app-existing-secret`; **no**
  `checksum/secret` annotation (Helm cannot hash an external Secret)

Chart-created Secret variant (renders the Secret + `checksum/secret`):

```bash
helm template java-app helm/java-app \
  --set secret.create=true --set secret.demoToken=demo-only
```

## D) Schema validation — negative test

```bash
cat > /tmp/invalid-values.yaml <<'EOF'
replicaCount: "two"
containerPort: "abc"
service:
  type: WrongType
EOF

helm template java-app helm/java-app -f /tmp/invalid-values.yaml
```

Expected: Helm **fails** with schema errors for all three values
(`got string, want integer` ×2, and the service.type enum violation).

## E) Helm package

```bash
helm package helm/java-app --destination /tmp
```

Expected: `java-app-0.1.0.tgz` is produced (packaged artifacts are
gitignored — don't commit them).

## F) kubectl validation (optional, needs any reachable cluster context)

```bash
helm template java-app helm/java-app | kubectl apply --dry-run=client -f -
```

Expected: all four resources report `created (dry run)`; nothing is created.

## G) Install / upgrade / rollback (optional, disposable cluster only)

```bash
helm install java-app helm/java-app
helm upgrade java-app helm/java-app -f examples/values-prod.yaml
helm rollback java-app 1
helm uninstall java-app
```

Only run against a disposable local cluster (kind/minikube). Record the
outcome in TEST_RESULTS.md only if you actually run it. Note the default
image is a local placeholder — pods will only pull it if you build/load an
image with that tag yourself.

## H) Cleanup

```bash
rm -f /tmp/invalid-values.yaml
rm -f /tmp/java-app-*.tgz
```
