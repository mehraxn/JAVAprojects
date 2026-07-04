# Testing Bank Account Simulator

The project has no external test dependencies. Compile and run `Main`, or call the classes from a small Java driver.

## Manual test cases

1. Create two accounts with unique numbers and verify both start at zero.
2. Deposit `100.00`; verify the balance and a `DEPOSIT` transaction.
3. Withdraw `40.00`; verify the balance is `60.00` and a `WITHDRAWAL` transaction exists.
4. Transfer `25.00` between accounts; verify both balances and matching transfer-out/transfer-in entries.
5. Try a withdrawal larger than the balance; expect `IllegalStateException` and no balance change.
6. Try a transfer larger than the source balance; expect `IllegalStateException` and no account changes.
7. Try deposits, withdrawals, and transfers with zero, negative, or null amounts; expect `IllegalArgumentException`.
8. Try transferring an account to itself; expect `IllegalArgumentException`.
9. Try an unknown source or destination account; expect `IllegalArgumentException`.
10. Create a duplicate account number; expect `IllegalArgumentException`.
11. Try modifying the list returned by `getTransactionHistory()`; expect `UnsupportedOperationException`.
