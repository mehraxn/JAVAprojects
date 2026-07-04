# Java Time API - Key Classes

This README explains three important classes in the `java.time` package: `LocalDateTime`, `DateTimeFormatter`, and `ChronoUnit`. These classes are part of Java 8's modern date-time API.

---

## 1. java.time.LocalDateTime

**Description:**
`LocalDateTime` represents both date and time without any time zone information. It is immutable and thread-safe.

**Key Features:**

* Stores year, month, day, hour, minute, second, and nanosecond.
* No time zone or offset.
* Can perform date-time arithmetic.

**Usage Examples:**

```java
import java.time.LocalDateTime;

LocalDateTime now = LocalDateTime.now();
LocalDateTime specificDateTime = LocalDateTime.of(2025, 12, 21, 10, 30);
LocalDateTime future = specificDateTime.plusDays(5);
LocalDateTime past = specificDateTime.minusHours(2);
int year = specificDateTime.getYear();
```

---

## 2. java.time.format.DateTimeFormatter

**Description:**
`DateTimeFormatter` is used for formatting and parsing date-time objects.

**Key Features:**

* Predefined formats like `ISO_LOCAL_DATE_TIME`.
* Custom patterns, e.g., `"dd/MM/yyyy HH:mm:ss"`.
* Immutable and thread-safe.

**Usage Examples:**

```java
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

LocalDateTime now = LocalDateTime.now();

// Predefined formatter
DateTimeFormatter formatter1 = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
System.out.println(now.format(formatter1));

// Custom formatter
DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
String formattedDate = now.format(formatter2);
System.out.println(formattedDate);

// Parsing string to LocalDateTime
LocalDateTime parsedDate = LocalDateTime.parse("21/12/2025 15:45:00", formatter2);
System.out.println(parsedDate);
```

---

## 3. java.time.temporal.ChronoUnit

**Description:**
`ChronoUnit` is an enum that represents standard units of time for calculations.

**Key Features:**

* Units: `NANOS`, `SECONDS`, `MINUTES`, `HOURS`, `DAYS`, `WEEKS`, `MONTHS`, `YEARS`.
* Can add/subtract time units or calculate differences.

**Usage Examples:**

```java
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

LocalDateTime start = LocalDateTime.of(2025, 12, 21, 10, 0);
LocalDateTime end = LocalDateTime.of(2025, 12, 25, 15, 0);

// Difference in days
long daysBetween = ChronoUnit.DAYS.between(start, end);
System.out.println(daysBetween);

// Adding 3 hours
LocalDateTime later = start.plus(3, ChronoUnit.HOURS);
System.out.println(later);

// Subtracting 2 months
LocalDateTime earlier = start.minus(2, ChronoUnit.MONTHS);
System.out.println(earlier);
```

---

## Summary Table

| Class             | Purpose                        | Key Feature                 | Example Use                                            |
| ----------------- | ------------------------------ | --------------------------- | ------------------------------------------------------ |
| LocalDateTime     | Represents date & time         | No time zone                | `LocalDateTime.now()`                                  |
| DateTimeFormatter | Format/parse date-time         | Custom patterns             | `dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))` |
| ChronoUnit        | Units of time for calculations | Enum for days, months, etc. | `ChronoUnit.DAYS.between(dt1, dt2)`                    |

---

This README provides a concise guide to using `LocalDateTime`, `DateTimeFormatter`, and `ChronoUnit` for modern Java date-time operations.
