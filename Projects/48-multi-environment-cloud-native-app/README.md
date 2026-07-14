# Multi-Environment Cloud-Native Java App

This project demonstrates one small Java HTTP service built into one container
image and promoted through development, staging, and production by changing
GitOps desired state. The core rule is:

> One Java app → one container image → the same immutable digest promoted through
> dev, staging, and prod.

Environment-specific configuration and scale change; application code and the
promoted image do not.

## Architecture

```text
app/src/Main.java
       |
       v
Dockerfile / CI build
       |
       v
registry.example.invalid/cloud-native-app@sha256:<CI digest>
       |
       +--> dev overlay      app-dev       auto-sync
       +--> staging overlay  app-staging   auto-sync after checks
       +--> prod overlay     app-prod      manual approval and sync
                    |
                    v
              Argo CD + Kustomize
```

The repository URL, registry, image digests, Secret values, and cluster are
example placeholders. No real production deployment is represented.

## Application endpoints

The framework-free Java service under
`app/src/multienvironmentcloudnativeapp/Main.java` exposes:

| Endpoint | Purpose |
| --- | --- |
| `/health` | liveness response |
| `/ready` | readiness response |
| `/config` | non-secret effective environment, log level, and feature flag |
| `/` | service greeting and active environment |

Compile and run it locally:

```bash
javac -d out app/src/multienvironmentcloudnativeapp/*.java
APP_ENVIRONMENT=dev APP_PORT=8080 java -cp out multienvironmentcloudnativeapp.Main
```

See [TESTING.md](TESTING.md) for endpoint and manifest validation commands.

## Image and promotion model

Every container reference uses this safe placeholder repository:

```text
registry.example.invalid/cloud-native-app
```

Each Kustomize overlay contains an obvious digest placeholder:

```yaml
images:
  - name: registry.example.invalid/cloud-native-app
    digest: sha256:REPLACE_WITH_DEV_DIGEST
```

CI should build the image once, push it, and record the registry-provided
`sha256:<real digest>`. Dev receives that digest first. A staging promotion PR
copies the exact dev digest into the staging overlay; a production promotion PR
copies the verified staging digest into prod. The image is never rebuilt during
promotion.

The committed `REPLACE_WITH_*_DIGEST` values are not real digests and cannot be
deployed successfully until replaced. The Helm values use matching placeholders
for rendering demonstrations.

## Environment differences

| | dev | staging | prod |
| --- | --- | --- | --- |
| Namespace | `app-dev` | `app-staging` | `app-prod` |
| Replicas | 1 | 2 | 4 |
| Log level | DEBUG | INFO | WARN |
| New UI flag | true | true | false |
| Argo CD sync | automatic | automatic | manual |

Kustomize is the GitOps deployment path. The Helm chart is an equivalent
packaging example; a real platform should choose one deployment mechanism rather
than operate both for the same release.

## Argo CD structure

`gitops/appproject.yaml` defines the `cloud-native-app` AppProject used by every
Application. It allows the example repository and the `argocd`, `app-dev`,
`app-staging`, and `app-prod` destinations. Apply it before choosing either:

- `gitops/app-of-apps.yaml`, which discovers the environment Applications; or
- `gitops/applicationset.example.yaml`, which generates them from a list.

All Argo CD files use `example.invalid` and are examples only. They were not
registered with an Argo CD controller by this project.

## Configuration and secrets

Non-secret values come from `app-config`. The Kubernetes Deployment also uses an
optional `app-secret` reference, allowing local rendering and startup when no
Secret exists. Files under `environments/*/secret.example.yaml` contain only
obvious placeholders and are not included in the Kustomize resources.

For Helm, Secret consumption is disabled by default. Set `secret.enabled=true`
only when `app-secret` is provisioned separately. `secret.create=true` renders
placeholder example values and must not be used for real credentials.

Production-style GitOps should use Sealed Secrets or External Secrets backed by
a secret manager. Never commit plaintext credentials.

## Container and pod security

- non-root user `10001`;
- no privilege escalation;
- all Linux capabilities dropped;
- read-only root filesystem;
- writable `/tmp` backed by an `emptyDir` volume;
- liveness/readiness probes and resource requests/limits.

## Project structure

```text
app/src/                 real Java application source
Dockerfile               multi-stage Java 21 image build
ci/                      inert build and promotion workflow examples
k8s/base/                shared Deployment, Service, and ConfigMap
k8s/overlays/            dev, staging, and prod digest/config/scale overlays
helm/app/                equivalent optional Helm packaging
environments/            Argo CD Applications and Secret shape examples
gitops/                  AppProject plus app-of-apps/ApplicationSet examples
docs/                    environment, promotion, rollback, GitOps, secret docs
TESTING.md               exact local validation workflow
TEST_RESULTS.md           blank evidence template for measured outputs
```

## Executable versus example-only

Executable locally:

- Java compilation and HTTP service;
- Docker image build, if Docker is available;
- Kustomize rendering with `kubectl kustomize`;
- Helm rendering with `helm template`.

Example-only until real infrastructure and credentials are supplied:

- pushing to `registry.example.invalid`;
- replacing placeholders with registry-produced digests;
- CI promotion pull requests;
- Argo CD registration, synchronization, and production deployment;
- Secret manager integration.

Do not claim any of those example-only operations succeeded without real
evidence. [TEST_RESULTS.md](TEST_RESULTS.md) intentionally contains blank
placeholders for outputs produced on the machine running the tests.
