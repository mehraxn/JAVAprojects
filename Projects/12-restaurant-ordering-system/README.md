# Restaurant Ordering System

## Overview

An educational Java restaurant ordering system focused on object-oriented design and business logic, built entirely with the Java standard library. No framework, database, or external dependency — plain `javac`/`java` is enough to build, run, and test it.

## What This Project Demonstrates

- A `MenuItem` domain model and `OrderItem` composition (menu item + quantity)
- `Restaurant` menu/order management with a clean service layer
- Order lifecycle/status transitions (`CREATED → PREPARING → READY → SERVED`, plus cancellation)
- Quantity merging when the same item is added twice
- `BigDecimal` money handling for subtotal / discount / total (never `double`)
- A configurable-free but clearly documented discount rule
- **Defensive design**: immutable `MenuItem`/`OrderItem`, defensive `Order` snapshots from the restaurant
- Dependency-free automated tests (custom assertion helper + test runner)
- Strict compilation with `-Xlint:all -Werror`

## Features

- Add menu items (duplicate ID and duplicate name rejected)
- Create orders with sequential IDs and add items
- Merge repeated menu-item quantities into a single line
- Calculate subtotal, apply the discount rule, and calculate the total
- Move an order through its status lifecycle
- Validate invalid operations cleanly
- Command-based CLI demos

## Main classes

- `MenuItem` — immutable menu item (id, name, `BigDecimal` price > 0).
- `OrderItem` — immutable line (menu item + quantity); "changing" quantity yields a new instance.
- `OrderStatus` — lifecycle enum (`CREATED`, `PREPARING`, `READY`, `SERVED`, `CANCELLED`).
- `Order` — owns items and status, calculates money, enforces the lifecycle.
- `Restaurant` — owns the menu and orders; hands out defensive order snapshots.
- `Money` — small helper for 2-decimal `BigDecimal` formatting.
- `Main` — CLI commands (`help`, `demo`, `order-demo`, `discount-demo`, `status-demo`, `validation-demo`).

## Money and the discount rule

All money is `BigDecimal`, never `double`, because binary floating point cannot represent most decimal fractions exactly (`0.1 + 0.2 != 0.3`). Totals use `add`/`multiply`/`subtract` and comparisons use `compareTo`; every amount is normalized to 2 decimals (half-up) so results are deterministic and user-facing output reads `46.80`, not `46.8000`.

**Discount rule:** an order whose subtotal is **`50.00` or more** receives a **10% discount**. The threshold is **inclusive** — a subtotal of exactly `50.00` qualifies. Below `50.00` there is no discount. `total = subtotal − discount`.

## Order lifecycle

```text
CREATED ──▶ PREPARING ──▶ READY ──▶ SERVED
   │            │
   └──▶ CANCELLED ◀──┘
```

- Only `CREATED` orders are editable (items can be added or removed).
- A `CREATED` order needs at least one item to move to `PREPARING` — empty orders are rejected.
- `PREPARING` orders cannot have items changed.
- `CREATED` and `PREPARING` orders can be `CANCELLED`.
- `SERVED` and `CANCELLED` are terminal; `READY` and `SERVED` cannot be cancelled.
- Backwards and skipping transitions are rejected.

## Tech Stack

- Java 21 standard library.
- Plain `javac`/`java`; no Maven, Gradle, or external dependencies.
- `BigDecimal` for monetary calculations.
- Dependency-free tests plus Bash and PowerShell validation scripts.

## Project Structure

```text
.
├── src/restaurantorderingsystem/     # Domain, service, money helper, CLI
├── tests/restaurantorderingsystem/   # Custom tests and test runner
├── scripts/                           # Cross-platform validation scripts
├── TESTING.md
└── TEST_RESULTS.md
```

## How to Run

```text
javac -Xlint:all -Werror -d out src/restaurantorderingsystem/*.java

java -cp out restaurantorderingsystem.Main help
java -cp out restaurantorderingsystem.Main demo
java -cp out restaurantorderingsystem.Main order-demo
java -cp out restaurantorderingsystem.Main discount-demo
java -cp out restaurantorderingsystem.Main status-demo
java -cp out restaurantorderingsystem.Main validation-demo
```

Running with no command prints the usage text. `demo` is the full walkthrough; the others focus on ordering/merging, the discount boundary, the status lifecycle, and validation. `validation-demo` intentionally triggers failures and exits 0 because the rejections are the point. `Main.run(args, out, err)` returns an exit code (0 for valid commands, non-zero for unknown ones) and only `main` calls `System.exit`.

## Defensive design

- `MenuItem` and `OrderItem` are immutable and `final`, so handing them out is safe.
- `Order.getItems()` returns an unmodifiable list of immutable items — external code cannot mutate the order through them.
- `Restaurant.findOrder`, `listOrders`, and `createOrder` return **defensive snapshot copies** of orders. Mutating a returned `Order` never changes the restaurant's live state; all real changes go through `Restaurant` methods (`addItemToOrder`, `updateOrderStatus`, `cancelOrder`). The automated tests prove this.

## Testing

The project ships with dependency-free automated tests (custom `Assert` helper including `assertBigDecimalEquals`, plus a `TestRunner`) covering the model, order, restaurant, money/discount logic, defensive copies, and CLI:

```text
javac -Xlint:all -Werror -cp out -d test-out tests/restaurantorderingsystem/*.java
java -cp "out;test-out" restaurantorderingsystem.TestRunner   # Windows (use out:test-out on Linux/macOS)
```

Or run everything with one script: `./scripts/test.sh` (Linux/macOS/Git Bash) or `.\scripts\test.ps1` (Windows PowerShell). See [TESTING.md](TESTING.md) for the full procedure and [TEST_RESULTS.md](TEST_RESULTS.md) for the latest recorded results.

## Java concepts practiced

- Classes, encapsulation, immutability, and final domain types
- `List`, `Map`, and composition (`Order` → `OrderItem` → `MenuItem`)
- `BigDecimal` money arithmetic and 2-decimal formatting
- Defensive copies and unmodifiable collection views
- Lifecycle/state-machine validation
- Exit codes and testable CLI entry points

## Known Limitations

- In-memory only — no database and no persistence
- No HTTP API, login, or authentication
- No payment integration
- No real kitchen-display or inventory/stock system
- No tax or service-charge system
- Not a production POS — this is a Java OOP/business-logic learning project

## Possible future improvements

- Tax and service-charge calculation
- Per-item notes and modifiers
- Multiple discount rules and coupons
- Persistence of menu and orders

## Resume Value

Built a dependency-free Java restaurant-ordering system with precise money calculations, line-item merging, discounts, validated order-state transitions, defensive snapshots, CLI demonstrations, and automated tests.
