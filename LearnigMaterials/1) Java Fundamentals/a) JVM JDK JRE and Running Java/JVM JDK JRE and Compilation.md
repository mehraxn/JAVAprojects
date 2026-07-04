# JVM, JRE, JDK, and Running Java Programs - Complete Guide

## Overview
Before studying Java syntax, you should understand what happens when a Java program runs. Java is different from languages that compile directly to machine code. Java source code is compiled into **bytecode**, and bytecode is executed by the **JVM**.

---

## Table of Contents
1. What is the JDK?
2. What is the JRE?
3. What is the JVM?
4. Java source code vs bytecode
5. How to compile and run a Java program
6. The `main` method
7. Common mistakes
8. Mini quiz

---

## 1. What is the JDK?

**JDK** means **Java Development Kit**.

It is used by developers to create Java programs. It contains:

- the compiler `javac`
- the Java runtime
- standard Java libraries
- tools for debugging, packaging, documentation, and more

If you want to write Java code, you need a JDK.

---

## 2. What is the JRE?

**JRE** means **Java Runtime Environment**.

It contains what is needed to **run** Java programs:

- JVM
- standard libraries

Older Java installations often separated JDK and JRE clearly. In modern Java distributions, you usually install the JDK and get everything needed.

---

## 3. What is the JVM?

**JVM** means **Java Virtual Machine**.

It is the program that executes Java bytecode.

Important idea:

```text
Java code does not run directly on your CPU.
Java code is compiled into bytecode.
The JVM runs the bytecode.
```

This is why Java is often described as:

```text
Write once, run anywhere
```

The same `.class` bytecode can run on Windows, macOS, or Linux if the correct JVM exists.

---

## 4. Java source code vs bytecode

You write:

```text
Main.java
```

The compiler creates:

```text
Main.class
```

The `.class` file contains bytecode.

Flow:

```text
Main.java --javac--> Main.class --JVM/java--> program output
```

---

## 5. How to compile and run a Java program

Create a file called `Main.java`:

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello Java");
    }
}
```

Compile:

```bash
javac Main.java
```

Run:

```bash
java Main
```

Output:

```text
Hello Java
```

Important: when running, write `java Main`, not `java Main.class`.

---

## 6. The `main` method

The entry point of a normal Java program is:

```java
public static void main(String[] args)
```

Meaning:

- `public`: JVM can access it
- `static`: JVM can call it without creating an object
- `void`: it returns nothing
- `main`: special method name recognized as entry point
- `String[] args`: command-line arguments

Example with arguments:

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Number of args: " + args.length);
    }
}
```

Run:

```bash
java Main hello world
```

Output:

```text
Number of args: 2
```

---

## 7. Common mistakes

### Mistake 1: File name and public class name do not match

```java
public class StudentApp { }
```

This must be saved as:

```text
StudentApp.java
```

### Mistake 2: Running with `.class`

Wrong:

```bash
java Main.class
```

Correct:

```bash
java Main
```

### Mistake 3: Forgetting semicolon

```java
System.out.println("Hello") // wrong
System.out.println("Hello"); // correct
```

---

## 8. Mini quiz

### Q1. Which tool compiles Java source code?
Answer: `javac`.

### Q2. What does the JVM execute?
Answer: bytecode stored in `.class` files.

### Q3. Do you need the JDK to develop Java programs?
Answer: Yes.

---

## Key takeaway
The JDK is for development, the JVM runs bytecode, and Java source code is compiled before it runs.
