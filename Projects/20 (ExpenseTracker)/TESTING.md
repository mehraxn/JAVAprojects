# Expense Tracker Testing

These are manual tests for the current console and public class APIs. Use a temporary CSV path so existing files are not overwritten.

## Normal test cases

| Test | Action | Expected result |
|---|---|---|
| Add expenses | Add three expenses with different IDs | All three appear in insertion order |
| Category filter | Filter `Food` records using `food` | Category matching is case-insensitive and only Food records are returned |
| Month filter | Filter by `YearMonth.of(2026, 7)` | Only expenses dated in July 2026 are returned |
| Total | Calculate the total for `42.75` and `18.50` | Result is `61.25` |
| CSV round trip | Save records, then load the file | Loaded values and record count match the originals |
| Quoted CSV | Save a title containing a comma and quotation mark | Value is quoted, escaped, and restored correctly |

## Edge-case test cases

| Test | Action | Expected result |
|---|---|---|
| No expenses | List and total a new service | Empty list and total `0` |
| No filter matches | Filter an unused category or month | Empty list |
| Missing CSV | Load a path that does not exist | Empty list; no exception |
| Empty CSV | Load a zero-byte or whitespace-only file | Empty list; no exception |
| Header only | Load a file containing only the expected header | Empty list |
| Blank lines | Put blank lines between valid CSV rows | Blank lines are ignored |
| Remove unknown ID | Remove an ID not in the service | Returns `false` |

## Invalid input test cases

| Test | Input | Expected result |
|---|---|---|
| Duplicate ID | Add two expenses with ID `E-001` | `IllegalArgumentException` |
| Invalid amount | Use `0`, a negative amount, or `null` | Validation exception |
| Empty field | Use a blank ID, title, or category | `IllegalArgumentException` |
| Invalid date text | Load `2026-02-30` from CSV | `IOException` identifies the invalid line |
| Invalid amount text | Load `twelve` as an amount | `IOException` identifies the invalid line |
| Wrong header | Load a non-empty file with different columns | `IOException` describes the expected header |
| Wrong field count | Load a row with fewer or extra fields | `IOException` identifies the line |
| Broken quotes | Load an unclosed quoted field | `IOException` identifies malformed CSV |
| Duplicate file ID | Load two rows with the same ID | `IOException` rejects the duplicate |

## Manual testing checklist

- [ ] Compile all files in `src/expensetracker`.
- [ ] Run `Main` without arguments and verify list, filters, and total output.
- [ ] Run `Main` with a temporary CSV path and verify the round-trip count.
- [ ] Inspect the saved header and ISO-formatted dates.
- [ ] Test a title containing a comma and quotation marks.
- [ ] Test missing, empty, header-only, and malformed files.
- [ ] Confirm returned lists cannot be modified.
- [ ] Confirm invalid values fail with clear messages.

## Phase 2 validation review additions

| Test | Action | Expected result |
|---|---|---|
| Directory used as input | Load an existing directory path | `IOException` says the path is not a regular file |
| Directory used as output | Save to an existing directory path | `IOException`; no expense data is changed |
