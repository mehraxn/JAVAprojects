# Testing Mini E-Commerce Backend

## Planned service tests

- Add and list products.
- Create carts and change item quantities.
- Calculate checkout totals.
- Reduce stock exactly once.
- List completed orders.

## Planned HTTP tests

- Test product, cart, and checkout endpoint methods.
- Verify validation and not-found status codes.
- Verify manually created response strings.

## Planned validation tests

- Reject duplicate products.
- Reject blank IDs and invalid money values.
- Reject zero or negative quantities.
- Reject insufficient stock and empty checkout.
- Ensure failed checkout leaves stock and order history unchanged.

## Manual checklist

- [ ] Implement product and cart validation.
- [ ] Make checkout all-or-nothing.
- [ ] Implement read-only result collections.
- [ ] Implement optional HttpServer routes.
- [ ] Start the server only in explicit server mode.
