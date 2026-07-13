# Building Report Objects

## Learning goals

- Design immutable report results.
- Use defensive copies.
- Keep report generation separate from printing.

## Report object example

```java
public final class OrderReport {
    private final int orderCount;
    private final BigDecimal totalRevenue;
    private final List<String> topProductNames;

    public OrderReport(int orderCount, BigDecimal totalRevenue, List<String> topProductNames) {
        this.orderCount = orderCount;
        this.totalRevenue = totalRevenue;
        this.topProductNames = List.copyOf(topProductNames);
    }
}
```

## Why reports should be immutable

Report results should describe what was calculated at a point in time. If outside code can mutate the report, tests and output become unreliable.

## Filtering before reporting

```java
List<Order> completedOrders = orders.stream()
        .filter(Order::isCompleted)
        .toList();
```

Filtering rules should be explicit.

## Common edge cases

- Empty data.
- One row.
- All values equal.
- Division by zero.
- Missing optional fields.

## Common mistakes

- Returning mutable lists.
- Mixing report calculation with console output.
- Ignoring empty data.
- Hiding skipped data without explaining why.

## Mini exercise

Create a `StudentScoreReport` with count, average, minimum, maximum, and immutable grade buckets.

## Quick summary

Report objects make calculated results clear, immutable, and testable.
