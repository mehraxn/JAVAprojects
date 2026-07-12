# Restaurant Ordering System Testing

All commands run from the project root. The automated tests need only a JDK — no database, network, or files.

## A) Clean

Linux/macOS/Git Bash:

```text
rm -rf out test-out
```

Windows PowerShell:

```text
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
```

## B) Strict compile: application

```text
javac -Xlint:all -Werror -d out src/restaurantorderingsystem/*.java
```

## C) Strict compile: tests

```text
javac -Xlint:all -Werror -cp out -d test-out tests/restaurantorderingsystem/*.java
```

## D) Run the automated tests

Linux/macOS:

```text
java -cp "out:test-out" restaurantorderingsystem.TestRunner
```

Windows (PowerShell or Git Bash — Windows Java uses `;`):

```text
java -cp "out;test-out" restaurantorderingsystem.TestRunner
```

The runner prints per-suite PASS/FAIL counts and a final summary, and exits 0 only if every check passes.

### What the suites cover

| Suite | Coverage |
|---|---|
| `MenuItemTest` | ID/name/price validation (price > 0), BigDecimal scale, immutability (final, no setters) |
| `OrderItemTest` | Quantity validation, immutable line, `withAdditionalQuantity` returning a new instance, overflow, line totals |
| `OrderTest` | Item merging, subtotal/discount/total, discount boundary (below/exact/above), full lifecycle and invalid transitions, unmodifiable items, defensive behavior, 2-decimal money |
| `RestaurantTest` | Menu management, duplicate ID/name rejection, sequential order IDs, unknown lookups, defensive order snapshots, status updates, cancel, end-to-end workflow |
| `MainTest` | Exit codes and output for `help`, `demo`, `order-demo`, `discount-demo`, `status-demo`, `validation-demo`, no-args default, and unknown commands |

## E) Run the CLI demos

```text
java -cp out restaurantorderingsystem.Main help
java -cp out restaurantorderingsystem.Main demo
java -cp out restaurantorderingsystem.Main order-demo
java -cp out restaurantorderingsystem.Main discount-demo
java -cp out restaurantorderingsystem.Main status-demo
java -cp out restaurantorderingsystem.Main validation-demo
```

All of these must exit 0 (the validation-demo failures are intentional demonstrations). `java -cp out restaurantorderingsystem.Main bogus` must print an error to stderr and exit non-zero.

## F) Scripts

Linux/macOS/Git Bash:

```text
./scripts/test.sh
```

Windows PowerShell:

```text
.\scripts\test.ps1
```

Both scripts clean, strict-compile the app and tests, run the full test suite, run all five demo commands, and remove `out/` and `test-out/` afterward.

## G) Cleanup

Linux/macOS/Git Bash:

```text
rm -rf out test-out
```

Windows PowerShell:

```text
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
```

## Manual edge cases worth trying

- `new MenuItem("M1", "Free", BigDecimal.ZERO)` → `IllegalArgumentException` (price must be > 0)
- Add a menu item whose name matches an existing one (any case) → `IllegalArgumentException`
- Add the same menu item to an order twice → one merged line, quantities summed
- Subtotal of exactly `50.00` → 10% discount applies (inclusive threshold)
- Move an empty order to PREPARING → `IllegalStateException`
- Add an item after PREPARING → `IllegalStateException`
- Cancel a SERVED order → `IllegalStateException`
- Mutate an order returned by `findOrder`/`listOrders`/`createOrder` → the restaurant's live order is unchanged
