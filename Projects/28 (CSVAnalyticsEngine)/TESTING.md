# Testing CSV Analytics Engine

## Planned parser tests

- Read headers and normal rows.
- Read quoted commas and escaped quotes.
- Handle empty files and header-only files.
- Reject duplicate headers and inconsistent row widths.

## Planned analytics tests

- Filter rows.
- Sum and average numeric columns.
- Group and count text values.
- Export and reload a result data set.

## Planned validation tests

- Reject null or missing paths.
- Reject unknown columns.
- Reject non-numeric values in numeric operations.
- Define behavior for empty numeric columns.

## Manual checklist

- [ ] Implement a documented CSV grammar.
- [ ] Keep parsing and analytics separate.
- [ ] Use BigDecimal for decimal aggregation.
- [ ] Return read-only data snapshots.
- [ ] Use disposable files for manual tests.
