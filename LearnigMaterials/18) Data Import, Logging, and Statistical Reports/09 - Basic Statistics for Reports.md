# Basic Statistics for Reports

## Learning goals

- Calculate count, min, max, average, variance, and standard deviation.
- Handle empty and one-value data safely.
- Avoid division by zero.

## Count, min, max, average

```java
int count = values.size();
double min = values.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
double max = values.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
double average = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
```

## Variance and standard deviation

```java
double average = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

double variance = values.isEmpty()
        ? 0.0
        : values.stream()
                .mapToDouble(value -> Math.pow(value - average, 2))
                .average()
                .orElse(0.0);

double standardDeviation = Math.sqrt(variance);
```

## Percentage

```java
double percentage = total == 0 ? 0.0 : (part * 100.0) / total;
```

## Edge cases

- Empty data: define defaults.
- One value: standard deviation is usually `0.0`.
- All values equal: standard deviation is `0.0`.
- Zero total: percentage should not divide by zero.

## Common mistakes

- Calling `.getAsDouble()` on an empty optional.
- Dividing by zero.
- Forgetting to document default behavior.

## Mini exercise

Write a method that returns count, min, max, average, and standard deviation for product prices.

## Quick summary

Reports must handle edge cases as carefully as normal business workflows.
