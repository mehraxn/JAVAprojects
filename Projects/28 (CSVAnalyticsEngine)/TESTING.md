# CSV Analytics Engine Testing

Use temporary input files for these manual tests. The examples assume the program has been compiled into an `out` directory.

## Normal test cases

| Test | Input or action | Expected result |
|---|---|---|
| Read table | Header plus three complete rows | Correct columns and row count `3` |
| Numeric statistics | Values `10`, `20`, and `30` | Min `10`, max `30`, average `20`, valid count `3` |
| Decimal statistics | Values `1.5` and `2.5` | Min `1.5`, max `2.5`, average `2` |
| Group rows | Categories `A`, `B`, `A` | Group A contains 2 rows; B contains 1 |
| Filter rows | Filter category for `A` | Only exact value `A` rows are returned |
| Case-insensitive column | Request `AMOUNT` for header `amount` | Column resolves and statistics are calculated |
| CSV round trip | Read, write, and read a quoted data set | Columns and values remain unchanged |

## Edge-case test cases

| Test | Input or action | Expected result |
|---|---|---|
| Empty file | Zero-byte or whitespace-only file | Empty data set with zero rows and columns |
| Header only | Valid header without data rows | Columns shown and row count `0` |
| Blank rows | Blank lines between records | Blank lines are ignored |
| Missing numeric cell | One blank amount | Missing count increases; other numbers are analyzed |
| Invalid numeric cell | Amount `unknown` | Invalid count increases; analysis continues |
| No valid numbers | Numeric analysis of blanks/text only | Min, max, and average are `n/a` |
| Missing group value | Group a row with a blank category | Row appears in `(missing)` group |
| Negative values | Values `-5` and `10` | Min `-5`, max `10`, average `2.5` |
| Quoted comma | Value `"Berlin, Germany"` | Parsed as one field |
| Escaped quote | Value `"He said ""yes"""` | Parsed with one quotation mark pair in the value |

## Invalid input test cases

| Test | Input | Expected result |
|---|---|---|
| Missing file | Path does not exist | `IOException` with a clear file message |
| Empty header name | Header `name,,amount` | `IOException` reports invalid structure |
| Duplicate header | Header `name,Name` | `IOException` rejects case-insensitive duplicate |
| Short row | Header has 3 fields; row has 2 | `IOException` reports actual and expected counts |
| Long row | Header has 2 fields; row has 3 | `IOException` reports actual and expected counts |
| Broken quotes | Unclosed or misplaced quotation mark | `IOException` identifies the malformed line |
| Unknown column | Analyze a column absent from the header | `IllegalArgumentException` |
| Null data set | Pass `null` to an analytics method | `IllegalArgumentException` |
| Multiline value | Quoted value spans physical lines | Rejected as unsupported/malformed input |

## Manual testing checklist

- [ ] Compile all files in `src/csvanalyticsengine`.
- [ ] Run `Main` without arguments and verify the usage message.
- [ ] Run with a valid file and confirm columns and row count.
- [ ] Request numeric statistics and verify valid, missing, and invalid counts.
- [ ] Request grouping and verify every row belongs to one group.
- [ ] Test commas and escaped quotation marks inside quoted cells.
- [ ] Test empty, header-only, inconsistent, and malformed files.
- [ ] Confirm unknown columns fail clearly.
- [ ] Write and reread a temporary data set to verify CSV escaping.

## Phase 2 validation review additions

| Test | Action | Expected result |
|---|---|---|
| Directory CSV path | Read or write an existing directory | `IOException` identifies a non-regular file |
| Mutate original row | Add row, then add another field to the original object | Stored data-set row remains unchanged |
| Mutate returned row | Change a row returned by `getRows()` | Stored data set remains unchanged |
| Negative statistics count | Construct statistics with any negative count | Rejected |
| Inconsistent empty statistics | Valid count zero with min/max/average values | Rejected |
| Inconsistent populated statistics | Positive valid count with missing min/max/average | Rejected |
| Impossible average | Average below minimum or above maximum | Rejected |
