# Test Results

Date: 2026-07-13

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK 21.0.11 / javac 21.0.11 |
| Strict application compile | PASS | `javac -Xlint:all -Werror -d out src/bankaccountsimulator/*.java` |
| Strict test compile | PASS | `javac -Xlint:all -Werror -cp out -d test-out tests/bankaccountsimulator/*.java` |
| Automated tests | PASS | 74 / 74 test cases passed, 155 assertion checks |
| Account tests | PASS | Validation, deposit/withdraw, overdraft, transaction history, snapshot |
| Transaction tests | PASS | IDs, type, amount, timestamp, balanceAfter, related account, snapshot |
| Bank tests | PASS | Accounts, deposit, withdraw, reports, deterministic IDs and fixed Clock |
| Transfer tests | PASS | All-or-nothing transfer behavior, TRANSFER_OUT/IN, related accounts |
| BigDecimal money tests | PASS | Decimal-safe balances and totals via `assertBigDecimalEquals` |
| Defensive snapshot tests | PASS | Unmodifiable results; snapshots cannot mutate internal account state |
| Main CLI tests | PASS | help/demo/deposit/withdraw/transfer/statement/validation + invalid command |
| Main demo | PASS | `java -cp out bankaccountsimulator.Main demo` (exit 0) |
| Deposit demo | PASS | `java -cp out bankaccountsimulator.Main deposit-demo` (exit 0) |
| Withdraw demo | PASS | `java -cp out bankaccountsimulator.Main withdraw-demo` (exit 0) |
| Transfer demo | PASS | `java -cp out bankaccountsimulator.Main transfer-demo` (exit 0) |

## Validation commands used

Windows PowerShell (this machine):

```powershell
.\scripts\test.ps1
```

Equivalent POSIX shells (Linux/macOS/Git Bash):

```bash
bash scripts/test.sh
```

`test.ps1` was run end-to-end on Windows (exit 0): strict application compile,
strict test compile, automated test execution (74/74), all six CLI demos, and
cleanup of generated build output. The scripts differ only in the JVM classpath
separator (`;` on Windows, `:` on POSIX shells).

## Known limitations

- In-memory bank account simulator only.
- No database.
- No HTTP API.
- No authentication/users.
- No real banking integration.
- No interest calculation.
- No concurrency/thread-safety guarantee.
- No production financial guarantees.
- Intended as a Java OOP/service-layer learning project.
