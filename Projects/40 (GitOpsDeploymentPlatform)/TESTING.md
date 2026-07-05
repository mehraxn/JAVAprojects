# Testing — GitOps Deployment Platform

> **Nothing was executed.** No Java, Docker, Kubernetes, Kustomize, Helm, or
> Argo CD command ran; nothing was built, rendered, synced, deployed, or rolled
> back. This documents the static review and the checks a disposable environment
> *would* use.

## 1. Static validation checklist

- [ ] Java package paths correct; `/health`, `/ready`, `/config` exist in source.
- [ ] Dockerfile build context is the project root; source path matches.
- [ ] Base Deployment selector = Pod labels = Service selector.
- [ ] Named container port agrees with the Java HTTP port and Service targetPort.
- [ ] Overlays patch the intended base resource names.
- [ ] Helm values and template references agree.
- [ ] Argo CD Applications point at the intended dev/prod overlay paths.

## 2. File existence checks

- [ ] `app/src/gitopsdeploymentplatform/*.java` and `docker/Dockerfile`
- [ ] `k8s/base/` + `k8s/overlays/dev/` + `k8s/overlays/prod/`
- [ ] `helm/java-app/` (Chart.yaml, values.yaml, templates, _helpers.tpl)
- [ ] `gitops/argocd/dev-application.example.yaml` + `prod-application.example.yaml`
- [ ] `docs/gitops-flow.md`, `docs/rollback.md`, `README.md`, `TESTING.md`

## 3. YAML / config review checklist

- [ ] All YAML is well-formed; overlays reference `../../base`.
- [ ] dev vs prod differ (replicas, image tag, config) and match the docs.
- [ ] Same image tag is promoted (config differs, image does not).
- [ ] dev Application: auto-sync + self-heal, **prune disabled**.
- [ ] prod Application: **no `automated:` block** (manual sync).

## 4. Security checks

- [ ] **No real secrets** — no Secret object or secret value committed.
- [ ] **No real credentials** — no tokens, registry, or kubeconfig.
- [ ] **No production endpoints** — repo URLs/images are `example.invalid`/placeholders.
- [ ] Container/Pod run non-root; read-only rootfs; capabilities dropped.

## 5. Commands normally used — NOT executed

```bash
# NOT executed
javac --add-modules jdk.httpserver -d out app/src/gitopsdeploymentplatform/*.java
docker build -f docker/Dockerfile -t my-java-app:dev-placeholder .
kubectl kustomize k8s/overlays/dev
kubectl kustomize k8s/overlays/prod
helm lint helm/java-app && helm template demo helm/java-app
kubectl apply --dry-run=client -f gitops/argocd/dev-application.example.yaml
```

## 6. Expected results in a proper environment

- Java endpoints return health, readiness, and the selected env/version.
- Base + overlays render valid resources with matching names/selectors.
- dev and prod render distinct replicas/tags/config.
- Helm renders an equivalent workload from values.
- Argo CD reports the selected revision/path; an approved sync creates resources without exposing secrets.
- Reverting the desired-state commit produces a clear rollback diff.

## 7. Manual review checklist (portfolio quality)

- [ ] README has a clear resume-style summary and an architecture diagram.
- [ ] Kustomize base/overlay split reads cleanly; no duplicated YAML.
- [ ] dev vs prod sync policy difference is obvious and justified.
- [ ] Every command is marked NOT executed; no fake badges/screenshots.
- [ ] Honest about what static review cannot prove.
