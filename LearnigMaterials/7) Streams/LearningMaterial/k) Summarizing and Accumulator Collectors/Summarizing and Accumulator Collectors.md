# Summarizing and Accumulator Collectors

## Simple Explanation

Sometimes you do not want the individual elements — you want **one summary number**:
how many, the total, the average, the minimum, the maximum. The `Collectors` class has
ready-made collectors for exactly this, used with `collect()`:

- `counting()` — how many elements
- `summingInt()` — the total
- `averagingInt()` — the average
- `minBy()` — the smallest element
- `maxBy()` — the largest element
- `summarizingInt()` — count, sum, min, max, average **all at once**
- `reducing()` — a general "combine everything into one value" tool

For all examples below we use a small list of students:

```java
class Student {
    String name;
    int score;
    Student(String name, int score) { this.name = name; this.score = score; }
    String getName() { return name; }
    int getScore()   { return score; }
}

List<Student> students = List.of(
    new Student("Sara", 85),
    new Student("Tom",  72),
    new Student("Ali",  90),
    new Student("Mia",  72)
);
```

---

## Why It Matters

- Reports and statistics ("average grade", "highest score", "total price") are extremely
  common in real programs and exams.
- These collectors are often used as the **downstream** collector inside `groupingBy`,
  e.g. "average score per class".

---

## Basic Example

```java
import java.util.stream.Collectors;

long howMany = students.stream()
    .collect(Collectors.counting());          // 4

int total = students.stream()
    .collect(Collectors.summingInt(Student::getScore));   // 319

double average = students.stream()
    .collect(Collectors.averagingInt(Student::getScore)); // 79.75
```

---

## Step-by-Step Explanation

### 1. `counting()` — number of elements → returns `Long`

```java
long count = students.stream().collect(Collectors.counting()); // 4
```

### 2. `summingInt()` — total of an int field → returns `Integer`

```java
int totalScore = students.stream()
    .collect(Collectors.summingInt(Student::getScore)); // 319
```
There are matching `summingLong` and `summingDouble` for other number types.

### 3. `averagingInt()` — average of an int field → returns `Double`

```java
double avg = students.stream()
    .collect(Collectors.averagingInt(Student::getScore)); // 79.75
```
`averagingInt` always returns a `double`, even though the field is an `int`.

### 4. `minBy()` — smallest element → returns `Optional<T>`

```java
import java.util.Comparator;
import java.util.Optional;

Optional<Student> lowest = students.stream()
    .collect(Collectors.minBy(Comparator.comparingInt(Student::getScore)));

lowest.ifPresent(s -> System.out.println(s.getName())); // Tom
```
It returns an `Optional` because the stream could be **empty** (then there is no minimum).

### 5. `maxBy()` — largest element → returns `Optional<T>`

```java
Optional<Student> top = students.stream()
    .collect(Collectors.maxBy(Comparator.comparingInt(Student::getScore)));

System.out.println(top.get().getName()); // Ali
```

### 6. `summarizingInt()` — everything at once → returns `IntSummaryStatistics`

```java
import java.util.IntSummaryStatistics;

IntSummaryStatistics stats = students.stream()
    .collect(Collectors.summarizingInt(Student::getScore));

System.out.println(stats.getCount());   // 4
System.out.println(stats.getSum());     // 319
System.out.println(stats.getMin());     // 72
System.out.println(stats.getMax());     // 90
System.out.println(stats.getAverage()); // 79.75
```
Use this when you need several statistics — it computes them in a single pass.

### 7. `reducing()` — the general combiner

`reducing` folds all elements into one value using a starting value and a combining rule.

```java
// Total score using reducing: start at 0, add each score
int total = students.stream()
    .collect(Collectors.reducing(
        0,                    // identity (starting value)
        Student::getScore,    // map each student to its score
        Integer::sum));       // combine two scores
// 319
```

For most simple cases `summingInt`, `counting`, etc. are clearer. Reach for `reducing`
only when no ready-made collector fits.

### Using them inside `groupingBy`

The real power shows when these become **downstream** collectors:

```java
// Average score per... (imagine students grouped by class name)
Map<String, Double> avgByName = students.stream()
    .collect(Collectors.groupingBy(
        Student::getName,
        Collectors.averagingInt(Student::getScore)));
```

---

## When to Use Each

| You want... | Use | Returns |
|---|---|---|
| How many elements | `counting()` | `Long` |
| The total of a number field | `summingInt/Long/Double` | `Integer`/`Long`/`Double` |
| The average of a number field | `averagingInt/Long/Double` | `Double` |
| The smallest element | `minBy(comparator)` | `Optional<T>` |
| The largest element | `maxBy(comparator)` | `Optional<T>` |
| Count + sum + min + max + avg together | `summarizingInt/Long/Double` | `IntSummaryStatistics` etc. |
| A custom combine that none of the above cover | `reducing(...)` | value or `Optional<T>` |

---

## Common Mistakes

1. **Forgetting `minBy`/`maxBy` return an `Optional`.** You must handle the empty case
   (`ifPresent`, `orElse`, ...). Calling `.get()` on an empty result throws
   `NoSuchElementException`.

2. **Expecting `averagingInt` to return an `int`.** It always returns a `double`
   (e.g. `79.75`, not `79`).

3. **Using `summingInt` with the wrong type helper.** `summingInt` needs a function
   returning `int`. For a `double` field use `summingDouble`.

4. **Reaching for `reducing` too early.** If a named collector exists (`counting`,
   `summingInt`, ...), it is clearer and less error-prone than `reducing`.

5. **Recomputing the stream for each statistic.** If you need several numbers, prefer one
   `summarizingInt` instead of separate `count()`, `sum()`, `average()` passes (and
   remember a stream is single-use anyway).

---

## Exam Notes

- `counting()` → `Long`. `summingInt` → `Integer`. `averagingInt` → `Double`.
- `minBy` / `maxBy` → `Optional<T>` and take a `Comparator`.
- `summarizingInt` → an `IntSummaryStatistics` with `getCount/getSum/getMin/getMax/getAverage`.
- These collectors are commonly used as the **downstream** argument of `groupingBy`.
- `reducing(identity, mapper, op)` is the general form that returns a plain value (not an `Optional`).

---

## Practice Questions

1. Which collector counts the elements, and what type does it return?

2. Write a pipeline that computes the **total** score of all students.

3. Write a pipeline that finds the student with the **highest** score and prints their
   name safely (handle the empty case).

4. What does `averagingInt` return for the list above, and what is its Java type?

5. Replace three separate calls (count, sum, average) with a single collector. Which one,
   and what object does it return?

6. Why do `minBy` and `maxBy` return an `Optional`?

7. Using `groupingBy`, write a pipeline that produces a `Map<String, Long>` counting how
   many students share each name.
