# README — `getHistogram()` (line‑by‑line scrutiny)

This document explains the following code **line by line** and points out design/edge‑case issues:

* `getHistogram()`
* `RangeComparator`
* `SimpleRange`

The goal of `getHistogram()` is to build a **time histogram** of measurements.

---

## What `getHistogram()` returns

```java
SortedMap<Range<LocalDateTime>, Long>
```

* **Key**: a time interval (a `Range<LocalDateTime>` bucket)
* **Value**: how many `Measurement` timestamps fall inside that bucket
* **SortedMap/TreeMap**: buckets are ordered chronologically (earliest bucket first)

Example idea (not your exact output):

* `[10:00, 11:00)` → 12
* `[11:00, 12:00)` → 8
* `[12:00, 12:30]` → 3 (last bucket may include its end)

---

## The code (walkthrough)

### 1) Method header

```java
@Override
public SortedMap<Range<LocalDateTime>, Long> getHistogram() {
```

* `@Override` means this method is declared in the `NetworkReport` interface and this class is providing the implementation.

---

### 2) Create a comparator + sorted map

```java
RangeComparator comparator = new RangeComparator();
SortedMap<Range<LocalDateTime>, Long> histogramMap = new TreeMap<>(comparator);
```

* A `TreeMap` must know how to **order keys**.
* Your keys are `Range<LocalDateTime>` objects.
* Since `Range<LocalDateTime>` does **not** have a “natural ordering”, you provide a `Comparator`.

**Important behavior:** TreeMap uses the comparator for **ordering AND equality**.

* If `compare(a, b) == 0`, TreeMap considers keys “equal” and may overwrite values.
* Your comparator compares ranges only by start time. That’s okay if bucket start times are always unique.

---

### 3) Empty measurements → return empty map

```java
if (measurements.isEmpty()) {
    return histogramMap;
}
```

* No measurements means no buckets.
* Returns `{}` (empty TreeMap).

---

### 4) Initialize earliest & latest with first measurement

```java
LocalDateTime earliestTimestamp = measurements.get(0).getTimestamp();
LocalDateTime latestTimestamp = measurements.get(0).getTimestamp();
```

* Uses the first element as the initial min/max.
* Assumes `measurements` is non-empty (already checked above).

---

### 5) Scan to find the true earliest & latest timestamp

