# Testing — Microservices Order System

Exact commands to validate this project. Results actually observed with these
commands are recorded honestly in [TEST_RESULTS.md](TEST_RESULTS.md).
Commands use POSIX shell syntax; on Windows use Git Bash (or `curl.exe` from
PowerShell). Run everything from this project folder.

## A) Java-only integration test (no Docker needed)

Requires JDK 21 and curl:

```bash
./scripts/local-integration-test.sh
```

It compiles all four services, starts them on 8080–8083 by default, and checks:
health endpoints, the valid order flow (CONFIRMED, stock 10→8, notification
recorded), payment rejection with inventory compensation (stock restored),
downstream idempotency (identical retried reservation has one effect),
unknown routes (404), wrong methods (405), and bad input (400). Exit code 0
only if everything passes.

If any default port is already in use, override the ports:

```bash
ORDER_PORT=18080 INVENTORY_PORT=18081 PAYMENT_PORT=18082 NOTIFICATION_PORT=18083   ./scripts/local-integration-test.sh
```

The script uses bounded curl timeouts and fails fast if a service cannot start,
which makes port conflicts easier to diagnose.

No JDK? Run it inside a container:

```bash
docker run --rm -v "$PWD:/w" -w /w eclipse-temurin:21-jdk bash -c \
  "command -v curl >/dev/null || (apt-get update -qq && apt-get install -y -qq curl); \
   ./scripts/local-integration-test.sh"
```

## B) Manual Java compile

```bash
javac -d out/inventory-service    services/inventory-service/src/inventoryservice/*.java
javac -d out/payment-service      services/payment-service/src/paymentservice/*.java
javac -d out/notification-service services/notification-service/src/notificationservice/*.java
javac -d out/order-service        services/order-service/src/orderservice/*.java

# start each in its own terminal (order-service needs the downstream URLs):
PORT=8081 java -cp out/inventory-service inventoryservice.Main
PORT=8082 java -cp out/payment-service paymentservice.Main
PORT=8083 java -cp out/notification-service notificationservice.Main
PORT=8080 INVENTORY_SERVICE_URL=http://localhost:8081 \
  PAYMENT_SERVICE_URL=http://localhost:8082 \
  NOTIFICATION_SERVICE_URL=http://localhost:8083 \
  java -cp out/order-service orderservice.Main
```

## C) Docker Compose validation

```bash
docker compose config --quiet     # validate the file
docker compose build
docker compose up -d --wait       # waits for healthchecks
docker compose ps                 # expect all four (healthy)
```

Health checks:

```bash
curl http://localhost:8080/health
curl http://localhost:8081/health
curl http://localhost:8082/health
curl http://localhost:8083/health
```

Order flow (expect 201, `"status":"CONFIRMED"`, then stock 8):

```bash
curl -X POST "http://localhost:8080/orders?sku=JAVA-BOOK&quantity=2&unitPrice=25.00"
curl "http://localhost:8081/inventory?sku=JAVA-BOOK"
curl "http://localhost:8083/notifications"
```

Payment rejection flow (expect `"status":"PAYMENT_REJECTED"`,
`inventory was released`, and DEVOPS-KIT stock still 5):

```bash
curl -X POST "http://localhost:8080/orders?sku=DEVOPS-KIT&quantity=1&unitPrice=10001.00"
curl "http://localhost:8081/inventory?sku=DEVOPS-KIT"
```

Error handling:

```bash
curl -i "http://localhost:8080/orders-wrong"          # 404
curl -i -X DELETE "http://localhost:8080/orders"      # 405
```

Cleanup:

```bash
docker compose down
```

## D) Kubernetes validation (no cluster changes)

```bash
kubectl kustomize k8s/                  # renders namespace + ConfigMap + 4x(Deployment,Service)
kubectl apply --dry-run=client -k k8s/  # validates without creating anything
```

Note: the client dry-run still needs a reachable cluster context to download
validation schemas; `kubectl kustomize` works fully offline.

## E) kind workflow (optional, not required)

```bash
docker compose build    # produces the four 0.1.0 images

kind create cluster --name order-system
kind load docker-image microservices-order/order-service:0.1.0        --name order-system
kind load docker-image microservices-order/inventory-service:0.1.0    --name order-system
kind load docker-image microservices-order/payment-service:0.1.0      --name order-system
kind load docker-image microservices-order/notification-service:0.1.0 --name order-system

kubectl apply -k k8s/
kubectl -n microservices-order-demo get pods
kubectl -n microservices-order-demo port-forward svc/order-service 8080:8080
# then run the same curl flows as in section C

# cleanup
kubectl delete -k k8s/
kind delete cluster --name order-system
```

Record the outcome in TEST_RESULTS.md only if you actually run this.

## F) Cleanup

```bash
rm -rf out
docker compose down            # if the stack is up
kubectl delete -k k8s/         # only if you applied it to a cluster
```
