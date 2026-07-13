# Histograms and Buckets

## Learning goals

- Understand histograms.
- Build value buckets.
- Handle boundary values clearly.

## What is a histogram?

A histogram groups numeric values into ranges called buckets.

Example score buckets:

| Bucket | Range |
|---|---|
| F | 0-59 |
| D | 60-69 |
| C | 70-79 |
| B | 80-89 |
| A | 90-100 |

## Bucket function

```java
public static String scoreBucket(double score) {
    if (score >= 90.0) {
        return "A";
    }
    if (score >= 80.0) {
        return "B";
    }
    if (score >= 70.0) {
        return "C";
    }
    if (score >= 60.0) {
        return "D";
    }
    return "F";
}
```

## Counting buckets

```java
Map<String, Long> histogram = scores.stream()
        .collect(Collectors.groupingBy(
                ReportMath::scoreBucket,
                TreeMap::new,
                Collectors.counting()));
```

## Common mistakes

- Overlapping bucket ranges.
- Missing boundary values.
- Sorting bucket labels alphabetically when numeric order matters.
- Not documenting whether boundaries are inclusive.

## Mini exercise

Create buckets for order totals: `0-49`, `50-99`, `100-499`, and `500+`.

## Quick summary

Histograms turn many numeric values into readable distribution summaries.
