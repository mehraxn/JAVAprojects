# Java Methods, Scope, and Pass-by-Value - Complete Guide

## Overview
A method is a reusable block of code that performs a task. Methods make code organized, readable, and reusable.

---

## 1. Basic method syntax

```java
returnType methodName(parameterList) {
    // method body
}
```

Example:

```java
public static int add(int a, int b) {
    return a + b;
}
```

Calling the method:

```java
int result = add(3, 4);
System.out.println(result); // 7
```

---

## 2. `void` methods

A `void` method does not return a value.

```java
public static void printGreeting(String name) {
    System.out.println("Hello " + name);
}
```

Call:

```java
printGreeting("Sara");
```

---

## 3. Return values

A non-void method must return a value of the declared return type.

```java
public static double average(int a, int b) {
    return (a + b) / 2.0;
}
```

Wrong:

```java
public static int getNumber() {
    // compile error: missing return statement
}
```

---

## 4. Parameters and arguments

In this method:

```java
public static int multiply(int x, int y) {
    return x * y;
}
```

`x` and `y` are **parameters**.

In this call:

```java
multiply(4, 5);
```

`4` and `5` are **arguments**.

---

## 5. Method overloading

Overloading means same method name, different parameter list.

```java
public static int sum(int a, int b) {
    return a + b;
}

public static double sum(double a, double b) {
    return a + b;
}

public static int sum(int a, int b, int c) {
    return a + b + c;
}
```

Important: return type alone is not enough for overloading.

Wrong:

```java
int getValue() { return 1; }
double getValue() { return 1.0; } // compile error
```

---

## 6. Scope

Scope means where a variable can be used.

```java
public static void main(String[] args) {
    int x = 10;

    if (x > 5) {
        int y = 20;
        System.out.println(y); // ok
    }

    // System.out.println(y); // compile error
}
```

`y` exists only inside the `if` block.

---

## 7. Java is pass-by-value

Java passes arguments by value. This means the method receives a copy of the variable value.

### Primitive example

```java
public static void change(int x) {
    x = 99;
}

public static void main(String[] args) {
    int a = 10;
    change(a);
    System.out.println(a); // 10
}
```

The method changed only its local copy.

### Object reference example

```java
public static void changeFirst(int[] arr) {
    arr[0] = 99;
}

public static void main(String[] args) {
    int[] numbers = {1, 2, 3};
    changeFirst(numbers);
    System.out.println(numbers[0]); // 99
}
```

Important explanation:

- Java passes a copy of the reference.
- The copied reference still points to the same object.
- Therefore the method can mutate the object.

But if the method reassigns the reference, the caller is not changed:

```java
public static void replace(int[] arr) {
    arr = new int[] {9, 9, 9};
}

public static void main(String[] args) {
    int[] numbers = {1, 2, 3};
    replace(numbers);
    System.out.println(numbers[0]); // 1
}
```

---

## Common mistakes

### Mistake 1: thinking Java is pass-by-reference
Java is always pass-by-value.

### Mistake 2: forgetting return
A method with return type must return something.

### Mistake 3: overloading only by return type
Not allowed.

---

## Mini quiz

### Q1. Can two methods have the same name?
Answer: Yes, if they have different parameter lists.

### Q2. Is Java pass-by-value or pass-by-reference?
Answer: pass-by-value.

### Q3. What is scope?
Answer: the part of the code where a variable is visible and usable.
