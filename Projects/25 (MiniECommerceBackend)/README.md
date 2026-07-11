# Mini E-Commerce Backend

An in-memory Java mini e-commerce backend simulation with beginner-friendly models, service-layer business rules, and a testable command-line interface.

## What it demonstrates

- Product catalog and cart management
- Stock validation and all-or-nothing checkout
- Order status transitions and cancellation with stock restoration
- `BigDecimal` money handling to avoid floating-point money errors
- Defensive copies and unmodifiable snapshots
- Dependency-free automated tests and strict compilation
- Command-based demo CLI

## Features and design

Products have unique IDs and names, positive prices, and nonnegative stock. Carts merge repeated product additions and calculate deterministic totals. Checkout validates every item before changing stock, then creates an order snapshot and removes the cart. Failed checkout leaves stock and cart state unchanged.

New orders start as `CREATED` and may become `PAID` or `CANCELLED`. Both are terminal; only cancelling a created order restores stock.

- `Product` represents catalog items.
- `Cart` represents requested quantities.
- `Order` represents a completed checkout snapshot and status.
- `ShopService` owns catalog, cart, checkout, stock, cancellation, and order logic.
- `Main` handles CLI/demo output only; `Main.run` is directly testable.

## Quick start

Run from this project directory:

```text
javac -Xlint:all -Werror -d out src/miniecommercebackend/*.java

java -cp out miniecommercebackend.Main help
java -cp out miniecommercebackend.Main demo
java -cp out miniecommercebackend.Main catalog-demo
java -cp out miniecommercebackend.Main checkout-demo
java -cp out miniecommercebackend.Main cancel-demo
java -cp out miniecommercebackend.Main failure-demo
```

See [TESTING.md](TESTING.md) for exact validation commands and [TEST_RESULTS.md](TEST_RESULTS.md) for recorded local results.

## Limitations

- Local in-memory operation only; no persistence or database
- No REST API, authentication, or users
- No payment integration or external shipping/inventory service
- Cart checks do not reserve stock between carts
- No production concurrency or distributed transaction guarantees
