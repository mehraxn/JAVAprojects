# Testing the Bank Account Simulator

The project uses a small, dependency-free test harness: a custom assertion
helper (`TestSupport`, including `assertBigDecimalEquals`) and a runner
(`TestRunner`). No JUnit, Maven, Gradle, or other external libraries are
involved. Tests live in `tests/bankaccountsimulator/` and share the source
package, so they can exercise package-private behaviour directly.

## What is covered

- `TransactionTypeTest` — enum values.
- `AccountTest` — validation, deposit/withdraw, overdraft, snapshot.
- `TransactionTest` — validation, immutability, snapshot.
- `BankTest` — accounts, deposit, withdraw, reports, deterministic IDs/clock.
- `TransferTest` — all-or-nothing transfer behaviour (the most important file).
- `SnapshotTest` — unmodifiable results and proof that returned data cannot mutate
  internal bank state.
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
javac -Xlint:all -Werror -d out src/bankaccountsimulator/*.java
~~~

### C) Strict compile: tests

~~~
javac -Xlint:all -Werror -cp out -d test-out tests/bankaccountsimulator/*.java
~~~

### D) Run tests

Linux/macOS/Git Bash:

~~~
java -cp "out:test-out" bankaccountsimulator.TestRunner
~~~

Windows PowerShell:

~~~
java -cp "out;test-out" bankaccountsimulator.TestRunner
~~~

### E) Run CLI demos

~~~
java -cp out bankaccountsimulator.Main help
java -cp out bankaccountsimulator.Main demo
java -cp out bankaccountsimulator.Main deposit-demo
java -cp out bankaccountsimulator.Main withdraw-demo
java -cp out bankaccountsimulator.Main transfer-demo
java -cp out bankaccountsimulator.Main statement-demo
java -cp out bankaccountsimulator.Main validation-demo
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

> Note: the JVM classpath separator differs by platform — `:` on Linux/macOS,
> `;` on Windows. `test.sh` uses `:` and `test.ps1` uses `;` accordingly.

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
- [ ] Verify deposit/withdraw update balance and record `balanceAfter`.
- [ ] Verify overdraft and zero/negative amounts are rejected.
- [ ] Verify transfers create `TRANSFER_OUT` + `TRANSFER_IN` with related accounts.
- [ ] Verify all-or-nothing: a failed transfer changes no balance or history.
- [ ] Verify transaction IDs are deterministic (`T0001`, `T0002`, …).
- [ ] Verify a fixed `Clock` yields deterministic timestamps.
- [ ] Verify returned lists are unmodifiable and snapshots cannot mutate state.
