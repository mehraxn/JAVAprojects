# Test Results

Validation performed in the artifact preparation environment. Re-run these on
your own machine before making final resume claims.

## Java compile

- `javac -d out-v1 app-v1/src/app/*.java`: passed
- `javac -d out-v2 app-v2/src/app/*.java`: passed

## Java endpoint smoke test

- v1 `/version`: passed, returned `{"version":"v1"}`
- v1 `/health`: passed, returned `ok`
- v1 `/ready`: passed, returned `ready`
- v1 `/`: passed, returned a v1 greeting
- v2 `/version`: passed, returned `{"version":"v2"}`
- v2 `/health`: passed, returned `ok`
- v2 `/ready`: passed, returned `ready`
- v2 `/`: passed, returned a v2 greeting with `new-greeting`

## YAML syntax review

Non-template Kubernetes and monitoring YAML files parsed successfully with
Python YAML tooling. Helm templates were not rendered in this environment.

## Not run here

- Docker builds: Docker was not available.
- Kubernetes apply: no cluster and no kubectl available.
- Helm render: Helm was not available.
- Real traffic switching/canary shifting: not performed.

## User-local results

Paste your own final outputs here after running `TESTING.md` locally.
