# Reading CSV Files in Java

## Learning goals

- Read text files with `Files.newBufferedReader`.
- Use `StandardCharsets.UTF_8`.
- Understand the basic limits of `String.split`.

## Basic CSV reading

```java
Path path = Path.of("products.csv");

try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
    String header = reader.readLine();
    String line;
    while ((line = reader.readLine()) != null) {
        String[] columns = line.split(",");
        System.out.println(Arrays.toString(columns));
    }
}
```

## Why specify UTF-8?

Using the default charset can behave differently on different machines. `StandardCharsets.UTF_8` makes the choice explicit.

## Empty lines

```java
if (line.trim().isEmpty()) {
    continue;
}
```

## Limitation of `String.split`

`String.split(",")` is fine for simple learning examples, but real CSV can contain quoted commas:

```text
P100,"Notebook, large",12.99
```

For real applications, use a CSV library.

## Common mistakes

- Forgetting try-with-resources.
- Using default charset.
- Assuming every row has the correct number of columns.
- Treating headers as data.

## Mini exercise

Read a `students.csv` file with header `id,name,score` and print each parsed row.

## Quick summary

For beginner examples, `BufferedReader` and UTF-8 are enough to learn the import workflow.
