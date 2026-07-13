# 08 — Exceptions Exercises

Topics: `try`/`catch`/`finally`, `throw`/`throws`, checked vs unchecked, custom exceptions,
try-with-resources. Solutions under each **Solution** heading. Code checked by static review.

---

## Exercise 1 — Catch an exception

Write code that divides `10 / 0` inside a `try` and prints `"Cannot divide by zero"` in the
`catch`.

**Expected output:**
```
Cannot divide by zero
```

### Solution
```java
try {
    int result = 10 / 0;
    System.out.println(result);
} catch (ArithmeticException e) {
    System.out.println("Cannot divide by zero");
}
```

---

## Exercise 2 — Throw your own exception

Write `void setAge(int age)` that throws `IllegalArgumentException` if `age < 0`.

**Expected output** (calling `setAge(-1)` inside a try/catch printing the message):
```
Age cannot be negative
```

### Solution
```java
static void setAge(int age) {
    if (age < 0) {
        throw new IllegalArgumentException("Age cannot be negative");
    }
}
// try { setAge(-1); } catch (IllegalArgumentException e) { System.out.println(e.getMessage()); }
```

---

## Exercise 3 — Custom exception class

Create a checked exception `InsufficientFundsException` and a `withdraw` method that throws it.

### Solution
```java
class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

class Account {
    private double balance = 100;
    void withdraw(double amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException("Not enough money");
        }
        balance -= amount;
    }
}
```
Extending `Exception` (not `RuntimeException`) makes it **checked**, so callers must handle or
declare it.

---

## Exercise 4 — try / catch / finally

Write code that prints `try`, then `finally`, even though there is no exception.

**Expected output:**
```
in try
in finally
```

### Solution
```java
try {
    System.out.println("in try");
} finally {
    System.out.println("in finally");
}
```

---

## Exercise 5 — try-with-resources (learning example)

Show how try-with-resources closes a `BufferedReader` automatically. *(This is a learning
example — do not run it; there is no file here.)*

### Solution
```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

try (BufferedReader reader = new BufferedReader(new FileReader("data.txt"))) {
    System.out.println(reader.readLine());
} catch (IOException e) {
    System.out.println("Could not read file");
}
// reader.close() is called automatically at the end of the try block
```

---

## Exercise 6 — Predict the output

```java
public class Main {
    static int test() {
        try {
            return 1;
        } finally {
            System.out.println("finally runs");
        }
    }
    public static void main(String[] args) {
        System.out.println(test());
    }
}
```

### Solution
```
finally runs
1
```
`finally` runs before the method actually returns; then the returned value `1` is printed.

---

## Exercise 7 — Predict the output (checked or unchecked?)

For each, say whether it is checked or unchecked:
`NullPointerException`, `IOException`, `ArithmeticException`, `SQLException`,
`ArrayIndexOutOfBoundsException`.

### Solution
- `NullPointerException` — unchecked
- `IOException` — checked
- `ArithmeticException` — unchecked
- `SQLException` — checked
- `ArrayIndexOutOfBoundsException` — unchecked
(Unchecked = subclasses of `RuntimeException`.)

---

## Exercise 8 — Fix the bug (catch order)

This does not compile. Fix it.
```java
try {
    riskyIO();
} catch (Exception e) {
    System.out.println("general");
} catch (IOException e) {           // error: unreachable
    System.out.println("io");
}
```

### Solution
The broad `Exception` catch hides the specific `IOException` one. Put the **specific** catch
first:
```java
try {
    riskyIO();
} catch (IOException e) {
    System.out.println("io");
} catch (Exception e) {
    System.out.println("general");
}
```

---

## Exercise 9 — Fix the bug (swallowed exception)

This hides problems: if parsing fails, the program silently continues with wrong data. Improve
the handling so the error is visible.
```java
int value = 0;
try {
    value = Integer.parseInt("abc");
} catch (NumberFormatException e) {
    // nothing here
}
System.out.println(value);
```

### Solution
Do not swallow exceptions silently — at least report them (or rethrow):
```java
int value = 0;
try {
    value = Integer.parseInt("abc");
} catch (NumberFormatException e) {
    System.out.println("Invalid number, using default: " + e.getMessage());
}
System.out.println(value); // prints the message, then 0
```

---

## Challenge — Validate and report

Write `parsePositive(String s)` that returns the parsed positive int, throws
`IllegalArgumentException` if it is not positive, and lets `NumberFormatException` propagate for
non-numbers. Then call it inside a try/catch for both cases.

### Solution
```java
static int parsePositive(String s) {
    int n = Integer.parseInt(s); // may throw NumberFormatException
    if (n <= 0) {
        throw new IllegalArgumentException("Must be positive: " + n);
    }
    return n;
}

public static void main(String[] args) {
    try {
        System.out.println(parsePositive("5"));   // 5
        System.out.println(parsePositive("-3"));  // throws IllegalArgumentException
    } catch (NumberFormatException e) {
        System.out.println("Not a number");
    } catch (IllegalArgumentException e) {
        System.out.println(e.getMessage());       // Must be positive: -3
    }
}
```
Expected output:
```
5
Must be positive: -3
```
