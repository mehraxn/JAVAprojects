# Java Optional - Complete Guide

## Overview
`Optional<T>` represents a value that may or may not be present. It helps avoid returning `null` and makes absence explicit.

---

## 1. Why Optional exists

Without Optional:

```java
public Student findStudent(String id) {
    // return student if found
    // return null if not found
}
```

Caller must remember to check null:

```java
Student s = findStudent("S1");
if (s != null) {
    System.out.println(s.getName());
}
```

With Optional:

```java
public Optional<Student> findStudent(String id) {
    // Optional.of(student) if found
    // Optional.empty() if not found
}
```

Now the method signature clearly says the student may be absent.

---

## 2. Creating Optional

```java
Optional<String> a = Optional.of("hello");
Optional<String> b = Optional.empty();
Optional<String> c = Optional.ofNullable(possiblyNullValue);
```

Important:

```java
Optional.of(null); // NullPointerException
```

Use `ofNullable` if value may be null.

---

## 3. Checking and getting value

```java
Optional<String> name = Optional.of("Sara");

if (name.isPresent()) {
    System.out.println(name.get());
}
```

This works, but modern style avoids direct `get()` when possible.

---

## 4. `orElse` and `orElseGet`

```java
String result = name.orElse("Unknown");
```

If optional has value, returns it. Otherwise returns default.

Lazy default:

```java
String result = name.orElseGet(() -> loadDefaultName());
```

`orElseGet` calls the supplier only if needed.

---

## 5. `ifPresent`

```java
name.ifPresent(n -> System.out.println(n));
```

With method reference:

```java
name.ifPresent(System.out::println);
```

---

## 6. `map`

Transform value if present.

```java
Optional<Student> student = findStudent("S1");
Optional<String> studentName = student.map(Student::getName);
```

If student is empty, result is empty.

---

## 7. Optional with streams

Stream terminal operations often return Optional:

```java
Optional<Integer> max = numbers.stream().max(Integer::compareTo);
```

Why? The stream might be empty.

```java
int result = numbers.stream()
    .max(Integer::compareTo)
    .orElse(0);
```

---

## 8. `orElseThrow`

```java
Student student = findStudent("S1")
    .orElseThrow(() -> new IllegalArgumentException("Student not found"));
```

Use this when absence is an error.

---

## Common mistakes

### Mistake 1: using `get()` without checking

```java
optional.get(); // can throw NoSuchElementException
```

### Mistake 2: using Optional for fields everywhere
Usually Optional is best for return types, not necessarily object fields.

### Mistake 3: returning null instead of Optional.empty
If return type is Optional, never return null.

---

## Mini quiz

### Q1. What does `Optional.empty()` mean?
Answer: no value is present.

### Q2. Which method safely gives default value?
Answer: `orElse` or `orElseGet`.

### Q3. Why does stream `max()` return Optional?
Answer: because the stream may be empty.
