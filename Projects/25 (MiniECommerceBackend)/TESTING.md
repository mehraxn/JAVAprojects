# Mini E-Commerce Backend Testing

These manual tests exercise the public model and `ShopService` APIs. No server or database is required.

## Normal test cases

| Test | Action | Expected result |
|---|---|---|
| Add products | Add two valid unique products | Both appear in insertion order |
| Search | Search partial ID or name with different casing | Matching products returned |
| Create carts | Create two carts | IDs `CART-1` and `CART-2` |
| Add to cart | Add quantity 2, then 1 of same product | Cart stores quantity 3 |
| Remove item | Remove an existing product line | Returns `true`; item disappears |
| Calculate total | Add 2 items at `12.50` and 1 at `79.90` | Total is `104.90` |
| Place order | Checkout a valid nonempty cart | Order created, stock reduced, cart removed |
| Mark paid | Change created order to `PAID` | Status updates and stock remains reduced |
| Cancel order | Cancel a created order | Status updates and stock is restored once |
| List orders | Place several orders | Immutable snapshots appear in order history |

## Edge-case and invalid input test cases

| Test | Input or action | Expected result |
|---|---|---|
| Duplicate product | Add the same product ID twice | Rejected |
| Blank product fields | Empty ID or name | Rejected |
| Invalid price | Null, zero, or negative price | Rejected |
| Invalid stock | Negative initial stock | Rejected |
| Stock overflow | Increase beyond `Integer.MAX_VALUE` | Rejected |
| Unknown product/cart | Use absent ID | Rejected |
| Invalid quantity | Add zero or negative quantity | Rejected |
| Excess quantity | Cart total quantity exceeds product stock | Rejected without changing cart |
| Competing carts | Two carts request stock; first checks out | Second checkout revalidates and fails if stock is insufficient |
| Empty checkout | Place order from empty cart | Rejected; no order created |
| Failed multi-item checkout | One item lacks stock | No stock changes and no order is created |
| Repeated checkout | Reuse cart ID after successful order | Rejected because cart was removed |
| Missing removal | Remove product not in cart | Returns `false` |
| Unknown order update | Update absent valid order ID | Returns `false` |
| Null status | Update order with null | Rejected |
| Invalid transition | Change paid/cancelled order again | `IllegalStateException` |
| Repeat cancellation | Set cancelled order to cancelled again | No extra stock restoration |
| Returned collection mutation | Modify product/order lists or item maps | Mutation is rejected or stored state remains unchanged |

## Manual testing checklist

- [ ] Compile all source files.
- [ ] Run `Main` and verify total, order, and remaining stock output.
- [ ] Add and search several products.
- [ ] Create carts, add repeated items, and remove an item.
- [ ] Verify totals using decimal prices.
- [ ] Place an order and confirm exact stock reduction.
- [ ] Cancel a created order and confirm exact one-time restoration.
- [ ] Mark another order paid and confirm it cannot be cancelled.
- [ ] Test empty, insufficient-stock, and competing-cart checkout failures.
- [ ] Confirm failed checkout leaves all state unchanged.

## Phase 2 validation review additions

| Test | Action | Expected result |
|---|---|---|
| Null status on missing order | Update an unknown order using null status | Null status is rejected before the not-found result |
| Zero-stock product | Add any positive quantity of a zero-stock product | Rejected and cart remains unchanged |
| Cancellation overflow | Attempt restoration beyond integer stock capacity | Entire cancellation is rejected before any stock changes |
