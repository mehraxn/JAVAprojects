# Bank Account Simulator

## Overview

An educational, dependency-free Java project that simulates a bank. It focuses on
clean object-oriented design, a service layer with real business rules,
`BigDecimal` money handling, all-or-nothing transfers, deterministic transaction
IDs and timestamps, transaction history, and defensive data exposure — not on a
database, web API, or real banking integration.

## What This Project Demonstrates

- **Account** domain model (number, owner, balance, transaction history)
- **Transaction** domain model (ID, type, amount, timestamp, description, balanceAfter)
- **TransactionType** enum (`DEPOSIT`, `WITHDRAWAL`, `TRANSFER_IN`, `TRANSFER_OUT`)
- **Bank** service layer that owns all state changes
- account creation, deposit, withdrawal, and transfer workflows
- overdraft protection and self-transfer prevention
- all-or-nothing transfer validation
- deterministic transaction IDs (`T0001`, `T0002`, …)
- deterministic timestamps via an injectable `Clock`
- `balanceAfter` recorded on every transaction
- transaction history and total-balance reporting
- defensive `AccountSnapshot`/`TransactionSnapshot` so internal state cannot be mutated
- command-based CLI demos
- dependency-free automated tests
- strict compilation (`-Xlint:all -Werror`)

## Features

- Create accounts (with a non-negative initial balance) and reject duplicates.
- Deposit and withdraw with positive-amount and overdraft checks.
- Transfer funds between accounts as a single all-or-nothing operation.
- Track full transaction history with running `balanceAfter`.
- Report total bank balance and look up accounts by owner.
- CLI demos for every feature area.

## Main classes

| Class | Responsibility |
|---|---|
| `Account` | Balance + history; only `Bank` mutates it (package-private ops). |
| `Transaction` | Immutable money-movement record (ID, type, amount, balanceAfter). |
| `TransactionType` | Enum: `DEPOSIT`, `WITHDRAWAL`, `TRANSFER_IN`, `TRANSFER_OUT`. |
| `Bank` | Service layer: accounts, deposit, withdraw, transfer, reports. |
| `AccountSnapshot` / `TransactionSnapshot` | Immutable read-only views returned to callers. |
| `Main` | Command-based CLI / demo driver. |

## Behavior notes

- **Money uses `BigDecimal`** (never `double`/`float`); comparisons use
  `compareTo`, and user-facing output is shown with 2 decimals.
- **Balances never go negative**; deposit/withdraw/transfer amounts must be
  strictly positive.
- **Transfers are all-or-nothing.** Every check (both accounts exist, not the same
  account, positive amount, sufficient funds) runs before any balance changes, so
  a failed transfer changes no balance and records no transaction.
- **Transaction IDs are deterministic** (`T0001`, `T0002`, …). A transfer creates
  two: a `TRANSFER_OUT` on the source and a `TRANSFER_IN` on the target.
- **Timestamps come from an injectable `Clock`**, so they are reproducible in
  tests and demos.
- **Public methods return immutable snapshots in unmodifiable lists.** Live
  `Account` objects are never leaked, so external code cannot deposit/withdraw by
  bypassing the bank.

## Tech Stack

- Java 21 standard library.
- Plain `javac`/`java`; no Maven, Gradle, or external dependencies.
- `BigDecimal` for money and `java.time.Clock` for deterministic time.
- Dependency-free tests plus Bash and PowerShell validation scripts.

## Project Structure

```text
.
├── src/bankaccountsimulator/     # Accounts, transactions, service, snapshots, CLI
├── tests/bankaccountsimulator/   # Custom automated test suite
├── scripts/                       # Cross-platform validation scripts
├── TESTING.md
└── TEST_RESULTS.md
```

## How to Run

Compile:

~~~
javac -Xlint:all -Werror -d out src/bankaccountsimulator/*.java
~~~

Run the CLI commands:

~~~
java -cp out bankaccountsimulator.Main help
java -cp out bankaccountsimulator.Main demo
java -cp out bankaccountsimulator.Main deposit-demo
java -cp out bankaccountsimulator.Main withdraw-demo
java -cp out bankaccountsimulator.Main transfer-demo
java -cp out bankaccountsimulator.Main statement-demo
java -cp out bankaccountsimulator.Main validation-demo
~~~

## Testing

The project ships with a dependency-free test suite (custom assertion helper and
runner — no JUnit, Maven, or Gradle). Run everything with:

~~~
bash scripts/test.sh
~~~

Windows PowerShell:

~~~
.\scripts\test.ps1
~~~

See [TESTING.md](TESTING.md) for exact commands and [TEST_RESULTS.md](TEST_RESULTS.md)
for the latest recorded run.

## Known Limitations

This is a learning project. It intentionally has:

- no database (in-memory only)
- no HTTP API
- no login/authentication
- no real banking integration
- no interest calculation
- no concurrency/thread-safety guarantee
- no production financial guarantees
- no production deployment

## Resume Value

Built a dependency-free Java banking simulator with precise money handling, atomic transfers, deterministic transaction records, account reporting, defensive snapshots, and automated tests.
