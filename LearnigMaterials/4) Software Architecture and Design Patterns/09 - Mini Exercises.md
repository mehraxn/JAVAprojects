# Mini Exercises

## 1. Refactor a Main-heavy program

Start with a program where `main` creates products, validates prices, calculates totals, and prints a receipt.

Refactor it into:

- `Product`
- `Order`
- `OrderService`
- `OrderSnapshot`
- `Main`

## 2. Add a facade

Create a `LibraryFacade` that coordinates:

- `MemberService`
- `BookService`
- `LoanService`

Add methods:

- `registerMember`
- `borrowBook`
- `returnBook`

## 3. Create a factory

Create an `AccountFactory` with methods:

- `standardAccount`
- `premiumAccount`
- `studentAccount`

Each method should create an account with clear default rules.

## 4. Build a repository interface

Create `CourseRepository` with:

- `save`
- `findById`
- `findAll`
- `deleteById`

Write an in-memory implementation.

## 5. Create snapshots

Create a mutable `Order` class with a list of item names. Then create an immutable `OrderSnapshot` that cannot expose the mutable list.

## 6. Responsibility review

For each responsibility, choose the best layer:

- parsing command-line arguments;
- checking whether a price is positive;
- transferring money between two accounts;
- saving an object;
- formatting a report line.

## Quick summary

These exercises practice the main architecture skill: putting each responsibility in the simplest correct place.
