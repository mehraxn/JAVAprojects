# Test Results

Date: 2026-07-13

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK 21.0.11 / javac 21.0.11 |
| Strict application compile | PASS | `javac -Xlint:all -Werror -d out src/productinventorymanager/*.java` |
| Strict test compile | PASS | `javac -Xlint:all -Werror -cp out -d test-out tests/productinventorymanager/*.java` |
| Automated tests | PASS | 68 / 68 test cases passed, 111 assertion checks |
| Product tests | PASS | Validation, category, reorder threshold, BigDecimal value, final class behavior |
| Inventory tests | PASS | Add/find/remove, stock changes, search, sorting, reports, failed-operation safety |
| Stock tests | PASS | Increase, decrease, set stock, underflow protection, overflow protection, missing SKU behavior |
| Search/sort tests | PASS | Case-insensitive SKU/name search; name, price, quantity sorting; ascending and descending ordering |
| Report tests | PASS | Low-stock, out-of-stock, total value, category value, highest-value products |
| Defensive snapshot tests | PASS | ProductSnapshot decouples public results from internal mutable Product state; returned collections/maps are unmodifiable |
| Main CLI tests | PASS | help, demo, stock-demo, search-demo, sort-demo, report-demo, validation-demo, invalid command handling |
| Main demo | PASS | `java -cp out productinventorymanager.Main demo` |
| Stock demo | PASS | `java -cp out productinventorymanager.Main stock-demo` |
| Search demo | PASS | `java -cp out productinventorymanager.Main search-demo` |
| Sort demo | PASS | `java -cp out productinventorymanager.Main sort-demo` |
| Report demo | PASS | `java -cp out productinventorymanager.Main report-demo` |
| Validation demo | PASS | `java -cp out productinventorymanager.Main validation-demo` |

## Validation command used

```bash
bash scripts/test.sh
```

The script performed strict application compilation, strict test compilation, automated test execution, CLI demo smoke tests, and final cleanup of generated build outputs.

## Known limitations

- In-memory product inventory manager only.
- No database.
- No HTTP API.
- No authentication/users.
- No barcode scanning.
- No supplier or purchase-order workflow.
- No warehouse/location tracking.
- No production inventory guarantees.
- Intended as a Java OOP/collection-management learning project.
