# Testing Restaurant Ordering System

The project has no external test dependencies. Compile and run `Main`, or call the classes from a small Java driver.

## Manual test cases

1. Add several uniquely identified menu items and verify `listMenu()`.
2. Add a duplicate menu item ID or negative price; expect `IllegalArgumentException`.
3. Create named and walk-in orders and verify unique generated order IDs.
4. Add one item, then add it again; verify the quantity increases.
5. Add several different items and verify each item subtotal and the order subtotal.
6. Remove an existing item and verify totals update.
7. Remove an item not present in the order; expect `IllegalArgumentException`.
8. Use zero, negative, or overflowing quantities; expect `IllegalArgumentException`.
9. Verify subtotal below `50.00` receives no discount.
10. Verify subtotal exactly `50.00` or above receives a 10% discount.
11. Verify the summary contains customer, items, subtotal, discount, total, and status.
12. Follow `CREATED -> PREPARING -> READY -> SERVED` and verify each status.
13. Cancel from `CREATED` or `PREPARING` and verify the final status.
14. Try an invalid status transition or editing a non-created order; expect `IllegalStateException`.
15. Use an unknown menu item or order ID; expect `IllegalArgumentException`.
16. Try modifying returned menu, order, or item lists; expect `UnsupportedOperationException`.

## Validation review additions

- Add different menu item IDs with the same case-insensitive name; expect `IllegalArgumentException`.
- Move an empty order to `PREPARING`; expect `IllegalStateException`.
- Verify rejected empty-order or status transitions preserve the original order status.
- Verify zero-priced menu items are allowed, while negative or null prices remain rejected.
