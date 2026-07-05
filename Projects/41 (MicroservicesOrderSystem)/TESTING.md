# Testing — Microservices Order System

> **Nothing was executed.** No Java, Docker, Docker Compose, Kubernetes, or
> network command ran. This documents static review plus the manual tests a
> disposable environment *would* use.

## 1. Static validation checklist

- [ ] Each service has its own package, entry point, model/service/controller, and Dockerfile.
- [ ] Dockerfile source paths match the project-root build context.
- [ ] Compose service names match the order-service downstream URLs.
- [ ] Java ports, Compose ports, Kubernetes ports, and probes agree (8080–8083).
- [ ] Deployment selectors = Pod labels = Service selectors.
- [ ] Idempotency (order-ID key) and compensation (release on payment reject) present in code.

## 2. File existence checks

- [ ] `services/{order,inventory,payment,notification}-service/` each with `src/**` + `Dockerfile`.
- [ ] `docker-compose.yml`, `.dockerignore`.
- [ ] `k8s/` namespace, per-service manifests, `order-configmap.yaml`, `kustomization.yaml`.
- [ ] `docs/architecture.md`, `docs/service-communication.md`, `README.md`, `TESTING.md`.

## 3. YAML / config review checklist

- [ ] Compose builds each image from root; all services on `order-network`.
- [ ] K8s images are obvious placeholders.
- [ ] ConfigMap URLs use in-cluster Service DNS names.
- [ ] `kubectl kustomize k8s` would assemble namespace + 4 Deployments + 4 Services.

## 4. Security checks

- [ ] **No real secrets** — no Secret object or secret value committed.
- [ ] **No real credentials** — no payment/email/SMS provider keys; all mock.
- [ ] **No production endpoints** — only `localhost`/Compose DNS/placeholder images.
- [ ] Containers non-root (10001); K8s read-only rootfs + dropped capabilities.

## 5. Commands normally used — NOT executed

```bash
# NOT executed
docker compose config
docker compose build && docker compose up
kubectl kustomize k8s
kubectl apply --dry-run=client -k k8s

# NOT executed — exercise the flow
curl "http://localhost:8081/inventory?sku=JAVA-BOOK"
curl -X POST "http://localhost:8080/orders?sku=JAVA-BOOK&quantity=2&unitPrice=25.00"
curl "http://localhost:8080/orders"
curl -X POST "http://localhost:8080/orders?sku=DEVOPS-KIT&quantity=1&unitPrice=10001.00"
```

## 6. Expected results in a proper environment

| Case | Expected |
| --- | --- |
| Valid order | 201; status CONFIRMED; stock decremented; one mock notification |
| Total > 10000.00 | mock payment declines → PAYMENT_REJECTED; inventory release requested |
| Unknown/insufficient SKU | INVENTORY_REJECTED |
| Repeat reserve/pay (same order ID) | idempotent; no double effect |
| Missing/invalid params | 400 validation |
| Downstream unavailable | failure detail recorded; no stack trace leaked |
| K8s render | namespace + 4 Deployments + 4 Services with matching selectors/ports |

## 7. Manual review checklist (portfolio quality)

- [ ] README states service ownership, ports, and the status flow clearly.
- [ ] Failure model (compensation, idempotency) is explained, not just the happy path.
- [ ] Limitations (in-memory, no transactions) are stated honestly.
- [ ] Every command marked NOT executed; no fake badges/screenshots.
- [ ] Consistent multi-stage, non-root Dockerfiles across all four services.