```java
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

* This is a standard “min/max scan”.
* After the loop:

  * `earliestTimestamp` = minimum timestamp in the list
  * `latestTimestamp` = maximum timestamp in the list

**Scrutiny:** if `measurement.getTimestamp()` can be null (shouldn’t), this would throw `NullPointerException`.

---

### 6) Choose the effective start date

```java
LocalDateTime effectiveStartDate = earliestTimestamp;
if (startDateStr != null) {
    int year = Integer.parseInt(startDateStr.substring(0, 4));
    int month = Integer.parseInt(startDateStr.substring(5, 7));
    int day = Integer.parseInt(startDateStr.substring(8, 10));
    int hour = Integer.parseInt(startDateStr.substring(11, 13));
    int minute = Integer.parseInt(startDateStr.substring(14, 16));
    int second = Integer.parseInt(startDateStr.substring(17, 19));
    effectiveStartDate = LocalDateTime.of(year, month, day, hour, minute, second);
}
```

* Default: start at the earliest measurement.
* If the report was created with a `startDateStr`, parse it and override.

**Scrutiny (important):**

* This assumes a **very strict string format** and length.
* If format is wrong (missing seconds, different separators), `substring(...)` throws.
* A safer/cleaner approach is `DateTimeFormatter` + `LocalDateTime.parse(...)`.

---

### 7) Choose the effective end date

```java
LocalDateTime effectiveEndDate = latestTimestamp;
if (endDateStr != null) {
    int year = Integer.parseInt(endDateStr.substring(0, 4));
    int month = Integer.parseInt(endDateStr.substring(5, 7));
    int day = Integer.parseInt(endDateStr.substring(8, 10));
    int hour = Integer.parseInt(endDateStr.substring(11, 13));
    int minute = Integer.parseInt(endDateStr.substring(14, 16));
    int second = Integer.parseInt(endDateStr.substring(17, 19));
    effectiveEndDate = LocalDateTime.of(year, month, day, hour, minute, second);
}
```

* Default: end at the latest measurement.
* If `endDateStr` exists, parse it and override.

**Scrutiny:** same parsing fragility as start date.

---

### 8) Decide bucket granularity (hourly vs daily)

```java
long totalHours = ChronoUnit.HOURS.between(effectiveStartDate, effectiveEndDate);
boolean useHourlyBuckets = (totalHours <= 48);
```

* If the time window is **48 hours or less**, use **hour buckets**.
* Otherwise, use **day buckets**.

**Scrutiny:**

* If `effectiveEndDate` is before `effectiveStartDate`, `totalHours` becomes negative.

  * Negative is still `<= 48` → it will choose hourly buckets.
  * But the histogram time window is logically wrong.
  * You may want a guard: if end < start → return empty or swap.

---

### 9) Start from the effective start

```java
LocalDateTime currentBucketStart = effectiveStartDate;
```

* The loop will build buckets starting here, and move forward until `effectiveEndDate`.

---

### 10) Bucket-building loop

```java
while (true) {
    if (currentBucketStart.isAfter(effectiveEndDate)) {
        break;
    }

    LocalDateTime currentBucketEnd;

    if (useHourlyBuckets) {
        ...
    } else {
        ...
    }

    boolean isLastBucket = currentBucketEnd.equals(effectiveEndDate);
    SimpleRange bucketRange = new SimpleRange(currentBucketStart, currentBucketEnd, isLastBucket);

    long measurementsInBucket = 0;

    for (Measurement measurement : measurements) {
        if (bucketRange.contains(measurement.getTimestamp())) {
            measurementsInBucket++;
        }
    }

    histogramMap.put(bucketRange, measurementsInBucket);

    if (currentBucketEnd.equals(effectiveEndDate)) {
        break;
    }

    currentBucketStart = currentBucketEnd;
}
```

This `while(true)` is structured as:

1. stop if start passed the end
2. compute the end of the current bucket
3. count how many timestamps fall in `[start, end)` (or last bucket `[start, end]`)
4. store result
5. if you reached the end, stop
6. else shift start → end and repeat

---

## How the bucket end is computed

### 11) Hourly bucket end

```java
LocalDateTime nextHourBoundary = currentBucketStart.plusHours(1)
    .truncatedTo(ChronoUnit.HOURS);

if (!nextHourBoundary.isAfter(currentBucketStart)) {
    nextHourBoundary = nextHourBoundary.plusHours(1);
}

if (nextHourBoundary.isAfter(effectiveEndDate)) {
    currentBucketEnd = effectiveEndDate;
} else {
    currentBucketEnd = nextHourBoundary;
}
```

What it tries to do:

* move to the **next hour boundary** (xx:00:00)
* but never go beyond the report end

Example:

* start = 10:15:30
* `plusHours(1)` → 11:15:30
* `truncatedTo(HOURS)` → 11:00:00
* so the bucket becomes `[10:15:30, 11:00:00)`

Then the next bucket starts at 11:00:00.

**Scrutiny:**

* The “safety check” `!nextHourBoundary.isAfter(currentBucketStart)` is defensive.
* It should rarely trigger, but it prevents accidental zero-length buckets.

---

### 12) Daily bucket end

```java
LocalDateTime nextDayBoundary = currentBucketStart.plusDays(1)
    .truncatedTo(ChronoUnit.DAYS);

if (!nextDayBoundary.isAfter(currentBucketStart)) {
    nextDayBoundary = nextDayBoundary.plusDays(1);
}

