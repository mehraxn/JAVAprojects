# JUnit Basics, Assertions, and Test Lifecycle - Complete Guide

## Overview
JUnit is used to automatically test Java code. A test checks whether your code behaves as expected.

---

## 1. What is a unit test?

A unit test checks a small part of code, usually one method.

Example method:

```java
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
}
```

JUnit test:

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {
    @Test
    void addShouldReturnSum() {
        Calculator calculator = new Calculator();
        assertEquals(5, calculator.add(2, 3));
    }
}
```

---

## 2. Assertions

Assertions check expected results.

```java
assertEquals(expected, actual);
assertTrue(condition);
assertFalse(condition);
assertNull(value);
assertNotNull(value);
assertThrows(ExceptionType.class, () -> codeThatThrows());
```

Example:

```java
assertTrue("hello".startsWith("h"));
assertFalse("hello".isBlank());
```

---

## 3. Testing exceptions

```java
@Test
void divideByZeroShouldThrow() {
    Calculator calculator = new Calculator();

    assertThrows(ArithmeticException.class, () -> {
        calculator.divide(10, 0);
    });
}
```

This test passes only if the expected exception is thrown.

---

## 4. Failure vs error

### Failure
A failure means an assertion failed.

```java
assertEquals(5, 2 + 2); // failure
```

The code executed, but result was not expected.

### Error
An error means an unexpected exception happened during the test.

```java
String text = null;
text.length(); // NullPointerException -> error if not expected
```

Exam summary:

| JUnit result | Meaning |
|---|---|
| Failure | wrong assertion / `fail()` |
| Error | unexpected runtime exception |

---

## 5. Test lifecycle

Common annotations:

```java
@BeforeEach
void setUp() { }

@AfterEach
void tearDown() { }

@BeforeAll
static void beforeAll() { }

@AfterAll
static void afterAll() { }
```

Example:

```java
class CalculatorTest {
    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @Test
    void addShouldReturnSum() {
        assertEquals(5, calculator.add(2, 3));
    }
}
```

---

## 6. Arrange, Act, Assert

A good test often has three parts:

```java
@Test
void withdrawShouldDecreaseBalance() {
    // Arrange
    BankAccount account = new BankAccount(100);

    // Act
    account.withdraw(30);

    // Assert
    assertEquals(70, account.getBalance());
}
```

---

## 7. Naming tests

Good test names explain behavior:

```java
withdrawShouldDecreaseBalance()
reserveTourShouldFailWhenCapacityIsFull()
loginShouldRejectWrongPassword()
```

---

## Common mistakes

### Mistake 1: writing tests without assertions
A test without assertions may not really verify behavior.

### Mistake 2: testing too much in one test
Keep tests focused.

### Mistake 3: confusing failure and error
Wrong assertion = failure. Unexpected exception = error.

---

## Mini quiz

### Q1. What causes a JUnit failure?
Answer: failed assertion or `fail()`.

### Q2. What causes a JUnit error?
Answer: unexpected exception.

### Q3. Which assertion checks exceptions?
Answer: `assertThrows`.
