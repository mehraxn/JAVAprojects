# Test Results

Date: 2026-07-11

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK 21.0.11 LTS |
| Strict application compile | PASS | `javac -Xlint:all -Werror` |
| Strict test compile | PASS | `javac -Xlint:all -Werror` |
| Automated tests | PASS | 100 checks across 5 suites |
| Main demo | PASS | `Main demo` completed with paid order summary |
| Checkout demo | PASS | Successful checkout and order creation |
| Cancel demo | PASS | Stock restored from 3 to 5 |
| Failure demo | PASS | Insufficient stock handled; cart unchanged |
| Defensive copy tests | PASS | Product, cart, order, catalog, and order-list snapshots |
| BigDecimal total tests | PASS | Exact cart and order decimal totals |

## Known limitations

- In-memory backend only.
- No database.
- No HTTP API.
- No authentication/users.
- No payment provider.
- No shipping workflow.
- No persistence.
- Intended as a Java OOP/backend-logic learning project.
