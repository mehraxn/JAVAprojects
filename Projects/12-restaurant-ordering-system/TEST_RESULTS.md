# Test Results

Date: 2026-07-12

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK 21.0.11 (Microsoft build), Windows 11 |
| Strict application compile | PASS | `javac -Xlint:all -Werror -d out src/restaurantorderingsystem/*.java` |
| Strict test compile | PASS | `javac -Xlint:all -Werror -cp out -d test-out tests/restaurantorderingsystem/*.java` |
| Automated tests | PASS | 137 checks, 0 failures (`restaurantorderingsystem.TestRunner`) |
| MenuItem tests | PASS | 19 checks: validation (price > 0), BigDecimal scale, immutability |
| OrderItem tests | PASS | 16 checks: quantity validation, immutable line, overflow, line totals |
| Order tests | PASS | 44 checks: merging, subtotal/discount/total, lifecycle, defensive items |
| Restaurant tests | PASS | 28 checks: menu/order workflows, defensive snapshots, end-to-end |
| Defensive copy tests | PASS | Returned MenuItem/OrderItem/Order data cannot mutate internal state (in the Order and Restaurant suites) |
| Main CLI tests | PASS | 30 checks: help/demo/order/discount/status/validation, unknown commands |
| Main demo | PASS | `demo` exits 0 and runs the lifecycle to SERVED |
| Discount demo | PASS | Below/exact/above boundary, money formatted to 2 decimals |
| Status demo | PASS | Valid transitions plus empty-order and invalid-transition rejections |
| scripts/test.sh | PASS | Full pipeline in Git Bash (compile, tests, demos, cleanup) |
| scripts/test.ps1 | PASS | Full pipeline in Windows PowerShell 5.1 |

## Known limitations

- In-memory restaurant ordering system only — no database.
- No HTTP API and no authentication/users.
- No kitchen display system.
- No payment integration.
- No inventory/stock management.
- No tax or service-charge system.
- No production POS guarantees.
- Intended as a Java OOP/business-logic learning project.

## Notes

- Tests were run on Windows 11 with the classpath separator `;`. On Linux/macOS use `out:test-out`.
- The suite is pure in-memory logic; no files or processes are created.
