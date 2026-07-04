# 11 — JUnit Exercises

Topics: writing tests, assertions, test lifecycle (`@BeforeEach`/`@AfterEach`), failure vs
error, `assertThrows`.

**Note:** These are **learning examples only**. They show how JUnit 5 test code looks and what
it checks — they are **not** a runnable project here (no JUnit library is installed). Read them
to practise recognising correct tests. Code checked by static review.

---

## The class under test

All exercises test this small class:
```java
public class Calculator {
    public int add(int a, int b) { return a + b; }
    public int divide(int a, int b) {
        if (b == 0) throw new IllegalArgumentException("Cannot divide by zero");
        return a / b;
    }
}
```

---

## Exercise 1 — Write a basic test

Write a JUnit 5 test that checks `add(2, 3)` returns `5`.

### Solution
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorTest {
    @Test
    void addsTwoNumbers() {
        Calculator calc = new Calculator();
        assertEquals(5, calc.add(2, 3));
    }
}
```
`assertEquals(expected, actual)` — expected value comes **first**.

---

## Exercise 2 — Test the lifecycle with @BeforeEach

Use `@BeforeEach` to create a fresh `Calculator` before each test.

### Solution
```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorTest {
    private Calculator calc;

    @BeforeEach
    void setUp() {
        calc = new Calculator(); // runs before EVERY @Test
    }

    @Test
    void addsPositives() { assertEquals(7, calc.add(3, 4)); }

    @Test
    void addsNegatives() { assertEquals(-1, calc.add(-3, 2)); }
}
```

---

## Exercise 3 — Assert an exception with assertThrows

Write a test that `divide(1, 0)` throws `IllegalArgumentException`.

### Solution
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Test
void divideByZeroThrows() {
    Calculator calc = new Calculator();
    assertThrows(IllegalArgumentException.class, () -> calc.divide(1, 0));
}
```

---

## Exercise 4 — Common assertions

Write one test method that uses `assertEquals`, `assertTrue`, and `assertFalse`.

### Solution
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@Test
void variousAssertions() {
    Calculator calc = new Calculator();
    assertEquals(6, calc.add(2, 4));
    assertTrue(calc.add(2, 2) == 4);
    assertFalse(calc.add(2, 2) == 5);
}
```

---

## Exercise 5 — Failure vs error (concept)

For each scenario, say whether JUnit reports a **failure** or an **error**:
1. `assertEquals(5, calc.add(2, 2))` when add returns 4.
2. `calc.divide(1, 0)` is called directly in a test (not inside `assertThrows`).

### Solution
1. **Failure** — an assertion did not hold (expected 5, got 4).
2. **Error** — an unexpected exception (`IllegalArgumentException`) was thrown during the test.
(Failure = assertion failed; Error = unexpected exception.)

---

## Exercise 6 — Predict the result

Given `add` works correctly, does this test **pass**, **fail**, or **error**?
```java
@Test
void check() {
    Calculator calc = new Calculator();
    assertEquals(5, calc.add(2, 2));
}
```

### Solution
It **fails**. `add(2, 2)` returns `4`, but the test expects `5` — an assertion failure (not an
error, because no unexpected exception was thrown).

---

## Exercise 7 — Fix the bug (arguments reversed)

This test technically passes but the arguments are in the wrong order, which produces confusing
failure messages. Fix it.
```java
@Test
void addTest() {
    Calculator calc = new Calculator();
    assertEquals(calc.add(2, 3), 5); // wrong order
}
```

### Solution
JUnit's convention is `assertEquals(expected, actual)`:
```java
assertEquals(5, calc.add(2, 3));
```
With the wrong order, a failure message would say "expected 5 but was ..." backwards, which is
misleading.

---

## Exercise 8 — Fix the bug (missing @Test)

This method never runs as a test. Why, and how to fix?
```java
class CalculatorTest {
    void addsNumbers() {
        assertEquals(4, new Calculator().add(2, 2));
    }
}
```

### Solution
Without the `@Test` annotation, JUnit does not recognise it as a test. Add it (and the static
import):
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Test
void addsNumbers() {
    assertEquals(4, new Calculator().add(2, 2));
}
```

---

## Challenge — Test plan (theory)

For a `String isPalindrome(String s)` method, list at least four test cases you would write
(input → expected).

### Solution (example answer)
| Input | Expected |
|---|---|
| `"racecar"` | `true` |
| `"hello"` | `false` |
| `""` (empty) | `true` |
| `"a"` (single char) | `true` |
| `"Aba"` (case sensitivity) | depends on spec — document the decision |

Good tests cover normal cases, edge cases (empty, single char), and a "tricky" case (casing).
