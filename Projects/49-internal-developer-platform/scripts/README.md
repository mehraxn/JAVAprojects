# scripts/

Automation for the golden path.

## `new-service.sh`

Scaffolds a new, self-contained Java service by copying the
[../template/](../template/) folder and substituting `__TOKEN__` placeholders
with your inputs. It is conservative and safe:

- validates `--name` and `--owner` (DNS-safe: lowercase, digits, hyphens, starts with a letter, ends with a letter/digit),
- keeps service names short enough for generated Kubernetes names (max 50 chars),
- validates `--port` (numeric, 1-65535),
- validates `--image` (repository only, no tag, no whitespace/special shell characters; registry ports like `localhost:5000/name` are allowed),
- refuses to overwrite an existing `--out` directory unless `--force` is given,
- writes only into the given `--out` folder,
- leaves Helm `{{ }}` templates untouched (they carry no `__TOKEN__`; per-service
  values go through `values.yaml`).

### Usage

```bash
./scripts/new-service.sh \
  --name payments-api \
  --owner payments-team \
  --port 8080 \
  --image registry.example.invalid/payments-api \
  --out examples/new-service \
  --force
```

The committed [../examples/new-service/](../examples/new-service/) is the real
output of exactly this command.

> On Windows, run under Git Bash / WSL. The generator only copies and substitutes
> files; it does not invoke Docker, Helm, Kubernetes, or Argo CD.
