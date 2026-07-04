# Expense Tracker

## Status

Java skeleton created. Core expense and persistence logic is not implemented yet.

## Planned features

- Add, remove, and list expenses.
- Categorize expenses.
- Calculate monthly and category totals.
- Save and load UTF-8 CSV files.
- Validate IDs, dates, categories, and positive money values.

## Current classes

- Expense: expense model.
- ExpenseService: in-memory operations and reports.
- ExpenseStore: storage contract.
- CsvExpenseStore: standard-Java CSV adapter.
- Main: demonstration entry point.

## Constraints

The project uses standard Java only. Manual JSON support is deferred; no JSON library is used.

## Source layout

Source files are under src/expensetracker.
