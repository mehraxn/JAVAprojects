# Example: `payments-api`

A fully rendered example of what [../../scripts/new-service.sh](../../scripts/new-service.sh)
would produce for these inputs — committed so you can inspect the output
**without running anything**:

```
--name payments-api --owner payments-team --port 8080
--image registry.example.invalid/payments-api
```

Every `__TOKEN__` from the templates has been substituted. **Nothing here was
built, deployed, or synced.**

```text
app/Dockerfile                app/src/service/Main.java   (image + service source)
service.yaml                  catalog metadata (owner=payments-team)
helm/Chart.yaml               chart + rendered values...
helm/values.yaml              (image repository set to the chosen repo)
helm/values-dev.yaml          example DEV config
helm/values-prod.yaml         example PROD config
gitops/dev/application.yaml    Argo CD Application (auto-sync)
gitops/prod/application.yaml   Argo CD Application (manual sync)
```

> The generic `helm/templates/` (deployment, service, configmap, _helpers) are
> copied **verbatim** from [../../helm-template/templates/](../../helm-template/templates/)
> and are omitted here to avoid duplication — only the per-service *values* and
> *manifests* differ between services.
