# JUnit Testing Patterns and Examples - Complete Guide

## Overview
This file gives practical examples of how to test normal return values, invalid inputs, collections, and exceptions.

---

## 1. Testing a simple method

Production code:

```java
public class StringUtils {
    public boolean isLong(String text) {
        return text != null && text.length() > 5;
    }
}
```

Test:

```java
@Test
void isLongShouldReturnTrueForLongText() {
    StringUtils utils = new StringUtils();
    assertTrue(utils.isLong("elephant"));
}

@Test
void isLongShouldReturnFalseForShortText() {
    StringUtils utils = new StringUtils();
    assertFalse(utils.isLong("cat"));
}
```

---

## 2. Testing boundary values

If a method validates age:

```java
public boolean canRegister(int age) {
    return age >= 18;
}
```

Test around the boundary:

```java
@Test
void canRegisterShouldReject17() {
    assertFalse(service.canRegister(17));
}

@Test
void canRegisterShouldAccept18() {
    assertTrue(service.canRegister(18));
}
```

Boundary values are important in exams and real projects.

---

## 3. Testing collections

```java
@Test
void filterEvenNumbersShouldReturnOnlyEvens() {
    List<Integer> result = NumberUtils.filterEven(List.of(1, 2, 3, 4));
    assertEquals(List.of(2, 4), result);
}
```

If order does not matter, compare sets:

```java
assertEquals(Set.of("a", "b"), new HashSet<>(result));
```

---

## 4. Testing exceptions

Production code:

```java
public void setGrade(int grade) {
    if (grade < 0 || grade > 30) {
        throw new IllegalArgumentException("Invalid grade");
    }
    this.grade = grade;
}
```

Test:

```java
@Test
void setGradeShouldRejectNegativeGrade() {
    Student student = new Student();

    assertThrows(IllegalArgumentException.class, () -> {
        student.setGrade(-1);
    });
}
```

---

## 5. Testing floating point numbers

Floating point values need tolerance/delta.

```java
assertEquals(3.14, actual, 0.001);
```

Why? Because decimal calculations may have tiny precision differences.

---

## 6. Testing with temporary objects

A test should create its own data when possible.

```java
@Test
void fullNameShouldCombineFirstAndLastName() {
    Student student = new Student("Sara", "Rossi");
    assertEquals("Sara Rossi", student.getFullName());
}
```

Avoid tests that depend on previous tests. Tests should be independent.

---

## 7. Bad test example

```java
@Test
void testEverything() {
    // tests login, payment, database, email all together
}
```

Problem: when it fails, you do not know exactly what broke.

Better: separate small tests.

---

## Common mistakes

### Mistake 1: relying on test execution order
Tests should pass in any order.

### Mistake 2: using exact equality for doubles
Use delta.

### Mistake 3: not testing invalid inputs
Good tests include both normal and invalid cases.

---

## Mini quiz

### Q1. What should you test near conditions like `age >= 18`?
Answer: boundary values, such as 17 and 18.

### Q2. How do you compare doubles in JUnit?
Answer: use `assertEquals(expected, actual, delta)`.

### Q3. Should tests depend on each other?
Answer: no.
