# Checked Exceptions, Unchecked Exceptions, and Try-With-Resources - Complete Guide

## Overview
Java exceptions are objects that represent errors or abnormal situations. The key exam distinction is between checked and unchecked exceptions.

---

## 1. Exception hierarchy

```text
Throwable
├── Error
└── Exception
    ├── RuntimeException
    └── checked exceptions
```

`Error` usually represents serious JVM/system problems and is normally not handled.

---

## 2. Checked exceptions

Checked exceptions must be handled or declared.

Example:

```java
public static void readFile(String path) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(path));
    System.out.println(reader.readLine());
    reader.close();
}
```

Caller must handle:

```java
try {
    readFile("data.txt");
} catch (IOException e) {
    System.out.println("Could not read file");
}
```

Common checked exceptions:

- `IOException`
- `SQLException`
- `ClassNotFoundException`

---

## 3. Unchecked exceptions

Unchecked exceptions extend `RuntimeException`. They do not need to be declared or caught.

Examples:

- `NullPointerException`
- `IllegalArgumentException`
- `IndexOutOfBoundsException`
- `ArithmeticException`

Example:

```java
int x = 10 / 0; // ArithmeticException
```

---

## 4. `throw` vs `throws`

### `throw`
Actually throws an exception object.

```java
throw new IllegalArgumentException("Invalid age");
```

### `throws`
Declares that a method may throw an exception.

```java
public void read() throws IOException { }
```

---

## 5. `finally`

`finally` runs after try/catch, usually even if there is an exception or return.

```java
try {
    System.out.println("try");
} catch (Exception e) {
    System.out.println("catch");
} finally {
    System.out.println("finally");
}
```

Use `finally` for cleanup, but modern Java often uses try-with-resources.

---

## 6. Try-with-resources

A resource is something that should be closed, like a file or database connection.

```java
try (BufferedReader reader = new BufferedReader(new FileReader("data.txt"))) {
    String line = reader.readLine();
    System.out.println(line);
} catch (IOException e) {
    System.out.println("File error: " + e.getMessage());
}
```

The reader is automatically closed.

Resource classes must implement `AutoCloseable`.

---

## 7. Multiple catch blocks

```java
try {
    // risky code
} catch (FileNotFoundException e) {
    System.out.println("File not found");
} catch (IOException e) {
    System.out.println("Other IO error");
}
```

Order matters: catch more specific exceptions first.

Wrong:

```java
catch (IOException e) { }
catch (FileNotFoundException e) { } // unreachable
```

---

## 8. Custom exception

```java
public class InvalidGradeException extends Exception {
    public InvalidGradeException(String message) {
        super(message);
    }
}
```

Use:

```java
public void setGrade(int grade) throws InvalidGradeException {
    if (grade < 0 || grade > 30) {
        throw new InvalidGradeException("Grade must be between 0 and 30");
    }
}
```

---

## Common mistakes

### Mistake 1: catching `Exception` too early
It hides specific errors.

### Mistake 2: forgetting checked exceptions must be handled or declared
The compiler enforces this.

### Mistake 3: manually closing resources without finally/try-with-resources
Use try-with-resources.

---

## Mini quiz

### Q1. Is `IOException` checked or unchecked?
Answer: checked.

### Q2. Is `NullPointerException` checked or unchecked?
Answer: unchecked.

### Q3. What closes resources automatically?
Answer: try-with-resources.
