# Java Variables, Types, Operators, and Strings - Complete Guide

## Overview
A variable is a named place where your program stores data. Java is a **strongly typed** language, meaning every variable has a type.

---

## Table of Contents
1. Variables
2. Primitive types
3. Reference types
4. Type conversion and casting
5. Operators
6. String basics
7. `String` comparison
8. Common mistakes
9. Mini quiz

---

## 1. Variables

Syntax:

```java
type variableName = value;
```

Example:

```java
int age = 22;
double price = 19.99;
boolean active = true;
String name = "Sara";
```

You can change a variable unless it is declared `final`:

```java
int x = 10;
x = 20;

final int MAX_USERS = 100;
// MAX_USERS = 200; // compile error
```

---

## 2. Primitive types

Primitive types store simple values.

| Type | Example | Meaning |
|---|---|---|
| `byte` | `byte b = 10;` | small integer |
| `short` | `short s = 100;` | integer |
| `int` | `int x = 5;` | most common integer |
| `long` | `long big = 100000L;` | large integer |
| `float` | `float f = 2.5f;` | decimal |
| `double` | `double d = 2.5;` | most common decimal |
| `char` | `char c = 'A';` | one character |
| `boolean` | `boolean ok = true;` | true/false |

---

## 3. Reference types

Reference types refer to objects.

Examples:

```java
String text = "hello";
int[] numbers = {1, 2, 3};
Student student = new Student();
```

Important difference:

```java
int a = 10;
int b = a;
b = 20;
System.out.println(a); // 10
```

For objects, the variable stores a reference:

```java
int[] arr1 = {1, 2, 3};
int[] arr2 = arr1;
arr2[0] = 99;
System.out.println(arr1[0]); // 99
```

Both `arr1` and `arr2` refer to the same array object.

---

## 4. Type conversion and casting

### Widening conversion
Safe automatic conversion:

```java
int x = 10;
double d = x; // int to double is safe
```

### Narrowing conversion
Needs explicit cast:

```java
double d = 10.8;
int x = (int) d;
System.out.println(x); // 10
```

The decimal part is removed.

---

## 5. Operators

### Arithmetic operators

```java
int a = 10;
int b = 3;

System.out.println(a + b); // 13
System.out.println(a - b); // 7
System.out.println(a * b); // 30
System.out.println(a / b); // 3
System.out.println(a % b); // 1
```

Important: integer division removes the decimal part.

```java
System.out.println(10 / 4);   // 2
System.out.println(10 / 4.0); // 2.5
```

### Comparison operators

```java
x == y
x != y
x > y
x >= y
x < y
x <= y
```

### Logical operators

```java
&&  // and
||  // or
!   // not
```

Example:

```java
int age = 20;
boolean hasTicket = true;

if (age >= 18 && hasTicket) {
    System.out.println("Can enter");
}
```

---

## 6. String basics

`String` is used for text:

```java
String firstName = "Ali";
String lastName = "Reza";
String fullName = firstName + " " + lastName;
System.out.println(fullName);
```

Useful methods:

```java
String text = "Java Programming";

System.out.println(text.length());       // 16
System.out.println(text.toUpperCase());  // JAVA PROGRAMMING
System.out.println(text.toLowerCase());  // java programming
System.out.println(text.contains("Pro")); // true
System.out.println(text.substring(0, 4)); // Java
```

---

## 7. `String` comparison

Do not compare strings with `==` for content.

Wrong:

```java
String a = new String("hello");
String b = new String("hello");
System.out.println(a == b); // false
```

Correct:

```java
System.out.println(a.equals(b)); // true
```

Use:

```java
a.equals(b)
```

for exact content comparison.

Use:

```java
a.equalsIgnoreCase(b)
```

for case-insensitive comparison.

---

## 8. Common mistakes

### Mistake 1: integer division

```java
double result = 5 / 2;
System.out.println(result); // 2.0, not 2.5
```

Correct:

```java
double result = 5 / 2.0;
```

### Mistake 2: using `==` for strings

Use `.equals()`.

### Mistake 3: confusing `=` and `==`

```java
x = 5;  // assignment
x == 5; // comparison
```

---

## 9. Mini quiz

### Q1. What is the output?

```java
System.out.println(7 / 2);
```

Answer: `3`.

### Q2. How do you compare two strings by content?
Answer: use `.equals()`.

### Q3. What does `final` mean for a variable?
Answer: it cannot be reassigned after initialization.
