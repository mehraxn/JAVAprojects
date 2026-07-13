# Outlier Detection

## Learning goals

- Understand what an outlier is.
- Learn a simple standard-deviation rule.
- Handle zero standard deviation safely.

## What is an outlier?

An outlier is a value far away from most other values. In reports, outliers can indicate unusual orders, data entry mistakes, or special cases.

## Simple rule

One beginner-friendly rule:

```text
outlier if absolute(value - average) > 2 * standardDeviation
```

## Example

```java
public static boolean isOutlier(double value, double average, double standardDeviation) {
    if (standardDeviation == 0.0) {
        return false;
    }
    return Math.abs(value - average) > 2.0 * standardDeviation;
}
```

## Caution

Outlier detection is context-dependent. A high order total may be valid. A low score may be valid. The report should identify unusual values, not automatically delete them.

## Common mistakes

- Treating every outlier as invalid.
- Dividing by zero.
- Running outlier detection on too few values.
- Hiding the rule used by the report.

## Mini exercise

Given product prices, calculate average and standard deviation, then list values outside two standard deviations.

## Quick summary

Outlier detection helps review unusual data, but it should be transparent and context-aware.
