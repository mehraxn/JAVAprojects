# Architecture

## Service ownership

- Order service owns order IDs, totals, status, and orchestration decisions.
- Inventory service owns stock counts and reservations. Other services never edit its maps.
- Payment service owns mock authorization records. It never handles payment credentials.
- Notification service owns a mock delivery history. It has no email or SMS integration.

Each service is independently compiled and packaged. There is deliberately no shared Java model module; HTTP contracts are the boundary.

## Request sequence

~~~text
client
  -> order-service
       -> inventory-service: reserve(orderId, sku, quantity)
       -> payment-service: authorize(orderId, total)
          -> inventory-service: release(orderId)  [when payment rejects]
       -> notification-service: record(orderId, message) [when confirmed]
  <- final order status
~~~

Inventory and payment operations use the order ID as an idempotency key for identical retries. A conflicting retry is rejected. This does not make the entire workflow exactly-once or transactional.

## Failure model

An inventory rejection stops processing. A payment rejection requests inventory compensation. Notification failure does not reverse a confirmed order; the order detail records that delivery was not confirmed. Network failures currently map to a failed downstream result after a short timeout.

## Deployment views

Docker Compose provides one private bridge network and exposes all four ports for learning. Kubernetes uses one namespace, four Deployments, four ClusterIP Services, and a non-sensitive ConfigMap for order-service URLs.

No deployment or runtime validation was performed.
