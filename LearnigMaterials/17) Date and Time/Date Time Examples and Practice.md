# Date and Time — Examples and Practice

## Simple Explanation

Java's modern date/time tools live in the `java.time` package (Java 8+). The four you use
most as a beginner are:

| Class | Stores | Example value |
|---|---|---|
| `LocalDate` | date only | `2026-07-04` |
| `LocalTime` | time only | `14:30` |
| `LocalDateTime` | date **and** time (no timezone) | `2026-07-04T14:30` |
| `DateTimeFormatter` | a formatting/parsing pattern | `dd/MM/yyyy` |

All of these are **immutable**: methods like `plusDays` return a **new** object and never
change the original.

> This file focuses on hands-on examples and practice. For the reference tables and the
> `Duration`/`Period` explanations, also read `three key classes.md` and
> `Date Time Formatting Duration Period.md` in this folder.

---

## Why It Matters

- Almost every real program handles dates: deadlines, ages, bookings, logs.
- Exams test the difference between `Duration` (time) and `Period` (calendar), and how
  to format/parse with `DateTimeFormatter`.

---

## Basic Example

```java
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

LocalDate date = LocalDate.of(2026, 7, 4);       // 2026-07-04
LocalTime time = LocalTime.of(14, 30);           // 14:30
LocalDateTime dt = LocalDateTime.of(date, time); // 2026-07-04T14:30

System.out.println(date); // 2026-07-04
System.out.println(time); // 14:30
System.out.println(dt);   // 2026-07-04T14:30
```

---

## Step-by-Step Explanation

### 1. `LocalDate` — creating and reading a date

```java
LocalDate today = LocalDate.now();          // system date
LocalDate d = LocalDate.of(2026, 1, 15);    // year, month, day

int year  = d.getYear();          // 2026
int month = d.getMonthValue();    // 1
int day   = d.getDayOfMonth();    // 15
System.out.println(d.getDayOfWeek()); // THURSDAY (a DayOfWeek enum)
```

Note: month is **1-based** here (`1` = January), unlike the old `Calendar` class.

### 2. `LocalTime` — creating and reading a time

```java
LocalTime now = LocalTime.now();
LocalTime t = LocalTime.of(9, 45);   // 09:45
LocalTime t2 = LocalTime.of(9, 45, 30); // 09:45:30 (with seconds)

int hour   = t.getHour();    // 9
int minute = t.getMinute();  // 45
```

### 3. `LocalDateTime` — combining date and time

```java
LocalDateTime meeting = LocalDateTime.of(2026, 7, 4, 14, 30);

LocalDate justDate = meeting.toLocalDate(); // 2026-07-04
LocalTime justTime = meeting.toLocalTime(); // 14:30

LocalDateTime later = meeting.plusHours(2); // 2026-07-04T16:30 (new object)
```

### 4. Formatting a date with `DateTimeFormatter`

Formatting turns a date **object → String**.

```java
import java.time.format.DateTimeFormatter;

LocalDate d = LocalDate.of(2026, 7, 4);
DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

String text = d.format(fmt);
System.out.println(text); // 04/07/2026
```

Common pattern letters: `yyyy` year, `MM` month, `dd` day, `HH` hour (24h), `mm` minute, `ss` second.

### 5. Parsing a date with `DateTimeFormatter`

Parsing turns a **String → date object**. The string must match the pattern exactly.

```java
DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
LocalDate d = LocalDate.parse("04/07/2026", fmt);
System.out.println(d); // 2026-07-04
```

### 6. `Duration` vs `Period` — side by side

They answer two different questions.

| | `Duration` | `Period` |
|---|---|---|
| Measures | time (hours, minutes, seconds) | calendar (years, months, days) |
| Works best with | `LocalTime`, `LocalDateTime`, `Instant` | `LocalDate` |
| Create with | `Duration.between(t1, t2)` | `Period.between(d1, d2)` |
| Read with | `toHours()`, `toMinutes()`, `getSeconds()` | `getYears()`, `getMonths()`, `getDays()` |

```java
import java.time.Duration;
import java.time.Period;

// Duration = time distance
LocalTime start = LocalTime.of(9, 0);
LocalTime end   = LocalTime.of(11, 30);
Duration dur = Duration.between(start, end);
System.out.println(dur.toMinutes()); // 150
System.out.println(dur.toHours());   // 2

// Period = calendar distance
LocalDate from = LocalDate.of(2026, 1, 10);
LocalDate to   = LocalDate.of(2026, 3, 15);
Period p = Period.between(from, to);
System.out.println(p.getMonths()); // 2
System.out.println(p.getDays());   // 5
```

### 7. `ChronoUnit.between()` — a single total amount

`Period` gives a broken-down amount (2 months **and** 5 days). When you want **one total
number** in a specific unit, use `ChronoUnit.between()`.

```java
import java.time.temporal.ChronoUnit;

LocalDate a = LocalDate.of(2026, 1, 1);
LocalDate b = LocalDate.of(2026, 1, 11);

long days   = ChronoUnit.DAYS.between(a, b);   // 10
long months = ChronoUnit.MONTHS.between(
        LocalDate.of(2026, 1, 15),
        LocalDate.of(2026, 3, 15));            // 2
```

Difference to remember:
- `Period.between(a, b)` → "2 months, 5 days" (broken down).
- `ChronoUnit.DAYS.between(a, b)` → "66" (one total number of days).

---

## Common Mistakes

1. **Expecting the object to change.** `date.plusDays(1)` returns a **new** date; the
   original is unchanged. Always assign the result: `date = date.plusDays(1);`.

2. **Using `Duration` for months/years.** Months vary in length, so `Duration` does not
   support them — use `Period` (or `ChronoUnit.MONTHS`).

3. **Pattern not matching the string when parsing.** `parse("2026-07-04", ofPattern("dd/MM/yyyy"))`
   throws `DateTimeParseException`. The text must match the pattern exactly.

4. **Mixing case in patterns.** `MM` = month, `mm` = minutes; `HH` = 24-hour, `hh` = 12-hour.
   Getting these wrong is a classic bug.

5. **Assuming `LocalDateTime` knows a timezone.** It does not. Use `ZonedDateTime` when the
   timezone matters.

---

## Exam Notes

- `LocalDate` = date, `LocalTime` = time, `LocalDateTime` = both, no timezone.
- All `java.time` types are **immutable**; "plus/minus" returns a new object.
- Format = object → String; parse = String → object; both use `DateTimeFormatter`.
- `Duration` = time-based; `Period` = date-based.
- `Period.between` gives a **broken-down** amount; `ChronoUnit.X.between` gives a **single total**.
- Month in `LocalDate.of(...)` is **1-based** (1 = January).

---

## Practice Questions

1. Which class would you use to store only a birthday (no time)? Which for a stopwatch
   reading of minutes and seconds?

2. Write code that creates the date 2026-12-25 and prints its day of the week.

3. Format `LocalDate.of(2026, 7, 4)` as `2026.07.04` (year.month.day). What pattern do
   you pass to `ofPattern`? (Answer: `"yyyy.MM.dd"`.)

4. What is the difference between `Period.between(a, b)` and `ChronoUnit.DAYS.between(a, b)`?

5. Given `LocalTime.of(8, 0)` and `LocalTime.of(9, 15)`, write code that prints the number
   of minutes between them. (Answer: 75.)

6. Why does `LocalDate.of(2026, 1, 10).plusMonths(1)` not change the original variable?

7. Which throws an error: parsing `"10-01-2026"` with pattern `"dd/MM/yyyy"` or with pattern
   `"dd-MM-yyyy"`? Explain.
