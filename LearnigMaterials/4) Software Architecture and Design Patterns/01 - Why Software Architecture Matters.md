# Why Software Architecture Matters

## Learning goals

- Understand what software architecture means in Java applications.
- See why larger programs need clear structure.
- Recognize the difference between a small script and a maintainable backend application.

## What software architecture means

Software architecture is the high-level organization of a program. It answers questions such as:

- Which class owns business rules?
- Which class reads input or prints output?
- Which class stores or loads data?
- Which parts are allowed to call each other?

In a small Java exercise, it is normal to write most logic in `main`. As the program grows, that approach becomes hard to test and hard to change. Architecture gives the code stable places for different responsibilities.

## Small script vs layered application

A small script might do everything in one method:

```java
public static void main(String[] args) {
    String name = "Amina";
    double score = 87.5;
    System.out.println(name + " passed: " + (score >= 60.0));
}
```

That is fine for a tiny example. A larger application needs structure:

```text
CLI or controller
    ↓
Service layer
    ↓
Domain model
    ↓
Repository or persistence layer
```

This structure makes each part easier to understand and test.

## Why it matters

Good architecture helps you:

- change input/output without rewriting business rules;
- test business logic without launching the whole program;
- replace in-memory storage with a database later;
- keep classes small and focused;
- avoid accidental changes to internal state.

## Simple example

Instead of letting `Main` calculate everything, `Main` can call a service:

```java
public final class Main {
    public static void main(String[] args) {
        GradeService service = new GradeService();
        service.addScore("S001", 92.0);
        System.out.println(service.averageFor("S001"));
    }
}
```

The service owns the workflow. The CLI only starts the workflow and displays results.

## Common mistakes

- Putting every rule inside `main`.
- Letting UI classes modify domain objects directly.
- Mixing file/database code with business calculations.
- Returning mutable internal lists from public methods.
- Creating too many layers before the program needs them.

## Mini exercise

Take a program where `main` reads input, validates it, updates objects, and prints results. Write down three responsibilities that could move into separate classes.

## Quick summary

Architecture is not about making code complicated. It is about giving each responsibility a clear home so the program stays understandable as it grows.
