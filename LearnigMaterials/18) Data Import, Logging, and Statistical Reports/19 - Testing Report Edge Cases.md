# Testing Report Edge Cases

## Learning goals

- Test reports for edge cases.
- Avoid division by zero.
- Verify deterministic ordering and bucket boundaries.

## Important cases

Test:

- empty data;
- one value;
- all values equal;
- negative values if the domain allows them;
- zero totals;
- bucket boundaries;
- deterministic ordering.

## Empty data example

```java
Report report = ReportCalculator.from(List.of());

assertEquals(0, report.count());
assertEquals(0.0, report.average());
```

Document the chosen behavior.

## Bucket boundary example

If score buckets are:

- 0-59;
- 60-69;
- 70-79;
- 80-89;
- 90-100;

Test exactly 59, 60, 69, 70, 89, 90, and 100.

## Deterministic ordering

If report rows have ties, define tie-breakers such as name then ID.

## Common mistakes

- Testing only typical values.
- Forgetting one-value standard deviation.
- Not checking bucket boundaries.
- Returning maps with unpredictable order when output order matters.

## Mini exercises

1. Test a report with no invoices.
2. Test a report with one order.
3. Test all histogram boundaries.
4. Test tie-breaking in top products.

## Quick summary

Report tests should prove edge-case behavior, not just average-case calculations.
