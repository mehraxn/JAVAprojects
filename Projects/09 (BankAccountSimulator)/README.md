# Bank Account Simulator

## Description

Bank Account Simulator is an in-memory Java project for accounts, balances, transfers, and transaction history. BigDecimal is used for money to avoid floating-point rounding errors.

## Features

- Create accounts with unique account numbers.
- Deposit and withdraw positive amounts.
- Prevent overdrafts.
- Transfer funds between different accounts.
- Reject invalid, self, and underfunded transfers.
- Record deposits, withdrawals, transfer-ins, and transfer-outs.
- Return read-only account and transaction lists.

## Java concepts practiced

- Encapsulation of financial state
- BigDecimal arithmetic and comparison
- List and Map collections
- Immutable transaction records
- Validation and exception handling
- Coordinating atomic-style operations between objects

## Main classes

- Account: owns balance and transaction history.
- Transaction: represents a dated financial operation.
- Bank: creates accounts and coordinates transfers.
- Main: demonstrates deposits, withdrawal, transfer, and history.

## How the program works

Account performs validated credits and debits. Bank validates both accounts, the amount, and available funds before applying a transfer. Successful operations append Transaction records; failed operations leave balances and history unchanged.

## Example usage

~~~powershell
javac -d out src\bankaccountsimulator\*.java
java -cp out bankaccountsimulator.Main
~~~

The demo prints final balances and each account's transaction history.

## Possible future improvements

- Add account closing rules.
- Add transaction filtering by type or date.
- Add statement export to a text file.
- Add configurable transfer limits.
- Add currency and rounding policies.
