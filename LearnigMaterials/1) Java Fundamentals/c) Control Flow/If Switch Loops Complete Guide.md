# Java Control Flow: if, switch, loops - Complete Guide

## Overview
Control flow decides **which code runs** and **how many times it runs**. The most important control flow structures are `if`, `switch`, `for`, `while`, and `do-while`.

---

## 1. `if`, `else if`, `else`

Use `if` when the program must make a decision.

```java
int grade = 27;

if (grade >= 18) {
    System.out.println("Passed");
} else {
    System.out.println("Failed");
}
```

Multiple conditions:

```java
int score = 85;

if (score >= 90) {
    System.out.println("Excellent");
} else if (score >= 70) {
    System.out.println("Good");
} else if (score >= 50) {
    System.out.println("Pass");
} else {
    System.out.println("Fail");
}
```

---

## 2. `switch`

Use `switch` when one value is compared to many possible cases.

```java
int day = 2;

switch (day) {
    case 1:
        System.out.println("Monday");
        break;
    case 2:
        System.out.println("Tuesday");
        break;
    default:
        System.out.println("Unknown day");
}
```

### Why `break` matters
Without `break`, Java continues into the next case. This is called **fall-through**.

```java
int x = 1;

switch (x) {
    case 1:
        System.out.println("one");
    case 2:
        System.out.println("two");
}
```

Output:

```text
one
two
```

---

## 3. `for` loop

Use a `for` loop when you know how many times you want to repeat.

```java
for (int i = 0; i < 5; i++) {
    System.out.println(i);
}
```

Output:

```text
0
1
2
3
4
```

Parts:

```java
for (initialization; condition; update) {
    // body
}
```

---

## 4. `while` loop

Use `while` when repetition depends on a condition.

```java
int count = 0;

while (count < 3) {
    System.out.println(count);
    count++;
}
```

---

## 5. `do-while` loop

A `do-while` loop runs at least once.

```java
int x = 10;

do {
    System.out.println(x);
    x++;
} while (x < 5);
```

Output:

```text
10
```

Even though the condition is false, the body runs once first.

---

## 6. `break` and `continue`

### `break`
Stops the loop completely.

```java
for (int i = 0; i < 10; i++) {
    if (i == 5) {
        break;
    }
    System.out.println(i);
}
```

Output:

```text
0
1
2
3
4
```

### `continue`
Skips the current iteration.

```java
for (int i = 0; i < 5; i++) {
    if (i == 2) {
        continue;
    }
    System.out.println(i);
}
```

Output:

```text
0
1
3
4
```

---

## 7. Enhanced for-loop

Used to iterate over arrays or collections.

```java
String[] names = {"Ali", "Sara", "Tom"};

for (String name : names) {
    System.out.println(name);
}
```

It is simpler when you only need the values and not the index.

---

## Common mistakes

### Mistake 1: infinite loop

```java
int i = 0;
while (i < 5) {
    System.out.println(i);
    // forgot i++
}
```

### Mistake 2: off-by-one error

```java
for (int i = 0; i <= array.length; i++) { // wrong
    System.out.println(array[i]);
}
```

Correct:

```java
for (int i = 0; i < array.length; i++) {
    System.out.println(array[i]);
}
```

---

## Mini quiz

### Q1. Which loop runs at least once?
Answer: `do-while`.

### Q2. What does `continue` do?
Answer: skips the current iteration and moves to the next one.

### Q3. Why can `switch` print multiple cases?
Answer: because of fall-through when `break` is missing.
