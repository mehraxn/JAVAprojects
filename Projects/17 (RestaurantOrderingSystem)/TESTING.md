# Testing Restaurant Ordering System

## Testing approach

Use known BigDecimal prices and quantities so subtotals, discount thresholds, and totals can be checked exactly.

## Normal test cases

| Test | Action | Expected result |
|---|---|---|
| Add menu | Add unique items | Items appear in listMenu |
| Create order | Create named and walk-in orders | Unique IDs are generated |
| Add item | Add one menu item | Order contains quantity and subtotal |
| Increase quantity | Add same item again | Existing quantity increases |
| Remove item | Remove existing item | Totals update |
| Status workflow | Advance created, preparing, ready, served | Every valid transition succeeds |

## Edge-case test cases

| Test | Action | Expected result |
|---|---|---|
| Discount boundary | Subtotal exactly 50.00 | 10% discount is applied |
| Below boundary | Subtotal below 50.00 | Discount is zero |
| Free item | Add item priced 0 | Item is accepted |
| Empty order | Move to PREPARING | IllegalStateException |
| Shared item request | Add same ID multiple times | One OrderItem with combined quantity |
| Cancel | Cancel from CREATED or PREPARING | Status becomes CANCELLED |

## Invalid input test cases

| Test | Action | Expected result |
|---|---|---|
| Duplicate menu | Reuse ID or case-insensitive name | IllegalArgumentException |
| Invalid money | Use negative or null price | IllegalArgumentException |
| Invalid quantity | Use zero, negative, or overflowing quantity | IllegalArgumentException |
| Unknown item/order | Use missing ID | IllegalArgumentException |
| Remove missing item | Remove item not in order | IllegalArgumentException |
| Invalid transition | Skip/reverse status or change final status | IllegalStateException |
| Edit final order | Add or remove after CREATED | IllegalStateException |

## Expected results

Item quantities, subtotal, discount, total, and status must agree. Rejected edits or transitions must preserve the existing order.

## Manual testing checklist

- [ ] Compile and run Main.
- [ ] Verify item subtotals and order subtotal.
- [ ] Test discount immediately below, at, and above 50.00.
- [ ] Verify empty orders cannot be prepared.
- [ ] Verify cancellation and normal status paths.
- [ ] Verify rejected edits preserve order contents and status.
- [ ] Verify returned lists cannot be modified.
