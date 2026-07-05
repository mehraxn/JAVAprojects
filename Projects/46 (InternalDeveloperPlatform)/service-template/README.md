# Service Template

The reusable "golden path" for a new Java HTTP service. A generator
([../scripts/new-service.sh](../scripts/new-service.sh)) copies this folder and
replaces the `__TOKEN__` placeholders with the developer's answers. **Nothing is
generated, built, or deployed here.**

## Placeholder tokens

| Token | Meaning | Example |
| --- | --- | --- |
| `__SERVICE_NAME__` | DNS-safe service name | `payments-api` |
| `__SERVICE_OWNER__` | owning team | `payments-team` |
| `__SERVICE_PORT__` | container port | `8080` |
| `__IMAGE_REPO__` | image repository (placeholder registry) | `registry.example.invalid/payments-api` |

## What's in the template

```text
template.yaml          input contract (parameters + constraints)
service.yaml           catalog metadata (owner, lifecycle)
app/Dockerfile         multi-stage, non-root image
app/src/service/Main.java   HTTP app with /, /health, /ready
```

The Java `package` stays fixed as `service` so the template **compiles as-is**;
per-service differences (name, port) are injected through environment variables
and config, never by rewriting the source. See a fully rendered result in
[../examples/new-service/](../examples/new-service/).
