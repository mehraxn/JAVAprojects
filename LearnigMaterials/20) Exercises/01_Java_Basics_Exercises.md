# 01 — Java Basics Exercises

Topics: variables, data types, operators, `String`, `if`/`switch`, loops.
Try each task on paper first. Solutions are under each **Solution** heading — cover them while
you work. No code is run here; outputs were checked by static review.

---

## Exercise 1 — Temperature label

Write a program that stores an `int temperature` and prints:
- `"Cold"` if below 10
- `"Warm"` if 10 to 25 (inclusive)
- `"Hot"` if above 25

**Expected output** (for `temperature = 20`):
```
Warm
```

### Solution
```java
public class TemperatureLabel {
    public static void main(String[] args) {
        int temperature = 20;
        if (temperature < 10) {
            System.out.println("Cold");
        } else if (temperature <= 25) {
            System.out.println("Warm");
        } else {
            System.out.println("Hot");
        }
    }
}
```

---

## Exercise 2 — Sum of 1..N with a loop

Read/assume `int n = 5;` and print the sum `1 + 2 + ... + n`.

**Expected output** (for `n = 5`):
```
15
```

### Solution
```java
int n = 5;
int sum = 0;
for (int i = 1; i <= n; i++) {
    sum += i;
}
System.out.println(sum); // 15
```

---

## Exercise 3 — Day name with `switch`

Given `int day = 3;` (1 = Monday), print the day name. Use a `switch`.

**Expected output** (for `day = 3`):
```
Wednesday
```

### Solution
```java
int day = 3;
switch (day) {
    case 1: System.out.println("Monday"); break;
    case 2: System.out.println("Tuesday"); break;
    case 3: System.out.println("Wednesday"); break;
    case 4: System.out.println("Thursday"); break;
    case 5: System.out.println("Friday"); break;
    case 6: System.out.println("Saturday"); break;
    case 7: System.out.println("Sunday"); break;
    default: System.out.println("Invalid day");
}
```

---

## Exercise 4 — String basics

Given `String name = "  Java Rocks  ";`, print:
1. Its length **after** trimming spaces.
2. It in uppercase (trimmed).
3. Whether it (trimmed) starts with `"Java"`.

**Expected output:**
```
10
JAVA ROCKS
true
```

### Solution
```java
String name = "  Java Rocks  ";
String t = name.trim();
System.out.println(t.length());          // 10
System.out.println(t.toUpperCase());     // JAVA ROCKS
System.out.println(t.startsWith("Java")); // true
```
*(“Java Rocks” = 4 + 1 space + 5 = 10 characters.)*

---

## Exercise 5 — Predict the output

```java
System.out.println(7 / 2);
System.out.println(7 % 2);
System.out.println(7.0 / 2);
int a = 3;
System.out.println(a++);
System.out.println(a);
```

### Solution
```
3
1
3.5
3
4
```
`7/2` is integer division (3). `7%2` is remainder (1). A `double` operand gives `3.5`.
`a++` prints the old value (3), then `a` becomes 4.

---

## Exercise 6 — Fix the bug

This should print `true` because both texts are equal, but it prints `false`. Fix it.
```java
String s1 = new String("hello");
String s2 = "hello";
System.out.println(s1 == s2);
```

### Solution
`==` compares object references, not text. Use `.equals`:
```java
System.out.println(s1.equals(s2)); // true
```

---

## Exercise 7 — Fix the bug (loop bounds)

This should print numbers 1 to 5 but throws nothing — it prints an extra 0-based mistake.
Actually it skips a number. Fix the loop so it prints 1,2,3,4,5.
```java
for (int i = 0; i < 5; i++) {
    System.out.println(i);
}
```

### Solution
The loop prints 0..4. To print 1..5, start at 1 and use `<= 5`:
```java
for (int i = 1; i <= 5; i++) {
    System.out.println(i);
}
```

---

## Challenge — FizzBuzz

Print numbers 1 to 15. For multiples of 3 print `Fizz`, multiples of 5 print `Buzz`,
multiples of both print `FizzBuzz`, otherwise the number.

**Expected output (first lines):**
```
1
2
Fizz
4
Buzz
```

### Solution
```java
for (int i = 1; i <= 15; i++) {
    if (i % 15 == 0)      System.out.println("FizzBuzz");
    else if (i % 3 == 0)  System.out.println("Fizz");
    else if (i % 5 == 0)  System.out.println("Buzz");
    else                  System.out.println(i);
}
```
Check `% 15` (or `% 3 == 0 && % 5 == 0`) **first**, otherwise 15 would print only `Fizz`.
