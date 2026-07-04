# Testing Bank Account Simulator

## Testing approach

Use exact BigDecimal strings in a small test driver and inspect balances and transaction history after each operation.

## Normal test cases

| Test | Action | Expected result |
|---|---|---|
| Create account | Create two unique account numbers | Both start at zero |
| Deposit | Deposit 100.00 | Balance is 100.00 and history has DEPOSIT |
| Withdraw | Withdraw 40.00 | Balance is 60.00 and history has WITHDRAWAL |
| Transfer | Transfer 25.00 between accounts | Both balances update correctly |
| Transfer history | Inspect both accounts | TRANSFER_OUT and TRANSFER_IN are recorded |
| List accounts | Request account list | Accounts appear in creation order |

## Edge-case test cases

| Test | Action | Expected result |
|---|---|---|
| Exact balance | Withdraw the complete balance | Balance becomes zero |
| Fractional amount | Deposit a positive decimal amount | Exact BigDecimal value is preserved |
| Shared owner | Create two accounts for one owner | Both are accepted |
| Failed operation | Trigger an overdraft or invalid transfer | No balance or history changes |
| Empty history | Inspect a new account | Empty unmodifiable list |

## Invalid input test cases

| Test | Action | Expected result |
|---|---|---|
| Invalid money | Use zero, negative, or null amount | IllegalArgumentException |
| Overdraft | Withdraw more than the balance | IllegalStateException |
| Underfunded transfer | Transfer more than source balance | IllegalStateException |
| Self-transfer | Use the same source and destination | IllegalArgumentException |
| Unknown account | Use a missing account number | IllegalArgumentException |
| Duplicate/blank account | Reuse or omit account number | IllegalArgumentException |

## Manual testing checklist

- [ ] Use BigDecimal values created from strings.
- [ ] Verify each successful operation adds one expected history entry.
- [ ] Verify a transfer adds one entry to each account.
- [ ] Verify failed operations preserve balances and histories.
- [ ] Test exact-balance withdrawal.
- [ ] Test duplicate and blank account data.
- [ ] Verify returned histories cannot be modified.
