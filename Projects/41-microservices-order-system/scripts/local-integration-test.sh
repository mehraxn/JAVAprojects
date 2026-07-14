#!/usr/bin/env bash
# Local integration test for the microservices order system.
#
# Compiles all four services, starts them on localhost, and drives the main
# business flows end to end: valid order, payment rejection with inventory
# compensation, downstream idempotency by order ID, unknown routes, and method
# validation.
#
# Default ports:
#   order: 8080, inventory: 8081, payment: 8082, notification: 8083
# Override them if a port is already in use, for example:
#   ORDER_PORT=18080 INVENTORY_PORT=18081 PAYMENT_PORT=18082 NOTIFICATION_PORT=18083 \
#     ./scripts/local-integration-test.sh
#
# Requires: JDK 21 (javac/java), curl, bash. No Docker needed.
# Exit code: 0 only if every check passes.
set -u

cd "$(dirname "$0")/.."

ORDER_PORT="${ORDER_PORT:-8080}"
INVENTORY_PORT="${INVENTORY_PORT:-8081}"
PAYMENT_PORT="${PAYMENT_PORT:-8082}"
NOTIFICATION_PORT="${NOTIFICATION_PORT:-8083}"

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

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    printf 'Missing required command: %s\n' "$1" >&2
    exit 1
  fi
}

validate_port() {
  local value="$1" name="$2"
  if ! printf '%s' "$value" | grep -Eq '^[0-9]+$'; then
    printf '%s must be a numeric TCP port; got: %s\n' "$name" "$value" >&2
    exit 1
  fi
  if [ "$value" -lt 1 ] || [ "$value" -gt 65535 ]; then
    printf '%s must be between 1 and 65535; got: %s\n' "$name" "$value" >&2
    exit 1
  fi
}

check_distinct_ports() {
  local ports="$ORDER_PORT $INVENTORY_PORT $PAYMENT_PORT $NOTIFICATION_PORT"
  for p in $ports; do
    count=0
    for q in $ports; do [ "$p" = "$q" ] && count=$((count + 1)); done
    if [ "$count" -gt 1 ]; then
      printf 'Ports must be distinct; duplicate port: %s\n' "$p" >&2
      exit 1
    fi
  done
}

curl_body() { curl --max-time 5 -fsS "$@"; }
curl_status() { curl --max-time 5 -s -o /dev/null -w '%{http_code}' "$@"; }

expect_contains() { # actual, expected-substring, label
  if printf '%s' "$1" | grep -q "$2"; then pass "$3"; else fail "$3 (got: $1)"; fi
}

expect_code() { # url, expected-status, label, [method]
  local code
  code=$(curl_status -X "${4:-GET}" "$1")
  if [ "$code" = "$2" ]; then pass "$3"; else fail "$3 (expected $2, got $code)"; fi
}

start_service() { # label, command...
  local label="$1"
  shift
  "$@" &
  local pid=$!
  PIDS+=("$pid")
  sleep 0.3
  if ! kill -0 "$pid" 2>/dev/null; then
    fail "$label failed to start; check whether its port is already in use"
    exit 1
  fi
}

require_command javac
require_command java
require_command curl
validate_port "$ORDER_PORT" ORDER_PORT
validate_port "$INVENTORY_PORT" INVENTORY_PORT
validate_port "$PAYMENT_PORT" PAYMENT_PORT
validate_port "$NOTIFICATION_PORT" NOTIFICATION_PORT
check_distinct_ports

