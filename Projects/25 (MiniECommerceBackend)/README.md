# Mini E-Commerce Backend

A standard-Java in-memory shopping backend focused on product catalogs, carts, stock validation, order placement, and simple order-status transitions.

## Features

- Add products with unique IDs, names, positive prices, and nonnegative stock.
- List products in insertion order.
- Search products by ID or name.
- Create carts with generated IDs.
- Add products to carts without exceeding current stock.
- Remove complete product lines from carts.
- Calculate cart totals with `BigDecimal`.
- Place orders using an all-or-nothing stock check.
- Preserve an immutable item and total snapshot in each order.
- Track `CREATED`, `PAID`, and `CANCELLED` status.
- Restore stock exactly once when a created order is cancelled.
- Prevent repeated checkout by removing a cart after successful order placement.

## Main models and service

- `Product` — catalog identity, name, price, and stock quantity.
- `Cart` — product IDs and requested quantities.
- `Order` — cart reference, immutable item snapshot, total, timestamp, and status.
- `ShopService` — synchronized catalog, cart, stock, checkout, and order operations.
- `Main` — console demonstration.

## How the program works

`ShopService` owns products, active carts, and placed orders. Adding to a cart checks current stock. Checkout validates every cart line and calculates the complete total before reducing any stock, so a failed order leaves all state unchanged. Successful checkout removes the cart and stores an order snapshot.

## How in-memory storage works

`ShopService` maintains insertion-ordered maps for products, active carts, and orders. Product and order results are copied before being returned. Cart IDs use `CART-1`, while order IDs use `ORDER-1`. Data is not persisted and is lost when the process exits.

Checkout first validates every item and calculates the total without changing state. Only after all products have sufficient stock does it create the order, reduce each stock value, and remove the cart. A failed checkout therefore leaves stock, carts, and order history unchanged.

## Order statuses

- New orders start as `CREATED`.
- A created order can become `PAID` or `CANCELLED`.
- Paid and cancelled orders are terminal.
- Cancelling a created order restores its product quantities before marking it cancelled.

## Example usage

```text
javac -d out src/miniecommercebackend/*.java
java -cp out miniecommercebackend.Main
```

This project intentionally has no HTTP server. The requested commerce behavior is exposed through the beginner-friendly `ShopService` API.

## Java concepts practiced

- Object-oriented models, encapsulation, and defensive copies
- `List` and `Map` collections
- `BigDecimal` money calculations
- Validation and guarded state transitions
- All-or-nothing checkout sequencing
- Stock restoration and overflow protection
- Synchronized in-memory service methods

## Backend concepts practiced

- Catalog, cart, inventory, and order service boundaries
- All-or-nothing validation before state changes
- Money calculations with `BigDecimal`
- Stock reservation checks and terminal order states

## Storage approach

Products, active carts, and orders use synchronized in-memory maps. Orders retain immutable item and total snapshots, but no state survives a program restart.

## Limitations

- No persistent storage, customers, payments, taxes, or shipping
- Cart checks do not reserve stock across different carts before checkout
- Product prices and stock cannot currently be edited through `ShopService`
- Status workflow is intentionally limited to CREATED, PAID, and CANCELLED

## Possible future improvements

- Partial quantity removal and cart quantity replacement
- Product updates and categories
- Discounts, taxes, and shipping calculations
- Customer accounts and order ownership
- File persistence
- Optional HTTP adapter and automated tests
