# Test Results

Date: 2026-07-11

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | NOT RUN | Pending validation |
| Strict application compile | NOT RUN | `javac -Xlint:all -Werror` |
| Strict test compile | NOT RUN | `javac -Xlint:all -Werror` |
| Automated tests | NOT RUN | Pending test runner execution |
| Main demo | NOT RUN | `Main demo` |
| Checkout demo | NOT RUN | Successful checkout |
| Cancel demo | NOT RUN | Stock restoration |
| Failure demo | NOT RUN | Invalid/insufficient stock behavior |
| Defensive copy tests | NOT RUN | Product/cart/order snapshots |
| BigDecimal total tests | NOT RUN | Money calculation |

## Known limitations

- In-memory backend only.
- No database.
- No HTTP API.
- No authentication/users.
- No payment provider.
- No shipping workflow.
- No persistence.
- Intended as a Java OOP/backend-logic learning project.
