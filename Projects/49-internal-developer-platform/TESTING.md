# Testing — Internal Developer Platform

Exact commands to generate a service and verify it end to end. Run them from the
project root. On Windows, use Git Bash or WSL. Record real output in
[TEST_RESULTS.md](TEST_RESULTS.md) — do not paste invented output.

## 1. Make the generator executable

```bash
chmod +x scripts/new-service.sh
```

## 2. Generate an example service

```bash
./scripts/new-service.sh \
  --name payments-api \
  --owner payments-team \
  --port 8080 \
  --image registry.example.invalid/payments-api \
  --out /tmp/payments-api \
  --force
```

## 3. Check the generated files

```bash
find /tmp/payments-api -maxdepth 3 -type f | sort
```

## 4. Check for unresolved placeholders (should print nothing)

```bash
grep -R "__SERVICE_" /tmp/payments-api || true
```

## 5. Check for empty generated values (should print nothing)

```bash
grep -R "SERVICE_NAME=$" /tmp/payments-api || true
grep -R "SERVICE_PORT=$" /tmp/payments-api || true
grep -R "EXPOSE $"       /tmp/payments-api || true
grep -R "^ *name: *$"    /tmp/payments-api || true
grep -R "^ *owner: *$"   /tmp/payments-api || true
```


## 6. Check input validation

Valid image with a registry port should pass:

```bash
./scripts/new-service.sh \
  --name local-api \
  --owner platform-team \
  --port 8081 \
  --image localhost:5000/local-api \
  --out /tmp/local-api \
  --force
```

These invalid inputs should fail safely:

```bash
./scripts/new-service.sh --name payments- --owner payments-team --port 8080 --image registry.example.invalid/payments-api --out /tmp/bad-api --force
./scripts/new-service.sh --name bad-api --owner platform-team --port 8082 --image "bad image" --out /tmp/bad-api --force
./scripts/new-service.sh --name bad-api --owner platform-team --port 8082 --image "registry.example.invalid/bad&api" --out /tmp/bad-api --force
./scripts/new-service.sh --name bad-api --owner platform-team --port 8082 --image registry.example.invalid/bad-api:latest --out /tmp/bad-api --force
./scripts/new-service.sh --name bad-api --owner platform-team --port 8082 --image registry.example.invalid/bad-api --out . --force
./scripts/new-service.sh --name bad-api --owner platform-team --port 8082 --image registry.example.invalid/bad-api --out examples --force
```

## 7. Compile the Java service (requires JDK 21)

```bash
javac -d /tmp/payments-api-out /tmp/payments-api/src/app/*.java
```

## 8. Run it

```bash
SERVICE_NAME=payments-api SERVICE_PORT=8080 java -cp /tmp/payments-api-out app.Main
```

## 9. Test the endpoints (in another terminal)

```bash
curl http://localhost:8080/
curl http://localhost:8080/health
curl http://localhost:8080/ready
```

Expected:

```json
{"service":"payments-api","message":"hello from payments-api"}
{"status":"ok","service":"payments-api"}
{"status":"ready","service":"payments-api"}
```

## 10. Optional: render the Helm chart (requires Helm)

```bash
helm lint /tmp/payments-api/helm
helm template payments-api /tmp/payments-api/helm
```

## 11. Optional: build the image (requires a running Docker daemon)

```bash
docker build -t registry.example.invalid/payments-api:0.1.0 /tmp/payments-api
```

## 12. Clean up

```bash
rm -rf /tmp/payments-api /tmp/local-api /tmp/bad-api /tmp/payments-api-out
```

## Notes

- The committed [examples/new-service/](examples/new-service/) is the real output
  of the command in step 2 (with `--out examples/new-service`).
- No Kubernetes, Argo CD, or cloud infrastructure is contacted by any command
  here. Helm and Docker steps are local only.
