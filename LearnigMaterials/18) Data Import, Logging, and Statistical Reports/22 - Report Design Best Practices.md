# Report Design Best Practices

## Learning goals

- Design immutable report results.
- Document formulas and units.
- Avoid exposing mutable lists and maps.

## Immutable report result

```java
public final class SalesReport {
    private final int orderCount;
    private final BigDecimal totalRevenue;
    private final Map<String, Long> ordersByStatus;

    public SalesReport(int orderCount, BigDecimal totalRevenue, Map<String, Long> ordersByStatus) {
        this.orderCount = orderCount;
        this.totalRevenue = totalRevenue;
        this.ordersByStatus = Map.copyOf(ordersByStatus);
    }
}
```

## Clear units

State whether a value is:

- count;
- ratio;
- percentage;
- money;
- days;
- milliseconds.

## Ratio vs percentage

Ratio:

```text
0.25
```

Percentage:

```text
25%
```

Do not mix them without labels.

## Filter before reporting

If a report is for completed orders only, filter first and document it.

## Avoid unnecessary full reads

If data is in a database, let the database filter and aggregate when practical. Do not always load every row into Java first.

## Common mistakes

- Mutable report collections.
- Undocumented formulas.
- Ambiguous units.
- Division by zero.
- Unstable ordering.

## Mini exercises

1. Create an immutable `InvoiceReport`.
2. Document formulas for average and percentage.
3. Add deterministic ordering to report rows.

## Quick summary

Reports should be immutable, well-labeled, formula-transparent, and edge-case safe.
