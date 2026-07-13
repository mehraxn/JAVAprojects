# Data Import and Reports Revision

## Data import checklist

- Use explicit charset such as UTF-8.
- Use try-with-resources.
- Read and validate headers.
- Validate row length.
- Validate required fields.
- Parse numbers and dates safely.
- Return an `ImportResult`.
- Include row numbers in errors.

## Logging checklist

- Use a logger per class.
- Choose levels intentionally.
- Avoid sensitive data.
- Do not swallow exceptions.
- Avoid excessive loop logging.

## Report checklist

- Define empty-data behavior.
- Use immutable report objects.
- Use defensive copies for collections.
- Calculate count, min, max, and average safely.
- Avoid division by zero.
- Document histogram bucket boundaries.

## Statistics reminders

- Count: number of values.
- Min/max: smallest/largest values.
- Average: total divided by count.
- Standard deviation: spread around the average.
- Outlier: unusual value according to a documented rule.

## Quick summary

Imports and reports are backend features: they need validation, clear results, logging, and edge-case handling.
