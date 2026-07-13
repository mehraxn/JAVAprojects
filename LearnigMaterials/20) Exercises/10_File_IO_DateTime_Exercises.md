# 10 — File IO and Date/Time Exercises

Topics: `Reader`/`Writer`, `BufferedReader`, NIO `Path`/`Files`, `LocalDate`/`LocalTime`/
`LocalDateTime`, `DateTimeFormatter`, `Duration`/`Period`, `ChronoUnit`.

**Note:** The IO examples are **learning examples only** — do not run them; there is no real
file here. Read them to understand the API. Code checked by static review.

---

## Part A — File IO (read as learning examples)

## Exercise 1 — Read a file line by line

Write code that reads and prints every line of `"data.txt"` using `BufferedReader`.

### Solution
```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

try (BufferedReader reader = new BufferedReader(new FileReader("data.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
} catch (IOException e) {
    System.out.println("Error reading file: " + e.getMessage());
}
```
`BufferedReader.readLine()` returns `null` at end of file.

---

## Exercise 2 — Write text to a file

Write `"Hello file"` to `"out.txt"` using a `BufferedWriter`.

### Solution
```java
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

try (BufferedWriter writer = new BufferedWriter(new FileWriter("out.txt"))) {
    writer.write("Hello file");
    writer.newLine();
} catch (IOException e) {
    System.out.println("Error writing file: " + e.getMessage());
}
```

---

## Exercise 3 — Modern NIO

Rewrite reading all lines and writing a string using `java.nio.file.Files`.

### Solution
```java
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

Path in = Path.of("data.txt");
List<String> lines = Files.readAllLines(in);   // throws IOException (declare/catch)

Path out = Path.of("out.txt");
Files.writeString(out, "Hello NIO");
```

---

## Exercise 4 — Fix the bug (Reader has no readLine)

This does not compile. Fix it.
```java
import java.io.FileReader;

FileReader reader = new FileReader("data.txt");
String line = reader.readLine(); // error: no such method
```

### Solution
`readLine()` belongs to `BufferedReader`, not `Reader`/`FileReader`. Wrap it:
```java
BufferedReader reader = new BufferedReader(new FileReader("data.txt"));
String line = reader.readLine();
```

---

## Part B — Date and Time

## Exercise 5 — Create dates and times

Create the date 2026-07-04, the time 14:30, and combine them into a `LocalDateTime`. Print all
three.

**Expected output:**
```
2026-07-04
14:30
2026-07-04T14:30
```

### Solution
```java
import java.time.*;

LocalDate date = LocalDate.of(2026, 7, 4);
LocalTime time = LocalTime.of(14, 30);
LocalDateTime dt = LocalDateTime.of(date, time);

System.out.println(date); // 2026-07-04
System.out.println(time); // 14:30
System.out.println(dt);   // 2026-07-04T14:30
```

---

## Exercise 6 — Format a date

Format `LocalDate.of(2026, 7, 4)` as `04/07/2026`.

**Expected output:**
```
04/07/2026
```

### Solution
```java
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

LocalDate d = LocalDate.of(2026, 7, 4);
DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
System.out.println(d.format(fmt)); // 04/07/2026
```

---

## Exercise 7 — Duration vs Period

Compute the minutes between 09:00 and 11:30, and the days part of the period between
2026-01-10 and 2026-01-25.

**Expected output:**
```
150
15
```

### Solution
```java
import java.time.*;

Duration dur = Duration.between(LocalTime.of(9, 0), LocalTime.of(11, 30));
System.out.println(dur.toMinutes()); // 150

Period p = Period.between(LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 25));
System.out.println(p.getDays()); // 15
```

---

## Exercise 8 — ChronoUnit.between

Print the total number of days between 2026-01-01 and 2026-02-01.

**Expected output:**
```
31
```

### Solution
```java
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

long days = ChronoUnit.DAYS.between(
    LocalDate.of(2026, 1, 1),
    LocalDate.of(2026, 2, 1));
System.out.println(days); // 31 (January has 31 days)
```

---

## Exercise 9 — Predict the output (immutability)

```java
LocalDate d = LocalDate.of(2026, 1, 10);
d.plusDays(5);
System.out.println(d);
```

### Solution
```
2026-01-10
```
`java.time` objects are immutable; `plusDays` returns a new date that is ignored here. To keep
it: `d = d.plusDays(5);`.

---

## Exercise 10 — Fix the bug (parse pattern mismatch)

This throws `DateTimeParseException`. Fix it two ways.
```java
DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
LocalDate d = LocalDate.parse("2026-07-04", fmt);
```

### Solution
The text and the pattern must match. Either change the pattern to match the text:
```java
DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
LocalDate d = LocalDate.parse("2026-07-04", fmt);
```
or (since the text is ISO format) parse without a formatter:
```java
LocalDate d = LocalDate.parse("2026-07-04"); // ISO by default
```

---

## Challenge — Days until a deadline

Write a method `long daysUntil(LocalDate deadline)` that returns the number of days from
**today** to the deadline (may be negative if past). Explain why the answer depends on the day
you run it.

### Solution
```java
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

static long daysUntil(LocalDate deadline) {
    return ChronoUnit.DAYS.between(LocalDate.now(), deadline);
}
```
The result uses `LocalDate.now()`, so it changes each day the program runs. (We do not run it
here — output depends on the current date.)
