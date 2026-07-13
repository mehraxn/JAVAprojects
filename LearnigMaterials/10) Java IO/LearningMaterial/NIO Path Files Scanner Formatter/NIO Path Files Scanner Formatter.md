# Java NIO `Path`, `Files`, Scanner, and Formatter - Complete Guide

## Overview
Your existing IO material covers classic readers/writers and serialization. This supplement covers modern file handling using `java.nio.file.Path` and `java.nio.file.Files`, plus `Scanner` and formatted output.

---

## 1. `Path`

A `Path` represents a file or directory path.

```java
import java.nio.file.Path;

Path path = Path.of("data", "students.txt");
System.out.println(path);
```

This is better than manually writing separators like `/` or `\`.

---

## 2. `Files.exists`

```java
import java.nio.file.Files;
import java.nio.file.Path;

Path path = Path.of("data.txt");

if (Files.exists(path)) {
    System.out.println("File exists");
} else {
    System.out.println("File does not exist");
}
```

---

## 3. Reading all lines

```java
Path path = Path.of("students.txt");
List<String> lines = Files.readAllLines(path);

for (String line : lines) {
    System.out.println(line);
}
```

This is simple for small files.

For large files, prefer streaming:

```java
try (Stream<String> lines = Files.lines(path)) {
    lines.filter(line -> !line.isBlank())
         .forEach(System.out::println);
}
```

---

## 4. Writing strings

```java
Path path = Path.of("output.txt");
Files.writeString(path, "Hello Java\n");
```

Write multiple lines:

```java
List<String> lines = List.of("one", "two", "three");
Files.write(path, lines);
```

---

## 5. Copying files

```java
Path source = Path.of("input.txt");
Path target = Path.of("copy.txt");

Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
```

Import:

```java
import java.nio.file.StandardCopyOption;
```

---

## 6. Reader and `readLine` important detail

`Reader` is a general abstract class for character input, but it does **not** have `readLine()`.

This does not compile:

```java
Reader r = new FileReader("data.txt");
r.readLine(); // compile error
```

Use `BufferedReader`:

```java
BufferedReader r = new BufferedReader(new FileReader("data.txt"));
String line = r.readLine();
```

---

## 7. Scanner

`Scanner` can parse input.

```java
Scanner scanner = new Scanner(System.in);
System.out.print("Enter age: ");
int age = scanner.nextInt();
```

Reading file with scanner:

```java
try (Scanner scanner = new Scanner(Path.of("data.txt"))) {
    while (scanner.hasNextLine()) {
        System.out.println(scanner.nextLine());
    }
}
```

---

## 8. Formatter / printf

```java
String name = "Sara";
int age = 22;

System.out.printf("Name: %s, Age: %d%n", name, age);
```

Common format symbols:

| Symbol | Meaning |
|---|---|
| `%s` | string |
| `%d` | integer |
| `%f` | floating point |
| `%.2f` | floating point with 2 decimals |
| `%n` | platform-independent newline |

Example:

```java
double price = 12.3456;
System.out.printf("Price: %.2f%n", price); // Price: 12.35
```

---

## Common mistakes

### Mistake 1: using `Reader.readLine()`
Use `BufferedReader.readLine()`.

### Mistake 2: reading huge files with `readAllLines`
Use `Files.lines` for large files.

### Mistake 3: forgetting try-with-resources for streams/scanners
Close resources automatically.

---

## Mini quiz

### Q1. Which class represents file paths in modern Java?
Answer: `Path`.

### Q2. Which class has `readLine()`?
Answer: `BufferedReader`.

### Q3. Which method reads a file as a stream of lines?
Answer: `Files.lines(path)`.
