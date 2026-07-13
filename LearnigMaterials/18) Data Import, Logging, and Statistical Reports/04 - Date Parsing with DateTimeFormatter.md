# Date Parsing with DateTimeFormatter

## Learning goals

- Parse dates with `DateTimeFormatter`.
- Handle parse errors.
- Validate date ranges.

## LocalDate parsing

```java
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
LocalDate date = LocalDate.parse("2026-03-15", formatter);
```

## LocalDateTime parsing

```java
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
LocalDateTime createdAt = LocalDateTime.parse("2026-03-15 09:30", formatter);
```

## Handling parse errors

```java
try {
    LocalDate date = LocalDate.parse(text, formatter);
} catch (DateTimeParseException ex) {
    throw new IllegalArgumentException("Invalid date: " + text);
}
```

## Validating ranges

```java
if (end.isBefore(start)) {
    throw new IllegalArgumentException("End date must not be before start date");
}
```

## Null date handling

If a date is required, reject null or blank input. If a date is optional, document what null means.

## Common mistakes

- Parsing dates with string splitting.
- Accepting impossible ranges.
- Using different date formats in different places without documentation.

## Mini exercise

Parse an invoice date in `yyyy-MM-dd` format and reject blank or invalid values.

## Quick summary

Use `DateTimeFormatter` for clear, testable date parsing.
