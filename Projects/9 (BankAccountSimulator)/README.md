# Bank Account Simulator

An in-memory Java application for accounts, balances, transfers, and transaction history.

## Implemented features

- Create accounts with unique account numbers.
- Deposit and withdraw positive monetary amounts.
- Reject overdrafts and zero or negative amounts.
- Transfer money between different accounts.
- Validate both accounts and source funds before a transfer changes balances.
- Record deposits, withdrawals, transfer-ins, and transfer-outs.
- Return read-only transaction history and account lists.

All monetary values use `BigDecimal`. The simulator does not provide overdrafts, interest, persistence, or concurrency support.

## Structure

- `Account` owns its balance and transaction history.
- `Transaction` represents an immutable history entry.
- `Bank` creates accounts and coordinates transfers.
- `Main` demonstrates deposits, withdrawal, transfer, balances, and history.

Source files are under `src/bankaccountsimulator` and use only standard Java.

## Run

```powershell
javac -d out src\bankaccountsimulator\*.java
java -cp out bankaccountsimulator.Main
```

See `TESTING.md` for manual test cases.
