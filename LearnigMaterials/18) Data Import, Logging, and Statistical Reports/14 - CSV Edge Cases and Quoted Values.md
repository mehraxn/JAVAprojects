# CSV Edge Cases and Quoted Values

## Learning goals

- Understand why simple CSV parsing can fail.
- Recognize quoted values, commas inside values, escaped quotes, and empty fields.
- Know when a CSV library is better.

## Why this topic matters

For simple learning examples, `String.split(",")` is enough to understand the workflow. Real CSV files are more complicated.

Example:

```text
"John Smith","New York, USA",25
```

`String.split(",")` breaks this into the wrong pieces because the comma inside quotes is part of the city value.

## Common CSV edge cases

- Quoted values.
- Commas inside quoted values.
- Escaped quotes.
- Empty fields.
- Extra spaces.
- Missing headers.
- Header columns in the wrong order.

## Dependency-free teaching parser idea

For learning, you can write a small parser that tracks whether it is inside quotes.

```java
List<String> fields = new ArrayList<>();
StringBuilder current = new StringBuilder();
boolean inQuotes = false;

for (int i = 0; i < line.length(); i++) {
    char ch = line.charAt(i);
    if (ch == '"') {
        inQuotes = !inQuotes;
    } else if (ch == ',' && !inQuotes) {
        fields.add(current.toString());
        current.setLength(0);
    } else {
        current.append(ch);
    }
}
fields.add(current.toString());
```

This teaches the idea, but full CSV rules are more complex.

## When to use a library

Use a CSV library in real applications when files may contain quotes, embedded commas, new lines inside values, or escaped characters.

## Common mistakes

- Assuming every CSV is simple.
- Ignoring headers.
- Not preserving empty fields.
- Treating parsing errors as valid data.

## Mini exercises

1. Explain why `String.split(",")` fails for `"John Smith","New York, USA",25`.
2. Validate a header line.
3. Decide when to use a library instead of custom parsing.

## Quick summary

Simple split is useful for learning, but real CSV has quoting rules that need careful parsing.
