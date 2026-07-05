# scripts/

Automation for the golden path. **These scripts were NOT run.**

## `new-service.sh`

Scaffolds a new service from the templates by substituting `__TOKEN__`
placeholders. It is deliberately conservative:

- validates `--name` against a DNS-safe pattern,
- **refuses to overwrite** an existing output directory,
- writes only into the given `--out` folder,
- touches Helm `{{ }}` templates *not at all* — per-service values go through
  `values.yaml`, not template edits.

Reference invocation (**NOT executed**):

```bash
./new-service.sh \
  --name payments-api \
  --owner payments-team \
  --port 8080 \
  --image registry.example.invalid/payments-api \
  --out ../examples/new-service
```

The committed [../examples/new-service/](../examples/new-service/) is what that
exact command would produce — provided so you can inspect the output without
running anything.

> On Windows, run under Git Bash / WSL. Nothing here is executed by this repo,
> and no Docker/Helm/Kubernetes/Argo CD command is invoked by the script.
