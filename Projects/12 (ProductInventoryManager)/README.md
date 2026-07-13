# Product Inventory Manager

An educational, dependency-free Java project that models a product inventory
manager. It focuses on clean object-oriented design, a service layer with real
business rules, `BigDecimal` money handling, defensive data exposure, and
dependency-free automated tests — not on a database, web API, or barcode/warehouse
integration.

## What it demonstrates

- **Product** domain model (SKU, name, category, price, quantity, reorder threshold)
- **Inventory** service layer that owns all state changes
- SKU-based product tracking (case-insensitive)
- product validation
- stock adjustment safeguards (underflow and overflow rejected)
- search by SKU and name
- sorting by name / price / quantity, ascending and descending
- low-stock reporting
- out-of-stock reporting
- inventory valuation with `BigDecimal`
- inventory value by category and highest-value reports
- defensive `ProductSnapshot` copies so internal state cannot be mutated from outside
- command-based CLI demos
- dependency-free automated tests
- strict compilation (`-Xlint:all -Werror`)

## Features

- Add products and reject duplicate SKUs.
- Find, remove, and list products.
- Search products by SKU or name (case-insensitive).
- Sort products by name, price, or quantity in either direction.
- Increase/decrease stock and set absolute stock, with underflow/overflow protection.
- Low-stock report (per-product reorder threshold).
- Out-of-stock report.
- Total inventory value, value by category, and highest-value products.
- CLI demos for every feature area.

## Main classes

| Class | Responsibility |
|---|---|
| `Product` | Immutable-from-outside product; only stock quantity changes, via `Inventory`. |
| `ProductSnapshot` | Immutable read-only view returned to callers (includes inventory value). |
| `ProductSortField` | Enum: `NAME`, `PRICE`, `QUANTITY`. |
| `Inventory` | Service layer owning products, stock changes, search, sorting, and reports. |
| `Main` | Command-based CLI / demo driver. |

## Behavior notes

- **SKU uniqueness is case-insensitive**: `p100` and `P100` are the same product.
  Lookups, removal, and stock adjustments are also case-insensitive.
- **Unit price must be greater than zero.** Free products are not modelled.
- **Money uses `BigDecimal`** (never `double`) to avoid floating-point rounding
  errors; comparisons use `compareTo`, totals use `add`/`multiply`. User-facing
  output is shown with 2 decimals.
- **Low-stock rule is `quantity <= reorderThreshold`** (inclusive), using each
  product's own reorder threshold.
- **Out-of-stock rule is `quantity == 0`.**
- **Stock safeguards**: adjustments that would make quantity negative (underflow)
  or exceed `Integer.MAX_VALUE` (overflow) are rejected and leave state unchanged.
  Adjusting by zero is an allowed no-op.
- **Public methods return `ProductSnapshot` copies in unmodifiable lists.** Live
  `Product` objects are never leaked, so callers cannot mutate inventory state.

## Quick start

Compile:

~~~
javac -Xlint:all -Werror -d out src/productinventorymanager/*.java
~~~

Run the CLI commands:

~~~
java -cp out productinventorymanager.Main help
java -cp out productinventorymanager.Main demo
java -cp out productinventorymanager.Main stock-demo
java -cp out productinventorymanager.Main search-demo
java -cp out productinventorymanager.Main sort-demo
java -cp out productinventorymanager.Main report-demo
java -cp out productinventorymanager.Main validation-demo
~~~

## Testing

The project ships with a dependency-free test suite (custom assertion helper and
runner — no JUnit, Maven, or Gradle). See [TESTING.md](TESTING.md) for exact
commands and [TEST_RESULTS.md](TEST_RESULTS.md) for the latest recorded run.

## Limitations

This is a learning project. It intentionally has:

- no database (in-memory only)
- no HTTP API
- no login/authentication
- no barcode scanner
- no supplier / purchase-order workflow
- no warehouse / location tracking
- no production inventory guarantees
- no production deployment
