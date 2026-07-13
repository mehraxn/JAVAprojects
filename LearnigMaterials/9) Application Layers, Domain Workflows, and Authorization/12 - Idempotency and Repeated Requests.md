# Idempotency and Repeated Requests

## Learning goals

- Understand idempotency.
- Avoid duplicate side effects from repeated requests.
- Know where idempotency matters.

## What idempotency means

An operation is idempotent if running it multiple times has the same final effect as running it once.

Example: cancelling an already cancelled order may return the current cancelled state instead of creating another cancellation event.

## Why it matters

Repeated calls happen because of retries, double clicks, timeouts, or scheduled jobs. A safe workflow should not create duplicate payments or duplicate approvals by accident.

## Cancel order example

```java
public OrderSnapshot cancelOrder(String orderId) {
    Order order = orders.findRequired(orderId);
    if (order.isCancelled()) {
        return OrderSnapshot.from(order);
    }
    order.cancel();
    orders.save(order);
    return OrderSnapshot.from(order);
}
```

## Payment retry example

For payments, idempotency usually requires an idempotency key.

```java
public PaymentResult charge(String idempotencyKey, PaymentRequest request) {
    return payments.findByKey(idempotencyKey)
            .orElseGet(() -> createNewPayment(idempotencyKey, request));
}
```

## When to use it

Use idempotency for workflows that may be retried and have side effects:

- cancel order;
- approve request;
- retry payment;
- import file;
- send notification.

## Common mistakes

- Assuming users click only once.
- Treating every repeated request as an error.
- Creating duplicate records on retry.
- Not storing enough information to detect a repeated request.

## Mini exercises

1. Make `cancelOrder` idempotent.
2. Design an idempotency key for payments.
3. Decide whether `createProduct` should be idempotent.

## Quick summary

Idempotency makes repeated requests safe by preventing duplicate side effects.
