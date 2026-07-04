# Restaurant Ordering System

An in-memory Java application for menu items, customer orders, discounts, and order status.

## Implemented features

- Add and remove uniquely identified menu items.
- Create customer or walk-in orders with generated IDs.
- Add new items or increase an existing item's quantity.
- Remove items while an order is editable.
- Calculate item subtotals and order subtotal.
- Apply a 10% discount when subtotal is at least `50.00`.
- Calculate final total and produce a readable order summary.
- Enforce simple order status transitions.
- Prevent editing after an order leaves `CREATED` status.

## Structure

- `MenuItem` stores menu identity and price.
- `OrderItem` combines a menu item and positive quantity.
- `Order` owns items, discount calculation, summary, and status.
- `Restaurant` manages the menu and customer orders.
- `Main` demonstrates ordering, discount, summary, and status updates.

Source files are under `src/restaurantorderingsystem` and use only standard Java.

## Run

```powershell
javac -d out src\restaurantorderingsystem\*.java
java -cp out restaurantorderingsystem.Main
```

See `TESTING.md` for manual test cases.
