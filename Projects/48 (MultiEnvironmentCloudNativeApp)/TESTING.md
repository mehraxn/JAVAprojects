# Testing — Multi-Environment Cloud-Native App

> **Nothing was executed.** No Java, Docker, CI/CD, Kubernetes, Kustomize, Helm,
> or Argo CD command ran; nothing was installed and **no environment was
> deployed.** This documents static review and expected behavior.

## 1. Static validation checklist

- [ ] App compiles conceptually (`package multienvironmentcloudnativeapp`).
- [ ] Each `k8s/overlays/<env>/` references `../../base` + adds replica/resource/config patches.
- [ ] Per-env values distinct (replicas 1/2/4; log DEBUG/INFO/WARN; feature flag), matching between Kustomize and Helm.
- [ ] All three overlays pin the **same** image tag (config differs, image does not).
- [ ] app-of-apps includes `environments/*/application.yaml`; prod has no `automated:` block.

## 2. File existence checks

- [ ] `app/src/**`, `Dockerfile`
- [ ] `k8s/base/*`, `k8s/overlays/{dev,staging,prod}/*`
- [ ] `helm/app/**` (Chart.yaml, values*, templates)
- [ ] `environments/{dev,staging,prod}/` (application.yaml, secret.example.yaml, README)
- [ ] `gitops/*`, `ci/*`, `docs/*`, `README.md`, `TESTING.md`

## 3. YAML / config review checklist

- [ ] All YAML well-formed across k8s/helm/environments/gitops/ci.
- [ ] Namespaces per env (`app-dev`/`app-staging`/`app-prod`) are consistent.
- [ ] CI templates parked/inert (`workflow_dispatch` / `enabled: false`); promotion is build-once, by digest.

## 4. Security checks

- [ ] **No real secrets** — only `*.example.yaml` with `REPLACE_ME`; `.gitignore` excludes real `secret.yaml`.
- [ ] **No real credentials** — no registry/cluster creds.
- [ ] **No production endpoints** — `example.invalid`/placeholders only.
- [ ] Non-root, read-only rootfs, dropped capabilities.

## 5. Commands normally used — NOT executed

```bash
# NOT executed
kubectl kustomize "Projects/48 (MultiEnvironmentCloudNativeApp)/k8s/overlays/prod"
helm template app helm/app -f helm/app/values-prod.yaml
kubectl apply -f gitops/app-of-apps.yaml
argocd app sync cloud-native-app-prod
```

## 6. Expected results in a proper environment

- `kubectl kustomize` renders distinct dev/staging/prod manifests (1/2/4 replicas, different config).
- `helm template`/`lint` produce equivalent output to the overlays.
- Registering the app-of-apps creates three child apps; dev/staging auto-sync, prod waits for manual sync.
- A promotion PR (image tag → verified digest) rolls out via Argo CD; `git revert` rolls back.

## 7. Manual review checklist (portfolio quality)

- [ ] README makes "build once, promote the artifact" explicit.
- [ ] Per-env divergence is visible and consistent (Kustomize ↔ Helm).
- [ ] Promotion + rollback flows are clear and auditable.
- [ ] Every command marked NOT executed; no fake badges/screenshots.
- [ ] Honest that no environment was deployed.
