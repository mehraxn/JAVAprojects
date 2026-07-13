# Java Date Time: Formatting, Duration, and Period - Complete Guide

## Overview
Your existing date/time file covers key classes. This supplement explains formatting and the difference between `Duration` and `Period`.

---

## 1. Core classes

| Class | Meaning |
|---|---|
| `LocalDate` | date only |
| `LocalTime` | time only |
| `LocalDateTime` | date and time, no timezone |
| `ZonedDateTime` | date and time with timezone |
| `Instant` | machine timestamp |

---

## 2. Formatting dates

```java
LocalDate date = LocalDate.of(2026, 7, 3);
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

String text = date.format(formatter);
System.out.println(text); // 03/07/2026
```

---

## 3. Parsing dates

```java
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
LocalDate date = LocalDate.parse("03/07/2026", formatter);
```

---

## 4. Period

`Period` measures date-based amounts: years, months, days.

```java
LocalDate start = LocalDate.of(2026, 7, 1);
LocalDate end = LocalDate.of(2026, 7, 10);

Period period = Period.between(start, end);
System.out.println(period.getDays()); // 9
```

Use `Period` for calendar dates.

---

## 5. Duration

`Duration` measures time-based amounts: seconds, minutes, hours.

```java
LocalTime start = LocalTime.of(10, 0);
LocalTime end = LocalTime.of(12, 30);

Duration duration = Duration.between(start, end);
System.out.println(duration.toMinutes()); // 150
```

Use `Duration` for exact time intervals.

---

## 6. Comparing dates

```java
LocalDate today = LocalDate.now();
LocalDate deadline = LocalDate.of(2026, 7, 10);

if (today.isBefore(deadline)) {
    System.out.println("Still time");
}
```

Methods:

```java
isBefore
isAfter
isEqual
```

---

## 7. Adding/subtracting time

```java
LocalDate nextWeek = LocalDate.now().plusWeeks(1);
LocalDate yesterday = LocalDate.now().minusDays(1);
LocalDateTime later = LocalDateTime.now().plusHours(2);
```

---

## Common mistakes

### Mistake 1: using old `Date`/`Calendar` for new code
Prefer `java.time` classes.

### Mistake 2: using `Duration` for months
Months have variable length. Use `Period` for date-based amounts.

### Mistake 3: forgetting `LocalDateTime` has no timezone
Use `ZonedDateTime` if timezone matters.

---

## Mini quiz

### Q1. Which class stores only date?
Answer: `LocalDate`.

### Q2. Which class measures hours/minutes/seconds?
Answer: `Duration`.

### Q3. Which class measures years/months/days?
Answer: `Period`.
