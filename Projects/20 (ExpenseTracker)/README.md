# Expense Tracker

A console-based Java application for recording expenses, reviewing spending, and persisting records in a CSV file. The project uses only the Java standard library and keeps money values as `BigDecimal`.

## Features

- Add expenses with a unique ID, title, positive amount, category, and date.
- List all expenses in insertion order.
- Filter expenses by category or calendar month.
- Calculate overall, monthly, and per-category totals.
- Save expenses as UTF-8 CSV and load them again.
- Preserve commas and quotation marks in text fields through CSV quoting.
- Safely return an empty list for a missing, zero-length, or blank expense file.
- Reject duplicate IDs, invalid amounts, empty required fields, invalid dates, and malformed CSV rows.

## Java concepts practiced

- Classes, interfaces, encapsulation, and service/store responsibilities
- `List`, `Map`, and `Set` collections
- `BigDecimal`, `LocalDate`, and `YearMonth`
- File I/O with `Path` and `Files`
- Checked and unchecked exceptions
- Defensive, unmodifiable collection views

## Backend concepts practiced

- Separating business logic from persistence through `ExpenseService` and `ExpenseStore`
- Validating data at model, service, and file boundaries
- UTF-8 CSV serialization, parsing, and error reporting
- Returning stable read-only query results

## Main classes

- `Expense` — validated expense model.
- `ExpenseService` — in-memory expense collection, filters, and totals.
- `ExpenseStore` — persistence interface.
- `CsvExpenseStore` — CSV reader and writer.
- `Main` — small console demonstration.

## How it works

`ExpenseService` stores expenses by ID in insertion order. Its query methods return unmodifiable lists. `CsvExpenseStore` writes the header `id,title,amount,category,date` and parses the same format when loading. Dates use ISO format (`yyyy-MM-dd`). A malformed non-empty file produces an `IOException` with a line-oriented message; a missing or empty file is treated as having no saved expenses.

## Example usage

Compile from the project folder:

```text
javac -d out src/expensetracker/*.java
```

Run the demonstration without writing a file:

```text
java -cp out expensetracker.Main
```

Pass a path to demonstrate saving and loading:

```text
java -cp out expensetracker.Main expenses.csv
```

Example CSV:

```csv
id,title,amount,category,date
E-001,Groceries,42.75,Food,2026-07-02
E-002,"Lunch, coffee",18.50,Food,2026-07-03
```

## Storage approach

Expenses are held in a `LinkedHashMap` while the program runs. `CsvExpenseStore` optionally saves and restores them using the fixed header `id,title,amount,category,date`. Missing and empty files load as an empty collection; malformed nonempty files are rejected.

## Limitations

- No interactive editing workflow or automatic save operation
- Data is process-local unless CSV persistence is called explicitly
- The educational CSV parser does not support multiline fields
- Currency codes, locale formatting, and recurring expenses are not modeled

## Possible future improvements

- Interactive menu input and expense editing
- Configurable currencies and formatted reports
- Date-range and amount-range filters
- Atomic file replacement and automatic backups
- Automated unit tests