if (nextDayBoundary.isAfter(effectiveEndDate)) {
    currentBucketEnd = effectiveEndDate;
} else {
    currentBucketEnd = nextDayBoundary;
}
```

Same idea as hourly, but the “boundary” is at **00:00:00 of the next day**.

Example:

* start = 2024-01-10 18:20
* next day boundary becomes 2024-01-11 00:00
* bucket is `[2024-01-10 18:20, 2024-01-11 00:00)`

---

## Counting measurements in the bucket

### 13) Build a `SimpleRange`

```java
boolean isLastBucket = currentBucketEnd.equals(effectiveEndDate);
SimpleRange bucketRange = new SimpleRange(currentBucketStart, currentBucketEnd, isLastBucket);
```

* Creates a range object that knows its start/end.
* It also knows if it’s the last bucket (important for inclusive end logic).

### 14) Count measurements by checking `contains(timestamp)`

```java
long measurementsInBucket = 0;

for (Measurement measurement : measurements) {
    if (bucketRange.contains(measurement.getTimestamp())) {
        measurementsInBucket++;
    }
}
```

* For each measurement, check if its timestamp is within the bucket.

**Scrutiny (performance):**

* This is **O(B × N)**:

  * B = number of buckets
  * N = number of measurements
* If you have many measurements and many buckets, this can get slow.

---

## Storing the bucket result

### 15) Put bucket into map

```java
histogramMap.put(bucketRange, measurementsInBucket);
```

* Adds one entry: `(bucketRange → count)`.
* Because it’s a TreeMap, insertion is ordered by the comparator.

---

## Ending the loop

### 16) Stop at the last bucket

```java
if (currentBucketEnd.equals(effectiveEndDate)) {
    break;
}

currentBucketStart = currentBucketEnd;
```

* If we reached the report end, finish.
* Otherwise slide window forward.

---

## The helper classes

### 17) `RangeComparator`

```java
private class RangeComparator implements Comparator<Range<LocalDateTime>> {
    @Override
    public int compare(Range<LocalDateTime> range1, Range<LocalDateTime> range2) {
        return range1.getStart().compareTo(range2.getStart());
    }
}
```

* Orders two ranges by their **start time**.

**Scrutiny (key uniqueness):**

* Comparator ignores the end time.
* If two different ranges have the same start time, TreeMap treats them as the same key.
* In this algorithm, bucket starts should be unique, so it’s fine.

---

### 18) `SimpleRange`

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
    public LocalDateTime getStart() { return startTime; }

    @Override
    public LocalDateTime getEnd() { return endTime; }
```

* This is your concrete range implementation.

---

### 19) The most important part: `contains(timestamp)`

```java
@Override
public boolean contains(LocalDateTime timestamp) {
    if (timestamp.isBefore(startTime)) {
        return false;
    }

    if (isLastBucket) {
        return !timestamp.isAfter(endTime);
    }

    return timestamp.isBefore(endTime);
}
```

This defines the boundary rules:

* If timestamp < start → outside
* If NOT last bucket → **[start, end)** (end is excluded)
* If last bucket → **[start, end]** (end is included)

Why the special last bucket?

* Without it, a measurement exactly at the final end time might be excluded.

---

## Scrutiny checklist (what could be improved)

1. **Date parsing by substring is fragile**

* Any format deviation throws exceptions.
* Better: `DateTimeFormatter` and `LocalDateTime.parse(...)`.

2. **No validation that start ≤ end**

* If user passes end before start, histogram logic becomes questionable.

3. **Time complexity O(B × N)**

* Each bucket loops over all measurements.
* A faster approach: sort timestamps once and use a pointer, or pre-bucket counts in one pass.

4. **Comparator only uses start time**

* Fine if bucket starts are unique (they should be).

5. **Edge case: start == end**

* Produces one “zero-length” bucket; that can be acceptable.

---

## One-sentence summary

`getHistogram()` chooses a time window, splits it into hourly/day buckets, counts how many measurement timestamps fall in each bucket using `SimpleRange.contains`, and returns the bucket counts sorted by bucket start time using a TreeMap + comparator.
