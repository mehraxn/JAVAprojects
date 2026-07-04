# Testing Expense Tracker

## Planned normal tests

- Add several expenses and list them.
- Calculate a monthly total.
- Calculate totals grouped by category.
- Save expenses and load them again.

## Planned validation tests

- Reject duplicate or blank expense IDs.
- Reject null dates and categories.
- Reject zero, negative, or null amounts.
- Handle missing and empty CSV files safely.
- Reject malformed CSV without partially loading data.

## Manual checklist

- [ ] Implement model validation.
- [ ] Implement service operations.
- [ ] Implement CSV escaping and parsing.
- [ ] Add a meaningful Main demonstration.
- [ ] Compile and run when a JDK is available.
