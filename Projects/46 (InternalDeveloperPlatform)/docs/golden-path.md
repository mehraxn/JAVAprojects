# Golden Path

The golden path is the paved road for creating a new service: it removes repeated
setup without hiding ownership or blocking justified exceptions.

## Inputs

The generator ([../scripts/new-service.sh](../scripts/new-service.sh)) accepts the
inputs defined in [../template.yaml](../template.yaml):

| Flag | Token | Example |
| --- | --- | --- |
| `--name` | `__SERVICE_NAME__` | `payments-api` |
| `--owner` | `__SERVICE_OWNER__` | `payments-team` |
| `--port` | `__SERVICE_PORT__` | `8080` |
| `--image` | `__SERVICE_IMAGE__` | `registry.example.invalid/payments-api` |

## Generated files

For each service the generator emits a complete, self-contained folder (see the
rendered [../examples/new-service/](../examples/new-service/)):

- `README.md` — how to build, run, and test the service
- `catalog-info.yaml` — catalog metadata (owner, lifecycle, image repo)
- `Dockerfile` — multi-stage, non-root image
- `src/app/Main.java` — HTTP app with `/`, `/health`, `/ready`
- `helm/` — Helm chart with `templates/` and dev/prod values
- `gitops/` — Argo CD Applications for dev and prod

## Acceptance criteria

A generated service is considered good when:

- no `__TOKEN__` placeholders remain and no generated value is empty,
- the Java service compiles and serves the three endpoints,
- `helm lint` passes and `helm template` renders valid manifests,
- guardrails are present: non-root, resource limits, probes, read-only root FS.

## Upgrades and exceptions

The template itself is versioned (`Chart.yaml` `version`, `template.yaml`
`apiVersion`). A real platform ships breaking template changes with a migration
note rather than a silent bump. The golden path is the default, not a cage: teams
with a justified need can diverge, ideally contributing the pattern back.
