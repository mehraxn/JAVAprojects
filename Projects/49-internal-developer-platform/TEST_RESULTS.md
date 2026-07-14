# Test Results

Real output from running the tests in [TESTING.md](TESTING.md). **Nothing here is
invented.** Where a step was not run, it says so explicitly.

Environment used for the recorded runs:

- Shell: GNU bash 5.3 (Cygwin) on Windows
- Helm: v4.2.2
- Docker: daemon running (used for the build + run below)
- Host JDK: **not installed** — Java was compiled and run **inside the container
  image** (the image ships its own JDK/JRE), not on the host.

Not attempted: no Kubernetes cluster and no Argo CD were involved; nothing was
deployed or synced.

---

## 1. Generator command — PASSED

```
$ ./scripts/new-service.sh \
    --name payments-api --owner payments-team --port 8080 \
    --image registry.example.invalid/payments-api \
    --out /tmp/payments-api --force

Generated service: payments-api
Owner: payments-team
Port: 8080
Image: registry.example.invalid/payments-api
Output: /tmp/payments-api
```

## 2. Generated example matches committed example — PASSED

```
$ diff -r /tmp/payments-api examples/new-service
(no output; directories are identical)
```

## 3. Placeholder check — PASSED

```
$ grep -R "__SERVICE_" /tmp/payments-api || echo "(no matches)"
(no matches)
```

## 4. Empty value check — PASSED

```
$ grep -R "SERVICE_NAME=$" /tmp/payments-api ; grep -R "SERVICE_PORT=$" /tmp/payments-api
$ grep -R "EXPOSE $" /tmp/payments-api ; grep -R "name: $" /tmp/payments-api
$ grep -R "owner: $" /tmp/payments-api
(no matches for any check)
```

## 5. Image / input validation — PASSED

The generator accepts valid repository inputs, including registry ports, and rejects
unsafe or invalid values before any template substitution happens.

```
$ ./scripts/new-service.sh ... --image localhost:5000/local-api --out /tmp/local-api --force
Generated service: local-api        # registry port accepted

$ ./scripts/new-service.sh ... --name payments- ...
ERROR: --name 'payments-' is invalid: use lowercase letters, digits, and hyphens; start with a letter; end with a letter or digit; max 50 chars.

$ ./scripts/new-service.sh ... --name <80-character-name> ...
ERROR: --name '<80-character-name>' is invalid: use lowercase letters, digits, and hyphens; start with a letter; end with a letter or digit; max 50 chars.

$ ./scripts/new-service.sh ... --image "bad image" ...
ERROR: --image 'bad image' is invalid: use lowercase repository characters only ...

$ ./scripts/new-service.sh ... --image "registry.example.invalid/bad&api" ...
ERROR: --image 'registry.example.invalid/bad&api' is invalid: use lowercase repository characters only ...

$ ./scripts/new-service.sh ... --image registry.example.invalid/bad-api:latest ...
ERROR: --image 'registry.example.invalid/bad-api:latest' must not include a tag; provide the repository only ...
```

## 6. Dangerous --out safety — PASSED (nothing deleted)

```
$ ./scripts/new-service.sh ... --out . --force
ERROR: Refusing to remove dangerous output directory: .

$ ./scripts/new-service.sh ... --out examples --force
ERROR: Refusing to remove dangerous output directory: examples
# examples/new-service left intact
```

## 7. Java compile — PASSED (inside the container)

No host JDK was available, so compilation was verified by the Docker build's
`javac` stage:

```
#11 [build 4/4] RUN javac -d out src/app/*.java
#11 DONE 0.9s
```

## 8. App run + endpoints — PASSED (container)

```
$ docker run -d --name pay-test -p 18080:8080 registry.example.invalid/payments-api:0.1.0
$ docker logs pay-test
payments-api listening on port 8080

$ curl -s http://localhost:18080/
{"service":"payments-api","message":"hello from payments-api"}

$ curl -s http://localhost:18080/health
{"status":"ok","service":"payments-api"}

$ curl -s http://localhost:18080/ready
{"status":"ready","service":"payments-api"}

$ curl -s -D - -o /dev/null http://localhost:18080/ | grep -i content-type
Content-type: application/json; charset=utf-8
```

## 9. Helm validation — PASSED

```
$ helm lint examples/new-service/helm
1 chart(s) linted, 0 chart(s) failed

$ helm template payments-api examples/new-service/helm | grep '^kind:'
kind: ConfigMap
kind: Service
kind: Deployment
```

Rendered values were correct: `SERVICE_NAME: "payments-api"`,
`SERVICE_PORT: "8080"`, image `registry.example.invalid/payments-api:0.1.0`,
probes on `/health` and `/ready`, non-root, read-only root FS with a writable
`/tmp` volume.

## 10. Docker build — PASSED

```
$ docker build -t registry.example.invalid/payments-api:0.1.0 examples/new-service
...
#15 naming to registry.example.invalid/payments-api:0.1.0 done
BUILD_EXIT=0
```

(The test image was removed after the run.)

## Known limitations

- No host JDK was installed; Java was compiled and executed **inside the built
  image**, not directly on the host. The `javac`/`java` commands in TESTING.md are
  correct for a host with JDK 21.
- Helm renders manifests locally only — no Kubernetes cluster or Argo CD was
  contacted, and nothing was deployed or synced.
- All registries and repo URLs are `example.invalid` placeholders.
