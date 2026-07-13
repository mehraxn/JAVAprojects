# Common Mistakes

## Data import mistakes

- Using the default charset.
- Forgetting try-with-resources.
- Treating every row as valid.
- Not including row numbers in errors.
- Using `String.split` for complex CSV files.
- Returning no import summary.

## Date parsing mistakes

- Parsing dates manually with substring logic.
- Accepting an end date before a start date.
- Using inconsistent date formats.

## Logging mistakes

- Logging sensitive data.
- Using `System.out` for application diagnostics.
- Calling `printStackTrace` everywhere.
- Catching and ignoring exceptions.
- Logging too much in loops.

## Report mistakes

- Returning mutable report collections.
- Dividing by zero.
- Ignoring empty data.
- Not documenting bucket boundaries.
- Filtering after calculating totals.

## Mini exercise

Review an import method and mark each problem as import, date, logging, or report design.

## Quick summary

Imports and reports become reliable when validation, logging, edge cases, and immutable results are handled deliberately.
