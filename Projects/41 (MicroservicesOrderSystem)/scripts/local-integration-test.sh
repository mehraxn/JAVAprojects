#!/usr/bin/env bash
# Local integration test for the microservices order system.
#
# Compiles all four services, starts them on localhost (inventory 8081,
# payment 8082, notification 8083, order 8080), and drives the main business
# flows end to end: valid order, payment rejection with inventory
# compensation, downstream idempotency by order ID, unknown routes, and
# method validation.
#
# Requires: JDK 21 (javac/java), curl, bash. No Docker needed.
# Exit code: 0 only if every check passes.
set -u

cd "$(dirname "$0")/.."

PIDS=()
cleanup() {
  for pid in ${PIDS[@]+"${PIDS[@]}"}; do
    kill "$pid" 2>/dev/null || true
  done
}
trap cleanup EXIT

FAILURES=0
step() { printf '\n== %s\n' "$1"; }
pass() { printf '   PASS: %s\n' "$1"; }
fail() { printf '   FAIL: %s\n' "$1"; FAILURES=$((FAILURES + 1)); }

expect_contains() { # actual, expected-substring, label
  if printf '%s' "$1" | grep -q "$2"; then pass "$3"; else fail "$3 (got: $1)"; fi
}

expect_code() { # url, expected-status, label, [method]
  local code
  code=$(curl -s -o /dev/null -w '%{http_code}' -X "${4:-GET}" "$1")
  if [ "$code" = "$2" ]; then pass "$3"; else fail "$3 (expected $2, got $code)"; fi
}

step "Compiling all four services"
javac -d out/inventory-service    services/inventory-service/src/inventoryservice/*.java       || { fail "inventory-service compile"; exit 1; }
javac -d out/payment-service      services/payment-service/src/paymentservice/*.java           || { fail "payment-service compile"; exit 1; }
javac -d out/notification-service services/notification-service/src/notificationservice/*.java || { fail "notification-service compile"; exit 1; }
javac -d out/order-service        services/order-service/src/orderservice/*.java               || { fail "order-service compile"; exit 1; }
pass "all four services compiled"

step "Starting services on 8081/8082/8083/8080"
PORT=8081 java -cp out/inventory-service    inventoryservice.Main    & PIDS+=($!)
PORT=8082 java -cp out/payment-service      paymentservice.Main      & PIDS+=($!)
PORT=8083 java -cp out/notification-service notificationservice.Main & PIDS+=($!)
PORT=8080 \
  INVENTORY_SERVICE_URL=http://localhost:8081 \
  PAYMENT_SERVICE_URL=http://localhost:8082 \
  NOTIFICATION_SERVICE_URL=http://localhost:8083 \
  java -cp out/order-service orderservice.Main & PIDS+=($!)

step "Waiting for /health on every service"
for port in 8081 8082 8083 8080; do
  ready=0
  for _ in $(seq 1 30); do
    if curl -fsS "http://localhost:${port}/health" >/dev/null 2>&1; then ready=1; break; fi
    sleep 1
  done
  if [ "$ready" = 1 ]; then pass "port ${port} healthy"; else fail "port ${port} never became healthy"; exit 1; fi
done

step "Valid order: JAVA-BOOK x2 @ 25.00 (expect CONFIRMED, stock 10 -> 8)"
response=$(curl -s -X POST "http://localhost:8080/orders?sku=JAVA-BOOK&quantity=2&unitPrice=25.00")
printf '   response: %s\n' "$response"
expect_contains "$response" '"status":"CONFIRMED"' "order is CONFIRMED"
stock=$(curl -s "http://localhost:8081/inventory?sku=JAVA-BOOK")
expect_contains "$stock" '"availableQuantity":8' "JAVA-BOOK stock decreased to 8"
notifications=$(curl -s "http://localhost:8083/notifications")
expect_contains "$notifications" 'is confirmed' "notification recorded"

step "Payment rejection: DEVOPS-KIT x1 @ 10001.00 (expect PAYMENT_REJECTED + compensation)"
response=$(curl -s -X POST "http://localhost:8080/orders?sku=DEVOPS-KIT&quantity=1&unitPrice=10001.00")
printf '   response: %s\n' "$response"
expect_contains "$response" '"status":"PAYMENT_REJECTED"' "order is PAYMENT_REJECTED"
expect_contains "$response" 'inventory was released' "compensation reported"
stock=$(curl -s "http://localhost:8081/inventory?sku=DEVOPS-KIT")
expect_contains "$stock" '"availableQuantity":5' "DEVOPS-KIT stock back to 5 (reservation released)"

step "Downstream idempotency: same order ID reserved twice has one effect"
first=$(curl -s -X POST "http://localhost:8081/inventory/reserve?orderId=IDEMPOTENCY-TEST&sku=JAVA-BOOK&quantity=1")
second=$(curl -s -X POST "http://localhost:8081/inventory/reserve?orderId=IDEMPOTENCY-TEST&sku=JAVA-BOOK&quantity=1")
expect_contains "$first"  '"reserved":true' "first reserve accepted"
expect_contains "$second" '"reserved":true' "identical retry accepted (idempotent)"
stock=$(curl -s "http://localhost:8081/inventory?sku=JAVA-BOOK")
expect_contains "$stock" '"availableQuantity":7' "stock decreased exactly once (8 -> 7)"
curl -s -X POST "http://localhost:8081/inventory/release?orderId=IDEMPOTENCY-TEST" >/dev/null
stock=$(curl -s "http://localhost:8081/inventory?sku=JAVA-BOOK")
expect_contains "$stock" '"availableQuantity":8' "release restored stock to 8"

step "Unknown routes return 404"
expect_code "http://localhost:8080/unknown"        404 "order-service /unknown"
expect_code "http://localhost:8080/orders/unknown" 404 "order-service /orders/unknown"
expect_code "http://localhost:8080/orders-wrong"   404 "order-service /orders-wrong"
expect_code "http://localhost:8081/inventory/unknown" 404 "inventory-service /inventory/unknown"
expect_code "http://localhost:8082/payments"       404 "payment-service /payments"
expect_code "http://localhost:8083/notifications/unknown" 404 "notification-service /notifications/unknown"

step "Method validation"
expect_code "http://localhost:8080/orders"             405 "DELETE /orders is 405" DELETE
expect_code "http://localhost:8082/payments/authorize" 405 "GET /payments/authorize is 405"
expect_code "http://localhost:8081/inventory/reserve"  405 "GET /inventory/reserve is 405"
expect_code "http://localhost:8080/health"             405 "POST /health is 405" POST

step "Bad input returns 400"
expect_code "http://localhost:8080/orders?sku=JAVA-BOOK&quantity=-1&unitPrice=25.00" 400 "negative quantity is 400" POST
expect_code "http://localhost:8080/orders?sku=JAVA-BOOK&quantity=abc&unitPrice=25.00" 400 "non-numeric quantity is 400" POST

printf '\n'
if [ "$FAILURES" -eq 0 ]; then
  printf 'ALL CHECKS PASSED\n'
  exit 0
else
  printf '%d CHECK(S) FAILED\n' "$FAILURES"
  exit 1
fi
