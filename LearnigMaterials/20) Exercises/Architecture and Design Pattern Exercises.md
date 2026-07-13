# Architecture and Design Pattern Exercises

## 1. Refactor a Main-heavy program

Start with a program where `main` creates products, validates prices, calculates totals, and prints output.

Refactor into:

- `Product`
- `Order`
- `OrderService`
- `OrderSnapshot`
- `Main`

## 2. Add a facade

Create a facade over three services:

- `MemberService`
- `BookService`
- `LoanService`

The facade should expose:

- `registerMember`
- `borrowBook`
- `returnBook`

## 3. Add a factory

Create `AccountFactory` methods:

- `standardAccount`
- `studentAccount`
- `premiumAccount`

Each method should make object creation clearer than raw constructor calls.

## 4. Add DTO snapshots

Create a mutable `Course` with enrolled student IDs. Return an immutable `CourseSnapshot`.

## 5. Review responsibilities

For each class in a small application, label it as:

- CLI/controller
- service
- domain model
- repository
- report object

If a class has too many roles, suggest a split.
