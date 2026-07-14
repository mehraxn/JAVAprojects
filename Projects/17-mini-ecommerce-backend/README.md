# Mini E-Commerce Backend

## Overview

This educational Java project models a small in-memory commerce backend with products, carts, checkout, stock, orders, payment-state transitions, and cancellation. It is a service-layer/CLI application rather than an HTTP API.

## Features

- Maintain a product catalog with unique IDs/names, positive prices, and stock.
- Add products to carts and merge repeated quantities.
- Calculate cart totals with `BigDecimal`.
- Validate a complete cart before changing stock.
- Create immutable order snapshots during checkout.
- Mark created orders as paid or cancel them with stock restoration.
- Preserve state when checkout validation fails.
- Provide focused CLI demonstrations for catalog, checkout, cancellation, and failure cases.

## What This Project Demonstrates

- OOP domain modeling and service-layer ownership.
- Collections, validation, and defensive copies.
- `BigDecimal` money handling.
- Atomic in-memory checkout and rollback-style stock restoration.
- Explicit order-state transitions and dependency-free automated testing.

## Tech Stack

- Java 21 standard library.
- Plain `javac`/`java`; no Maven or external dependencies.
- Dependency-free custom tests.
- Bash and PowerShell validation scripts.

## Architecture / Design

`Product`, `Cart`, and `Order` model the domain. `ShopService` owns the catalog, carts, checkout, stock changes, cancellation, and order transitions; `Main` provides testable CLI demonstrations. No repository or transport layer is included because all state is local and in memory.

## Project Structure

```text
.
├── src/miniecommercebackend/     # Domain, ShopService, CLI
├── tests/miniecommercebackend/   # Custom tests and runner
├── scripts/                      # test.sh and test.ps1
├── TESTING.md
└── TEST_RESULTS.md
```

## How to Run

```bash
javac -Xlint:all -Werror -d out src/miniecommercebackend/*.java
java -cp out miniecommercebackend.Main demo
```

Other commands include `help`, `catalog-demo`, `checkout-demo`, `cancel-demo`, and `failure-demo`.

## Testing

```bash
bash scripts/test.sh
```

Windows PowerShell:

```powershell
.\scripts\test.ps1
```

See [TESTING.md](TESTING.md) for exact commands and [TEST_RESULTS.md](TEST_RESULTS.md) for the latest recorded validation results.

## Known Limitations

- In-memory educational application with no database or file persistence.
- No REST API, authentication, users, payment gateway, shipping provider, or external inventory service.
- Cart additions do not reserve stock between carts.
- No production concurrency or distributed-transaction guarantees.

## Resume Value

Built a Java commerce service with validated catalogs and carts, atomic checkout, precise totals, stock restoration on cancellation, defensive order snapshots, CLI workflows, and automated tests.
