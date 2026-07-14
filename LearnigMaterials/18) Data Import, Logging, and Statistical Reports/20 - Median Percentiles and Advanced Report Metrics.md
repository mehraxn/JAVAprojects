# Median, Percentiles, and Advanced Report Metrics

## Learning goals

- Understand median and percentiles.
- Sort data before percentile calculations.
- Use metrics carefully in reports.

## Median

The median is the middle value after sorting.

```java
List<Double> sorted = values.stream().sorted().toList();
```

For odd count, take the middle. For even count, average the two middle values.

## Simple median method

```java
public static double median(List<Double> values) {
    if (values.isEmpty()) {
        return 0.0;
    }
    List<Double> sorted = values.stream().sorted().toList();
    int middle = sorted.size() / 2;
    if (sorted.size() % 2 == 1) {
        return sorted.get(middle);
    }
    return (sorted.get(middle - 1) + sorted.get(middle)) / 2.0;
}
```

## Percentile idea

A percentile shows a value below which a percentage of values fall. A simple learning rule is to sort values and choose an index based on the percentile.

## Use cases

- invoice totals;
- order processing time;
- student scores;
- employee performance metrics.

## Common mistakes

- Calculating median without sorting.
- Not documenting percentile formula.
- Treating percentile methods as identical across all tools.
- Ignoring empty data.

## Mini exercises

1. Calculate median for odd and even lists.
2. Implement a simple 90th percentile.
3. Explain why percentile formulas should be documented.

## Quick summary

Median and percentiles describe distribution beyond average, but formulas and edge cases must be clear.
