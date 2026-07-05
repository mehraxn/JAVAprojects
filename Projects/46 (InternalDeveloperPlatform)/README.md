# Internal Developer Platform (mini)

*A mini Internal Developer Platform showing self-service onboarding of a new Java service — a golden-path template, Helm chart, GitOps wiring, and a generator script, with a fully rendered example.*

## Problem this project solves

Every new service shouldn't mean filing tickets and copy-pasting YAML from an old
repo. This project demonstrates the **platform-engineering** answer: a **golden
path** where a developer answers a few questions and gets a consistent,
guardrail-compliant service — source, Dockerfile, Helm chart, and GitOps
manifests — while still owning it.

## Technologies & concepts

- **Golden-path templating** with `__TOKEN__` placeholders + a generator script
- **Java 21** service template (compiles as-is) + hardened Dockerfile
- **Helm** chart template with dev/prod values
- **GitOps** (Argo CD `Application` per environment; dev auto, prod manual)
- **Self-service / paved road**, catalog metadata, ownership

## Architecture overview

```
 developer inputs (name, owner, port, image)
        │  scripts/new-service.sh  (substitutes __TOKEN__)
        ▼
 service-template/  +  helm-template/  +  gitops-template/
        │  PR → merge
        ▼
 CI builds image  →  Argo CD syncs Helm release  (dev auto · prod manual)
```

## Project structure

```text
service-template/     Java app + Dockerfile + template.yaml + catalog metadata
helm-template/        reusable chart (+ values-dev/staging via values)
gitops-template/      Argo CD Application per environment
scripts/              new-service.sh generator (+ README)
examples/new-service/ rendered output for a "payments-api" service
ci/  k8s/policies/     supporting pipeline + guardrail
docs/onboarding-guide.md  docs/platform-architecture.md  docs/service-lifecycle.md
README.md  TESTING.md
```

## Important files explained

- **service-template/** — `template.yaml` (input contract), a **compiling** Java app (fixed `package service`, per-service values via env/config), hardened Dockerfile, catalog `service.yaml`.
- **helm-template/** — values-driven chart (deployment/service/configmap/_helpers), non-root, probes, resource limits.
- **gitops-template/** — dev `Application` auto-syncs; prod is manual; both with deletion-protection finalizers.
- **scripts/new-service.sh** — validates the name, refuses to overwrite, substitutes `__TOKEN__`s (only in non-Helm files, so Helm's `{{ }}` is untouched).
- **examples/new-service/** — the exact `payments-api` output, committed so you can inspect it without running the generator.

## How it would work in a real environment

A developer runs `new-service.sh` (or a Backstage-style portal), gets a generated
folder, opens a PR, and on merge CI builds the image and Argo CD deploys it —
automatically to dev, on a human's click to prod. The `__TOKEN__` design keeps
generation simple and collision-free with Helm templating.

## What was prepared but NOT executed

Prepared: all three templates, the generator, supporting CI/policy, three docs,
and a fully rendered example. **Not executed:** the generator was **not run**
(output is committed instead), no image built, no `helm`/`kubectl`, no Argo
CD/Flux sync. **Nothing was deployed.**

## Security notes

- **No real secrets or credentials** anywhere; all repo URLs/registries are `example.invalid`.
- Guardrails baked into the template: non-root, resource limits, probes.
- The generator is conservative: name validation + refuses to overwrite an existing dir.
- Ownership is explicit (catalog `service.yaml`), so self-service ≠ ownerless.

## Limitations

- The generator/Helm/kubectl/Argo CD were never run; the example is illustrative.
- Java compilation not run (no JDK on the authoring machine).
- No live catalog/portal; templating is token-substitution, not a full scaffolder.

## Future improvements

- Back the golden path with Backstage software templates + a service catalog.
- Add CI that lints/tests the generated service and `helm lint`s the chart.
- Template versioning + an upgrade path for existing services; policy-as-code gates.

## What I learned

- What an **Internal Developer Platform / golden path** is and why it reduces toil without removing ownership.
- Designing a **template + generator** that stays collision-free with Helm syntax.
- Wiring **GitOps per environment** with sane dev-auto / prod-manual defaults.
- Committing a **worked example** so reviewers see output without running anything.
