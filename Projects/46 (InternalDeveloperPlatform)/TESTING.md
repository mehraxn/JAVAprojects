# Testing — Internal Developer Platform

> **Nothing was executed.** No generator, Docker, Kubernetes, Helm, or Argo
> CD/Flux command ran; nothing was installed and **nothing was deployed.** This
> documents static review and expected behavior.

## 1. Static validation checklist

- [ ] Template Java compiles (fixed `package service;`, no `__TOKEN__` in code).
- [ ] Same `__TOKEN__` names appear in `template.yaml` and the files that use them.
- [ ] `examples/new-service/` has no leftover `__...__`; values match the documented inputs.
- [ ] Helm `_helpers` names referenced by templates are defined; no `__TOKEN__` in Helm `{{ }}`.
- [ ] Generator validates name + refuses to overwrite an existing dir.

## 2. File existence checks

- [ ] `service-template/` (template.yaml, service.yaml, app/Dockerfile, app/src/service/Main.java, README)
- [ ] `helm-template/` (Chart.yaml, values*, templates/*)
- [ ] `gitops-template/environments/{dev,prod}/application.yaml` + README
- [ ] `scripts/new-service.sh` + README; `examples/new-service/**`
- [ ] `docs/onboarding-guide.md`, `platform-architecture.md`, `service-lifecycle.md`, `README.md`, `TESTING.md`

## 3. YAML / config review checklist

- [ ] All YAML/JSON well-formed.
- [ ] dev Application auto-syncs; prod is manual; namespaces differ.
- [ ] Rendered example's helm values point at the chosen image repo.

## 4. Security checks

- [ ] **No real secrets** — none present.
- [ ] **No real credentials** — no tokens/registry creds.
- [ ] **No production endpoints** — all `example.invalid`/placeholders.
- [ ] Template enforces non-root, resource limits, probes.

## 5. Commands normally used — NOT executed

```bash
# NOT executed — result already committed under examples/new-service/
scripts/new-service.sh --name payments-api --owner payments-team \
  --port 8080 --image registry.example.invalid/payments-api --out examples/new-service
helm template payments-api helm-template -f helm-template/values-dev.yaml
kubectl apply -f gitops-template/environments/dev/application.yaml
```

## 6. Expected results in a proper environment

- The generator produces a folder identical to `examples/new-service/`.
- `helm lint`/`helm template` render valid manifests.
- The generated app compiles and containerizes.
- Argo CD dev auto-syncs; prod waits for a manual sync.

## 7. Manual review checklist (portfolio quality)

- [ ] README explains the IDP/golden-path idea and the onboarding flow.
- [ ] Template + generator + rendered example tell one coherent story.
- [ ] `__TOKEN__` vs Helm `{{ }}` separation is explained.
- [ ] Every command marked NOT executed; no fake badges/screenshots.
- [ ] Honest that the generator was not run and nothing deployed.