step "Compiling all four services"
javac -d out/inventory-service    services/inventory-service/src/inventoryservice/*.java       || { fail "inventory-service compile"; exit 1; }
javac -d out/payment-service      services/payment-service/src/paymentservice/*.java           || { fail "payment-service compile"; exit 1; }
javac -d out/notification-service services/notification-service/src/notificationservice/*.java || { fail "notification-service compile"; exit 1; }
javac -d out/order-service        services/order-service/src/orderservice/*.java               || { fail "order-service compile"; exit 1; }
pass "all four services compiled"

step "Starting services on inventory=${INVENTORY_PORT}, payment=${PAYMENT_PORT}, notification=${NOTIFICATION_PORT}, order=${ORDER_PORT}"
start_service "inventory-service"    env PORT="$INVENTORY_PORT" java -cp out/inventory-service inventoryservice.Main
start_service "payment-service"      env PORT="$PAYMENT_PORT" java -cp out/payment-service paymentservice.Main
start_service "notification-service" env PORT="$NOTIFICATION_PORT" java -cp out/notification-service notificationservice.Main
start_service "order-service"        env PORT="$ORDER_PORT" \
  INVENTORY_SERVICE_URL="http://localhost:${INVENTORY_PORT}" \
  PAYMENT_SERVICE_URL="http://localhost:${PAYMENT_PORT}" \
  NOTIFICATION_SERVICE_URL="http://localhost:${NOTIFICATION_PORT}" \
  java -cp out/order-service orderservice.Main

step "Waiting for /health on every service"
for pair in \
  "inventory-service:$INVENTORY_PORT" \
  "payment-service:$PAYMENT_PORT" \
  "notification-service:$NOTIFICATION_PORT" \
  "order-service:$ORDER_PORT"; do
  label="${pair%%:*}"
  port="${pair##*:}"
  ready=0
  for _ in $(seq 1 30); do
    if curl --max-time 2 -fsS "http://localhost:${port}/health" >/dev/null 2>&1; then ready=1; break; fi
    sleep 1
  done
  if [ "$ready" = 1 ]; then pass "${label} healthy on port ${port}"; else fail "${label} on port ${port} never became healthy"; exit 1; fi
done

ORDER_URL="http://localhost:${ORDER_PORT}"
INVENTORY_URL="http://localhost:${INVENTORY_PORT}"
PAYMENT_URL="http://localhost:${PAYMENT_PORT}"
NOTIFICATION_URL="http://localhost:${NOTIFICATION_PORT}"

step "Valid order: JAVA-BOOK x2 @ 25.00 (expect CONFIRMED, stock 10 -> 8)"
response=$(curl_body -X POST "${ORDER_URL}/orders?sku=JAVA-BOOK&quantity=2&unitPrice=25.00")
printf '   response: %s\n' "$response"
expect_contains "$response" '"status":"CONFIRMED"' "order is CONFIRMED"
stock=$(curl_body "${INVENTORY_URL}/inventory?sku=JAVA-BOOK")
expect_contains "$stock" '"availableQuantity":8' "JAVA-BOOK stock decreased to 8"
notifications=$(curl_body "${NOTIFICATION_URL}/notifications")
expect_contains "$notifications" 'is confirmed' "notification recorded"

step "Payment rejection: DEVOPS-KIT x1 @ 10001.00 (expect PAYMENT_REJECTED + compensation)"
response=$(curl_body -X POST "${ORDER_URL}/orders?sku=DEVOPS-KIT&quantity=1&unitPrice=10001.00")
printf '   response: %s\n' "$response"
expect_contains "$response" '"status":"PAYMENT_REJECTED"' "order is PAYMENT_REJECTED"
expect_contains "$response" 'inventory was released' "compensation reported"
stock=$(curl_body "${INVENTORY_URL}/inventory?sku=DEVOPS-KIT")
expect_contains "$stock" '"availableQuantity":5' "DEVOPS-KIT stock back to 5 (reservation released)"

step "Downstream idempotency: same order ID reserved twice has one effect"
first=$(curl_body -X POST "${INVENTORY_URL}/inventory/reserve?orderId=IDEMPOTENCY-TEST&sku=JAVA-BOOK&quantity=1")
second=$(curl_body -X POST "${INVENTORY_URL}/inventory/reserve?orderId=IDEMPOTENCY-TEST&sku=JAVA-BOOK&quantity=1")
expect_contains "$first"  '"reserved":true' "first reserve accepted"
expect_contains "$second" '"reserved":true' "identical retry accepted (idempotent)"
stock=$(curl_body "${INVENTORY_URL}/inventory?sku=JAVA-BOOK")
expect_contains "$stock" '"availableQuantity":7' "stock decreased exactly once (8 -> 7)"
curl_body -X POST "${INVENTORY_URL}/inventory/release?orderId=IDEMPOTENCY-TEST" >/dev/null
stock=$(curl_body "${INVENTORY_URL}/inventory?sku=JAVA-BOOK")
expect_contains "$stock" '"availableQuantity":8' "release restored stock to 8"

step "Unknown routes return 404"
expect_code "${ORDER_URL}/unknown"        404 "order-service /unknown"
expect_code "${ORDER_URL}/orders/unknown" 404 "order-service /orders/unknown"
expect_code "${ORDER_URL}/orders-wrong"   404 "order-service /orders-wrong"
expect_code "${INVENTORY_URL}/inventory/unknown" 404 "inventory-service /inventory/unknown"
expect_code "${PAYMENT_URL}/payments"       404 "payment-service /payments"
expect_code "${NOTIFICATION_URL}/notifications/unknown" 404 "notification-service /notifications/unknown"

step "Method validation"
expect_code "${ORDER_URL}/orders"             405 "DELETE /orders is 405" DELETE
expect_code "${PAYMENT_URL}/payments/authorize" 405 "GET /payments/authorize is 405"
expect_code "${INVENTORY_URL}/inventory/reserve"  405 "GET /inventory/reserve is 405"
expect_code "${ORDER_URL}/health"             405 "POST /health is 405" POST

step "Bad input returns 400"
expect_code "${ORDER_URL}/orders?sku=JAVA-BOOK&quantity=-1&unitPrice=25.00" 400 "negative quantity is 400" POST
expect_code "${ORDER_URL}/orders?sku=JAVA-BOOK&quantity=abc&unitPrice=25.00" 400 "non-numeric quantity is 400" POST

printf '\n'
if [ "$FAILURES" -eq 0 ]; then
  printf 'ALL CHECKS PASSED\n'
  exit 0
else
  printf '%d CHECK(S) FAILED\n' "$FAILURES"
  exit 1
fi
