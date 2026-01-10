# NetworkReport getHistogram() Method - Complete Explanation

## Table of Contents
1. [Overview](#1-overview)
2. [Method Signature and Return Type](#2-method-signature-and-return-type)
3. [Step-by-Step Algorithm](#3-step-by-step-algorithm)
4. [Key Concepts](#4-key-concepts)
5. [Helper Classes](#5-helper-classes)
6. [Visual Examples](#6-visual-examples)
7. [Edge Cases](#7-edge-cases)

---

## 1. Overview

The `getHistogram()` method creates a **time-based histogram** of measurements for a network. It groups measurements into consecutive time buckets (either hourly or daily) and counts how many measurements fall into each bucket.

### Purpose
- Visualize the distribution of measurements over time
- Identify peak activity periods
- Analyze measurement patterns within a network

### What It Returns
A `SortedMap<Range<LocalDateTime>, Long>` where:
- **Key**: A time range (bucket) with start and end timestamps
- **Value**: The count of measurements that fall within that bucket

---

## 2. Method Signature and Return Type

```java
@Override
public SortedMap<Range<LocalDateTime>, Long> getHistogram()
```

### Return Type Breakdown

```
SortedMap<Range<LocalDateTime>, Long>
    │         │                   │
    │         │                   └── Count of measurements in the bucket
    │         │
    │         └── A range with start/end LocalDateTime values
    │
    └── Ordered map (sorted by bucket start time)
```

---

## 3. Step-by-Step Algorithm

### Step 1: Initialize the Result Map

```java
RangeComparator comparator = new RangeComparator();
SortedMap<Range<LocalDateTime>, Long> histogramMap = new TreeMap<>(comparator);
```

- Creates a `TreeMap` with a custom comparator
- The comparator sorts buckets by their **start time** in ascending order
- If no measurements exist, returns an empty map immediately

### Step 2: Find Earliest and Latest Timestamps

```java
LocalDateTime earliestTimestamp = measurements.get(0).getTimestamp();
LocalDateTime latestTimestamp = measurements.get(0).getTimestamp();

for (Measurement measurement : measurements) {
    LocalDateTime currentTimestamp = measurement.getTimestamp();
    if (currentTimestamp.isBefore(earliestTimestamp)) {
        earliestTimestamp = currentTimestamp;
    }
    if (currentTimestamp.isAfter(latestTimestamp)) {
        latestTimestamp = currentTimestamp;
    }
}
```

**Purpose**: Determine the actual data range within the measurements.

#### Why Initialize Both with the First Measurement?

This is a **standard min/max algorithm initialization pattern**. Here's why:

1. **The list is unsorted** - We don't know which measurement has the earliest or latest timestamp. Any measurement could be the minimum or maximum.

2. **We need valid starting values** - By using `measurements.get(0).getTimestamp()`:
   - We guarantee a real timestamp from our actual data
   - It's a valid `LocalDateTime` (never null)
   - It will be correctly replaced if a better value exists

3. **The loop corrects the values** - As we iterate through ALL measurements:
   - If we find an earlier timestamp → `earliestTimestamp` gets updated
   - If we find a later timestamp → `latestTimestamp` gets updated
   - If the first measurement happens to BE the earliest/latest → it correctly stays unchanged

**Visual Example of the Algorithm**:

```
Measurements (unsorted list):
Index 0: 2024-03-15 14:00:00  ← Initialize BOTH with this
Index 1: 2024-03-15 10:00:00  ← Actually the earliest
Index 2: 2024-03-15 18:00:00  ← Actually the latest
Index 3: 2024-03-15 12:00:00

Step-by-step iteration:
┌─────────┬─────────────────────┬─────────────────────┬─────────────────────┐
│  Step   │  Current Timestamp  │  earliestTimestamp  │  latestTimestamp    │
├─────────┼─────────────────────┼─────────────────────┼─────────────────────┤
│ Initial │         -           │  14:00:00           │  14:00:00           │
│ Index 0 │  14:00:00           │  14:00:00 (no change)│ 14:00:00 (no change)│
│ Index 1 │  10:00:00           │  10:00:00 ✓ UPDATED │  14:00:00           │
│ Index 2 │  18:00:00           │  10:00:00           │  18:00:00 ✓ UPDATED │
│ Index 3 │  12:00:00           │  10:00:00           │  18:00:00           │
└─────────┴─────────────────────┴─────────────────────┴─────────────────────┘

Final Result: earliest = 10:00:00, latest = 18:00:00 ✓
```

**Why Not Use Other Approaches?**

| Approach | Example Code | Problem |
|----------|--------------|---------|
| Use MIN/MAX constants | `LocalDateTime.MIN` | Edge case issues, not a real data point |
| Initialize with null | `LocalDateTime earliest = null` | Requires null checks in every comparison, more complex code |
| **Use first element** ✓ | `measurements.get(0)` | ✓ Safe, simple, always valid, clean code |

**Important Note**: This approach is safe here because we already checked that `measurements` is not empty at the beginning of the method. If the list were empty, `measurements.get(0)` would throw an `IndexOutOfBoundsException`.

### Step 3: Determine Effective Date Range

```java
LocalDateTime effectiveStartDate = earliestTimestamp;
if (startDateStr != null) {
    // Parse startDateStr and use it instead
    effectiveStartDate = LocalDateTime.of(year, month, day, hour, minute, second);
}

LocalDateTime effectiveEndDate = latestTimestamp;
if (endDateStr != null) {
    // Parse endDateStr and use it instead
    effectiveEndDate = LocalDateTime.of(year, month, day, hour, minute, second);
}
```

**Logic**:
| startDate | endDate | effectiveStart | effectiveEnd |
|-----------|---------|----------------|--------------|
| null | null | earliest measurement | latest measurement |
| provided | null | parsed startDate | latest measurement |
| null | provided | earliest measurement | parsed endDate |
| provided | provided | parsed startDate | parsed endDate |

### Step 4: Choose Bucket Granularity (Hourly vs Daily)

```java
long totalHours = ChronoUnit.HOURS.between(effectiveStartDate, effectiveEndDate);
boolean useHourlyBuckets = (totalHours <= 48);
```

#### Where Does This Rule Come From?

This requirement is defined in the **original project README** under the `NetworkReport` section:

> *"The map groups the network's measurements into consecutive time buckets whose granularity (hourly or daily) depends on the duration of the requested interval or, if no interval is provided, on the effective range of available measurements."*

And more specifically in the `NetworkReport` interface documentation:

> *"if both effectiveStart and effectiveEnd are non-null and the duration between them is **less than or equal to 48 hours**, the interval is partitioned into **hourly buckets**; in all other cases, the interval is partitioned into **daily buckets**."*

#### The Decision Rule

| Duration of Interval | Bucket Type | Why? |
|---------------------|-------------|------|
| **0 to 48 hours** (≤ 48) | **HOURLY** buckets | Short intervals need fine granularity for detailed analysis |
| **More than 48 hours** (> 48) | **DAILY** buckets | Long intervals would have too many hourly buckets, daily is more readable |

#### How the Code Implements This

```java
// Step 1: Calculate the total duration in hours
long totalHours = ChronoUnit.HOURS.between(effectiveStartDate, effectiveEndDate);

// Step 2: Apply the 48-hour threshold rule
boolean useHourlyBuckets = (totalHours <= 48);
//                         ↑
//                         This is the KEY decision point!
//                         
//                         totalHours <= 48  →  useHourlyBuckets = true   →  HOURLY
//                         totalHours > 48   →  useHourlyBuckets = false  →  DAILY
```

#### Visual Decision Flowchart

```
                    ┌─────────────────────────────┐
                    │  Calculate duration between │
                    │  effectiveStart and         │
                    │  effectiveEnd               │
                    └──────────────┬──────────────┘
                                   │
                                   ▼
                    ┌─────────────────────────────┐
                    │  totalHours = ChronoUnit    │
                    │  .HOURS.between(start, end) │
                    └──────────────┬──────────────┘
                                   │
                                   ▼
                    ┌─────────────────────────────┐
                    │   Is totalHours <= 48 ?     │
                    └──────────────┬──────────────┘
                                   │
                    ┌──────────────┴──────────────┐
                    │                             │
                YES ▼                             ▼ NO
        ┌───────────────────┐         ┌───────────────────┐
        │  useHourlyBuckets │         │  useHourlyBuckets │
        │      = true       │         │      = false      │
        │                   │         │                   │
        │  → HOURLY BUCKETS │         │  → DAILY BUCKETS  │
        │  (1-hour chunks)  │         │  (1-day chunks)   │
        └───────────────────┘         └───────────────────┘
```

#### Practical Examples

```
┌─────────────────────────────────────────────────────────────────────────┐
│  EXAMPLE 1: Short Interval (HOURLY)                                     │
│                                                                         │
│  effectiveStart: 2024-03-15 10:00:00                                    │
│  effectiveEnd:   2024-03-16 22:00:00                                    │
│                                                                         │
│  Duration: 36 hours                                                     │
│  36 <= 48? YES → useHourlyBuckets = true                                │
│                                                                         │
│  Result: Creates ~36 hourly buckets                                     │
│  [10:00-11:00), [11:00-12:00), [12:00-13:00), ... [21:00-22:00]         │
├─────────────────────────────────────────────────────────────────────────┤
│  EXAMPLE 2: Exactly 48 Hours (HOURLY - boundary case)                   │
│                                                                         │
│  effectiveStart: 2024-03-15 00:00:00                                    │
│  effectiveEnd:   2024-03-17 00:00:00                                    │
│                                                                         │
│  Duration: 48 hours                                                     │
│  48 <= 48? YES → useHourlyBuckets = true                                │
│                                                                         │
│  Result: Creates 48 hourly buckets                                      │
├─────────────────────────────────────────────────────────────────────────┤
│  EXAMPLE 3: Just Over 48 Hours (DAILY)                                  │
│                                                                         │
│  effectiveStart: 2024-03-15 00:00:00                                    │
│  effectiveEnd:   2024-03-17 01:00:00                                    │
│                                                                         │
│  Duration: 49 hours                                                     │
│  49 <= 48? NO → useHourlyBuckets = false                                │
│                                                                         │
│  Result: Creates 3 daily buckets                                        │
│  [Mar15 00:00 - Mar16 00:00), [Mar16 00:00 - Mar17 00:00),              │
│  [Mar17 00:00 - Mar17 01:00]                                            │
├─────────────────────────────────────────────────────────────────────────┤
│  EXAMPLE 4: Long Interval (DAILY)                                       │
│                                                                         │
│  effectiveStart: 2024-03-01 00:00:00                                    │
│  effectiveEnd:   2024-03-15 00:00:00                                    │
│                                                                         │
│  Duration: 336 hours (14 days)                                          │
│  336 <= 48? NO → useHourlyBuckets = false                               │
│                                                                         │
│  Result: Creates 14 daily buckets (much more manageable than 336!)      │
└─────────────────────────────────────────────────────────────────────────┘
```

#### Why 48 Hours as the Threshold?

The choice of **48 hours** makes practical sense:

| Scenario | Hourly Buckets | Daily Buckets |
|----------|----------------|---------------|
| 1 day (24h) | 24 buckets ✓ readable | 1 bucket (too coarse) |
| 2 days (48h) | 48 buckets ✓ manageable | 2 buckets (too coarse) |
| 3 days (72h) | 72 buckets (too many!) | 3 buckets ✓ readable |
| 1 week (168h) | 168 buckets (way too many!) | 7 buckets ✓ perfect |
| 1 month (720h) | 720 buckets (unusable!) | ~30 buckets ✓ good |

**48 hours is the sweet spot** where hourly data is still useful and not overwhelming.

### Step 5: Create Buckets Iteratively

```java
LocalDateTime currentBucketStart = effectiveStartDate;

while (true) {
    if (currentBucketStart.isAfter(effectiveEndDate)) {
        break;
    }
    
    // Calculate bucket end...
    // Create bucket...
    // Count measurements...
    
    if (currentBucketEnd.equals(effectiveEndDate)) {
        break;
    }
    
    currentBucketStart = currentBucketEnd;  // Move to next bucket
}
```

#### For Hourly Buckets:

```java
LocalDateTime nextHourBoundary = currentBucketStart.plusHours(1).truncatedTo(ChronoUnit.HOURS);

if (!nextHourBoundary.isAfter(currentBucketStart)) {
    nextHourBoundary = nextHourBoundary.plusHours(1);
}

if (nextHourBoundary.isAfter(effectiveEndDate)) {
    currentBucketEnd = effectiveEndDate;  // Last bucket ends at effectiveEnd
} else {
    currentBucketEnd = nextHourBoundary;  // Normal hour boundary
}
```

#### For Daily Buckets:

```java
LocalDateTime nextDayBoundary = currentBucketStart.plusDays(1).truncatedTo(ChronoUnit.DAYS);

if (!nextDayBoundary.isAfter(currentBucketStart)) {
    nextDayBoundary = nextDayBoundary.plusDays(1);
}

if (nextDayBoundary.isAfter(effectiveEndDate)) {
    currentBucketEnd = effectiveEndDate;  // Last bucket ends at effectiveEnd
} else {
    currentBucketEnd = nextDayBoundary;   // Normal day boundary
}
```

### Step 6: Create Range and Count Measurements

```java
boolean isLastBucket = currentBucketEnd.equals(effectiveEndDate);
SimpleRange bucketRange = new SimpleRange(currentBucketStart, currentBucketEnd, isLastBucket);

long measurementsInBucket = 0;
for (Measurement measurement : measurements) {
    if (bucketRange.contains(measurement.getTimestamp())) {
        measurementsInBucket++;
    }
}

histogramMap.put(bucketRange, measurementsInBucket);
```

---

## 4. Key Concepts

### 4.1 Bucket Boundary Convention

The histogram follows a **left-closed, right-open** convention with a special rule for the last bucket:

```
Normal Buckets:     [start, end)    → start ≤ value < end
Last Bucket:        [start, end]    → start ≤ value ≤ end
```

**Why?** To ensure the maximum timestamp is always included in some bucket.

```
Example with measurements at: 10:00, 10:30, 11:00, 11:30, 12:00

Buckets:
[10:00, 11:00)  → Contains: 10:00, 10:30    Count: 2
[11:00, 12:00]  → Contains: 11:00, 11:30, 12:00    Count: 3  (LAST BUCKET - includes 12:00!)
```

### 4.2 Truncation Explained

`truncatedTo(ChronoUnit.HOURS)` sets minutes, seconds, nanoseconds to zero:

```
2024-03-15 14:37:22  →  truncatedTo(HOURS)  →  2024-03-15 14:00:00
2024-03-15 00:00:00  →  truncatedTo(DAYS)   →  2024-03-15 00:00:00
```

### 4.3 Contiguous Buckets

Buckets are **contiguous** (no gaps between them):

```
Bucket 1: [2024-03-15 10:00:00, 2024-03-15 11:00:00)
Bucket 2: [2024-03-15 11:00:00, 2024-03-15 12:00:00)   ← Starts where Bucket 1 ends
Bucket 3: [2024-03-15 12:00:00, 2024-03-15 13:00:00]   ← Last bucket
```

---

## 5. Helper Classes

### 5.1 SimpleRange (Inner Class)

```java
private class SimpleRange implements Range<LocalDateTime> {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isLastBucket;

    public SimpleRange(LocalDateTime start, LocalDateTime end, boolean isLast) {
        this.startTime = start;
        this.endTime = end;
        this.isLastBucket = isLast;
    }

    @Override
    public LocalDateTime getStart() {
        return startTime;
    }

    @Override
    public LocalDateTime getEnd() {
        return endTime;
    }

    @Override
    public boolean contains(LocalDateTime timestamp) {
        // Check if timestamp is before start
        if (timestamp.isBefore(startTime)) {
            return false;
        }

        // For last bucket: [start, end] (inclusive)
        if (isLastBucket) {
            return !timestamp.isAfter(endTime);
        }

        // For normal buckets: [start, end) (exclusive end)
        return timestamp.isBefore(endTime);
    }
}
```

**The `contains()` Logic Visualized**:

```
                    startTime           endTime
                        │                   │
    ────────────────────┼───────────────────┼────────────────────
                        │                   │
    BEFORE START        │     IN RANGE      │     AFTER END
    (returns false)     │                   │
                        │                   │
    Normal Bucket:      │  [inclusive       │  exclusive)
                        │                   │
    Last Bucket:        │  [inclusive       │  inclusive]
```

### 5.2 RangeComparator (Inner Class)

```java
private class RangeComparator implements Comparator<Range<LocalDateTime>> {
    @Override
    public int compare(Range<LocalDateTime> range1, Range<LocalDateTime> range2) {
        return range1.getStart().compareTo(range2.getStart());
    }
}
```

**Purpose**: Ensures the `TreeMap` is sorted by bucket start times in ascending order.

---

## 6. Visual Examples

### Example 1: Hourly Buckets (≤ 48 hours)

**Scenario**:
- `effectiveStartDate`: `2024-03-15 10:30:00`
- `effectiveEndDate`: `2024-03-15 14:15:00`
- Duration: ~4 hours (< 48, so HOURLY)

**Generated Buckets**:

```
    10:30        11:00        12:00        13:00        14:00   14:15
      │            │            │            │            │       │
      ▼            ▼            ▼            ▼            ▼       ▼
    ┌─────────────┬────────────┬────────────┬────────────┬───────┐
    │  Bucket 1   │  Bucket 2  │  Bucket 3  │  Bucket 4  │  B5   │
    │ [10:30,11:00)│[11:00,12:00)│[12:00,13:00)│[13:00,14:00)│[14:00,14:15]│
    └─────────────┴────────────┴────────────┴────────────┴───────┘
                                                          ↑
                                              Last bucket is [inclusive]
```

### Example 2: Daily Buckets (> 48 hours)

**Scenario**:
- `effectiveStartDate`: `2024-03-15 14:00:00`
- `effectiveEndDate`: `2024-03-18 10:00:00`
- Duration: ~68 hours (> 48, so DAILY)

**Generated Buckets**:

```
    Mar 15          Mar 16          Mar 17          Mar 18
    14:00           00:00           00:00           00:00   10:00
      │               │               │               │       │
      ▼               ▼               ▼               ▼       ▼
    ┌────────────────┬───────────────┬───────────────┬───────┐
    │    Bucket 1    │   Bucket 2    │   Bucket 3    │  B4   │
    │[Mar15 14:00,   │[Mar16 00:00,  │[Mar17 00:00,  │[Mar18 │
    │ Mar16 00:00)   │ Mar17 00:00)  │ Mar18 00:00)  │00:00, │
    │                │               │               │10:00] │
    └────────────────┴───────────────┴───────────────┴───────┘
```

### Example 3: Measurement Counting

**Measurements**:
```
M1: 2024-03-15 10:30:00
M2: 2024-03-15 10:59:59
M3: 2024-03-15 11:00:00
M4: 2024-03-15 11:30:00
M5: 2024-03-15 12:00:00  ← This is exactly at effectiveEndDate
```

**Buckets and Counts**:

```
Bucket [10:30, 11:00)  → M1, M2           → Count: 2
Bucket [11:00, 12:00]  → M3, M4, M5       → Count: 3 (Last bucket includes 12:00)
```

---

## 7. Edge Cases

### 7.1 Empty Measurements

```java
if (measurements.isEmpty()) {
    return histogramMap;  // Returns empty SortedMap
}
```

### 7.2 Single Measurement

- Creates exactly one bucket
- That bucket is both the first and last bucket
- The bucket is `[timestamp, timestamp]` (inclusive on both ends)

### 7.3 All Measurements at Same Time

- Creates exactly one bucket
- All measurements fall into this single bucket

### 7.4 Measurements Exactly at Bucket Boundaries

```
Bucket 1: [10:00, 11:00)  ← 11:00:00 is NOT in this bucket
Bucket 2: [11:00, 12:00]  ← 11:00:00 IS in this bucket
```

### 7.5 Partial First/Last Buckets

The first bucket may not start at hour/day boundary:
```
effectiveStart: 10:37:22
First bucket: [10:37:22, 11:00:00)  ← Partial hour
```

The last bucket may not end at hour/day boundary:
```
effectiveEnd: 14:22:15
Last bucket: [14:00:00, 14:22:15]  ← Partial hour
```

---

## Summary Flowchart

```
                    ┌─────────────────────┐
                    │   getHistogram()    │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │ Empty measurements? │
                    └──────────┬──────────┘
                        Yes │      │ No
                    ┌───────▼──┐   │
                    │Return {}│   │
                    └──────────┘   │
                               ┌───▼───────────────────┐
                               │Find earliest/latest   │
                               │measurement timestamps │
                               └───────────┬───────────┘
                                           │
                               ┌───────────▼───────────┐
                               │Calculate effective    │
                               │start/end dates        │
                               └───────────┬───────────┘
                                           │
                               ┌───────────▼───────────┐
                               │Duration ≤ 48 hours?   │
                               └───────────┬───────────┘
                                    Yes │      │ No
                               ┌────────▼──┐ ┌─▼────────┐
                               │ HOURLY    │ │  DAILY   │
                               │ buckets   │ │  buckets │
                               └────────┬──┘ └─┬────────┘
                                        │      │
                               ┌────────▼──────▼────────┐
                               │Create buckets from     │
                               │effectiveStart to       │
                               │effectiveEnd            │
                               └───────────┬────────────┘
                                           │
                               ┌───────────▼───────────┐
                               │For each bucket:       │
                               │Count measurements     │
                               │where contains(t)==true│
                               └───────────┬───────────┘
                                           │
                               ┌───────────▼───────────┐
                               │Return SortedMap       │
                               │(ordered by start time)│
                               └───────────────────────┘
```

---

## Code Reference

The complete `getHistogram()` method can be found in:
```
com.weather.report.reports.NetworkReportImpl
```

This method implements the `NetworkReport` interface which extends `Report<LocalDateTime>`.