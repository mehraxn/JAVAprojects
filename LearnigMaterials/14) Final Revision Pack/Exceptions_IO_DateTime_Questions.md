# Exceptions, IO, and Date/Time Questions (with Answers)

Covers exception handling, Java IO, and the `java.time` date/time API. Theory, multiple
choice, and "predict the output". Answers follow each question. Code checked by static review.

---

## 1. Exceptions

**Q1.** What is the difference between a **checked** and an **unchecked** exception?
**Answer:** A **checked** exception (e.g. `IOException`) must be caught or declared with
`throws`; the compiler enforces this. An **unchecked** exception (subclasses of
`RuntimeException`, e.g. `NullPointerException`) does not have to be handled or declared.

**Q2 (MCQ).** Which is **unchecked**?
a. `IOException`  b. `SQLException`  c. `ArithmeticException`  d. `FileNotFoundException`
**Answer:** c. `ArithmeticException`.

**Q3.** Difference between `throw` and `throws`?
**Answer:** `throw` actually throws an exception object inside a method body; `throws` declares
in the method signature that the method may throw one.

**Q4.** What is the root class of all exceptions and errors?
**Answer:** `Throwable`. Below it: `Error` (serious, usually not caught) and `Exception`
(with `RuntimeException` as the unchecked branch).

**Q5 (predict).**
```java
try {
    int[] a = new int[2];
    System.out.println(a[5]);
} catch (ArrayIndexOutOfBoundsException e) {
    System.out.println("caught");
} finally {
    System.out.println("done");
}
```
**Answer:** `caught` then `done`. The bad index throws, the catch handles it, and `finally`
always runs.

**Q6 (predict).**
```java
static int f() {
    try {
        return 1;
    } finally {
        return 2;
    }
}
// System.out.println(f());
```
**Answer:** `2`. A `return` in `finally` overrides the one in `try` (avoid doing this).

**Q7.** What is try-with-resources and why use it?
**Answer:** A `try (Resource r = ...)` form that **auto-closes** the resource (anything
implementing `AutoCloseable`) when the block ends, even on exception — so you do not forget
`close()`.
```java
try (BufferedReader r = new BufferedReader(new FileReader("data.txt"))) {
    System.out.println(r.readLine());
} // r is closed automatically
```

**Q8.** Order of multiple `catch` blocks — does it matter?
**Answer:** Yes. More **specific** exceptions must come before more **general** ones, or the
code will not compile (a broad catch would make the later specific one unreachable).

---

## 2. Java IO

**Q9.** What is the difference between the Reader/Writer and InputStream/OutputStream families?
**Answer:** `Reader`/`Writer` handle **characters** (text); `InputStream`/`OutputStream` handle
**bytes** (any binary data).

**Q10 (MCQ / classic trap).** Which class provides `readLine()`?
a. `Reader`  b. `FileReader`  c. `BufferedReader`  d. `InputStream`
**Answer:** c. `BufferedReader`. Plain `Reader`/`FileReader` do **not** have `readLine()`.

**Q11.** How do you read a text file line by line the classic way?
**Answer:**
```java
try (BufferedReader r = new BufferedReader(new FileReader("data.txt"))) {
    String line;
    while ((line = r.readLine()) != null) {
        System.out.println(line);
    }
}
```

**Q12.** Give the modern NIO way to read all lines and to write a string to a file.
**Answer:**
```java
Path p = Path.of("data.txt");
List<String> lines = Files.readAllLines(p);
Files.writeString(Path.of("out.txt"), "hello");
```

**Q13.** Why wrap a `FileReader` in a `BufferedReader`?
**Answer:** Buffering reduces the number of physical read operations (better performance) and
adds convenient methods like `readLine()`.

---

## 3. Date and Time (`java.time`)

**Q14.** Which class stores only a date? Only a time? Both without a timezone?
**Answer:** `LocalDate` (date), `LocalTime` (time), `LocalDateTime` (both, no timezone).

**Q15.** Are `java.time` objects mutable?
**Answer:** No — they are **immutable**. Methods like `plusDays` return a **new** object.

**Q16 (predict).**
```java
LocalDate d = LocalDate.of(2026, 1, 10);
d.plusDays(5);
System.out.println(d);
```
**Answer:** `2026-01-10` (unchanged). The result of `plusDays` was ignored; the original is
immutable.

**Q17.** Difference between `Duration` and `Period`?
**Answer:** `Duration` measures **time** (hours, minutes, seconds), best with `LocalTime`/
`LocalDateTime`. `Period` measures **calendar** amounts (years, months, days), best with
`LocalDate`.

**Q18 (predict).**
```java
LocalTime a = LocalTime.of(9, 0);
LocalTime b = LocalTime.of(11, 30);
System.out.println(Duration.between(a, b).toMinutes());
```
**Answer:** `150`.

**Q19 (predict).**
```java
LocalDate a = LocalDate.of(2026, 1, 1);
LocalDate b = LocalDate.of(2026, 1, 11);
System.out.println(ChronoUnit.DAYS.between(a, b));
```
**Answer:** `10`.

**Q20.** What is the difference between `Period.between(a, b)` and `ChronoUnit.DAYS.between(a, b)`?
**Answer:** `Period.between` gives a **broken-down** amount (e.g. "2 months and 5 days");
`ChronoUnit.DAYS.between` gives a **single total** number of days.

**Q21.** Format vs parse — which direction is each?
**Answer:** **Format** = date object → `String`. **Parse** = `String` → date object. Both use
`DateTimeFormatter`.

**Q22 (predict — careful).**
```java
DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
LocalDate d = LocalDate.parse("10-01-2026", f);
```
**Answer:** Throws `DateTimeParseException`. The text uses `-` but the pattern expects `/`.

---

## Quick recap

- Checked = compiler-enforced; unchecked = `RuntimeException` family.
- `finally` almost always runs; never `return` from it.
- `readLine()` lives on `BufferedReader`, not `Reader`.
- `java.time` types are immutable; assign the result of `plus/minus`.
- `Duration` = time, `Period` = calendar; `ChronoUnit.between` = one total number.
