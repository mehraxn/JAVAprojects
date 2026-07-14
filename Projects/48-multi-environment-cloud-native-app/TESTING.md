# Local validation workflow

Run commands from the Project 48 directory. Record real output in
`TEST_RESULTS.md`; do not replace placeholders with expected or invented output.

## 1. Compile the Java app

Requires JDK 21 or another JDK that provides the `jdk.httpserver` module.

```bash
javac -d out app/src/multienvironmentcloudnativeapp/*.java
```

## 2. Run the Java app

```bash
APP_ENVIRONMENT=dev APP_PORT=8080 java -cp out multienvironmentcloudnativeapp.Main
```

Keep that terminal running. In a second terminal, test every endpoint:

```bash
curl http://localhost:8080/health
curl http://localhost:8080/ready
curl http://localhost:8080/config
curl http://localhost:8080/
```

Stop the server with `Ctrl+C`, then remove compiled output:

```bash
rm -rf out
```

PowerShell equivalents for starting and cleaning up are:

```powershell
$env:APP_ENVIRONMENT = "dev"
$env:APP_PORT = "8080"
java -cp out multienvironmentcloudnativeapp.Main
Remove-Item Env:APP_ENVIRONMENT, Env:APP_PORT
Remove-Item -Recurse -Force out
```

## 3. Render Kustomize overlays

Optional; requires `kubectl` with built-in Kustomize support. Rendering does not
deploy anything.

```bash
kubectl kustomize k8s/overlays/dev
kubectl kustomize k8s/overlays/staging
kubectl kustomize k8s/overlays/prod
```

Check that renders contain namespaces `app-dev`, `app-staging`, and `app-prod`;
replica counts 1, 2, and 4; optional `app-secret`; writable `/tmp`; and the
matching `REPLACE_WITH_*_DIGEST` placeholder.

## 4. Render the Helm chart

Optional; requires Helm. These commands render locally and do not install a
release:

```bash
helm template cloud-native-app helm/app
helm template cloud-native-app helm/app -f helm/app/values-dev.yaml
helm template cloud-native-app helm/app -f helm/app/values-staging.yaml
helm template cloud-native-app helm/app -f helm/app/values-prod.yaml
```

To demonstrate optional Secret wiring with placeholder values only:

```bash
helm template cloud-native-app helm/app \
  --set secret.enabled=true \
  --set secret.create=true
```

Never pass a real secret through `--set` or commit one to a values file.

## 5. Optional Docker build

Requires a running Docker daemon. This builds locally; it does not push to the
placeholder registry:

```bash
docker build -t registry.example.invalid/cloud-native-app:local .
docker run --rm -p 8080:8080 \
  -e APP_ENVIRONMENT=dev \
  registry.example.invalid/cloud-native-app:local
```

Use the endpoint commands from step 2 in a second terminal.

## 6. GitOps static review

Verify before using a real Argo CD installation:

- `gitops/appproject.yaml` is applied before Applications;
- the placeholder repository URL is replaced with the real public repository;
- each overlay digest placeholder is replaced with a real CI-produced digest;
- dev/staging use automatic sync and prod remains manual;
- real Secrets are provided by an approved external mechanism.

These repository files do not prove that Argo CD synchronized a cluster.
