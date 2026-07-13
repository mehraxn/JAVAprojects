# Testing the Product Inventory Manager

The project uses a small, dependency-free test harness: a custom assertion
helper (`TestSupport`, including `assertBigDecimalEquals`) and a runner
(`TestRunner`). No JUnit, Maven, Gradle, or other external libraries are
involved. Tests live in `tests/productinventorymanager/` and share the source
package, so they can exercise package-private behaviour directly.

## What is covered

- `ProductTest` — validation, defaults, `BigDecimal` price, low-stock boundary.
- `ProductSortFieldTest` — enum values.
- `InventoryTest` — product management, stock safeguards, search, sorting,
  reports, and unchanged state after failures (the most important file).
- `ProductSnapshotTest` — snapshot fields/value, unmodifiable results, and proof
  that returned data cannot mutate internal inventory state.
- `MainTest` — `Main.run` smoke tests, called in-process (no separate JVM).

## Commands

### A) Clean

Linux/macOS/Git Bash:

~~~
rm -rf out test-out
~~~

Windows PowerShell:

~~~
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
~~~

### B) Strict compile: application

~~~
javac -Xlint:all -Werror -d out src/productinventorymanager/*.java
~~~

### C) Strict compile: tests

~~~
javac -Xlint:all -Werror -cp out -d test-out tests/productinventorymanager/*.java
~~~

### D) Run tests

Linux/macOS/Git Bash:

~~~
java -cp "out:test-out" productinventorymanager.TestRunner
~~~

Windows PowerShell:

~~~
java -cp "out;test-out" productinventorymanager.TestRunner
~~~

### E) Run CLI demos

~~~
java -cp out productinventorymanager.Main help
java -cp out productinventorymanager.Main demo
java -cp out productinventorymanager.Main stock-demo
java -cp out productinventorymanager.Main search-demo
java -cp out productinventorymanager.Main sort-demo
java -cp out productinventorymanager.Main report-demo
java -cp out productinventorymanager.Main validation-demo
~~~

### F) Scripts

Linux/macOS/Git Bash:

~~~
./scripts/test.sh
~~~

Windows PowerShell:

~~~
.\scripts\test.ps1
~~~

### G) Cleanup

Linux/macOS/Git Bash:

~~~
rm -rf out test-out
~~~

Windows PowerShell:

~~~
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
~~~

## Manual testing checklist

- [ ] Compile strictly with `-Xlint:all -Werror`.
- [ ] Run `TestRunner` and confirm all cases pass.
- [ ] Test absolute and relative stock updates.
- [ ] Verify underflow/overflow are rejected and leave quantity unchanged.
- [ ] Verify all three sort fields in both directions.
- [ ] Test low-stock values below, equal to, and above threshold.
- [ ] Recalculate total value after stock changes and removal.
- [ ] Verify returned lists are unmodifiable and snapshots cannot mutate inventory.
