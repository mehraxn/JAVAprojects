# Library Management System

An educational, dependency-free Java project that models a lending library. It
focuses on clean object-oriented design, a service layer with real business
rules, borrowing/return workflows, borrowing limits, due-date tracking and
overdue detection, loan history, and defensive data exposure — not on a database,
web API, or barcode/real-library integration.

## What it demonstrates

- **Book** domain model (ISBN, title, author, category, publication year, availability)
- **Member** domain model (ID, name, optional email, borrowed-ISBN set)
- **LoanRecord** domain model (loan ID, ISBN, member, borrow/due/return dates, status)
- **LoanStatus** enum (`ACTIVE`, `RETURNED`)
- **Library** service layer that owns all state changes
- book and member registration with duplicate rejection
- borrowing and return workflows that keep book, member, and loan in sync
- borrowing limits (default 3 active loans per member)
- due-date tracking (borrow date + loan period) and overdue detection
- active and completed loan history, plus per-member/per-book history
- case-insensitive catalogue search and available-book reporting
- defensive snapshots so internal state cannot be mutated from outside
- deterministic loan IDs (`L0001`, `L0002`, …) and dates via an injectable `Clock`
- command-based CLI demos
- dependency-free automated tests
- strict compilation (`-Xlint:all -Werror`)

## Features

- Register books and members; reject duplicate ISBNs / member IDs.
- Borrow a book: mark it unavailable, add it to the member, create an active loan.
- Return a book: mark it available, remove it from the member, complete the loan.
- Enforce a per-member borrowing limit.
- Track due dates and detect overdue active loans.
- Search the catalogue by title or author (case-insensitive) and list available books.
- Report active/completed loans, overdue loans, and per-member/per-book history.
- CLI demos for every feature area.

## Main classes

| Class | Responsibility |
|---|---|
| `Book` | Catalogue entry + availability; only `Library` borrows/returns it. |
| `Member` | Member + borrowed-ISBN set; only `Library` mutates the set. |
| `LoanRecord` | One borrowing event; status/returnDate transition once, on return. |
| `LoanStatus` | Enum: `ACTIVE`, `RETURNED`. |
| `Library` | Service layer: catalogue, members, borrow/return, search, reports. |
| `BookSnapshot` / `MemberSnapshot` / `LoanRecordSnapshot` | Immutable read-only views. |
| `Main` | Command-based CLI / demo driver. |

## Behavior notes

- **Borrowing and returning are state-consistent.** A borrow updates book
  availability, the member's borrowed set, and the loan history together; a return
  reverses all three together. Every check runs before any state change, so a
  **failed borrow or return leaves all state unchanged**.
- **Borrowed books are unavailable**; returned books become available again.
- **Due dates** are the borrow date plus the loan period (default 14 days).
- **Active loans can be overdue** (today after the due date); **returned loans are
  never overdue**.
- **A member may hold at most the borrow limit** (default 3) active loans;
  returned loans do not count.
- **Dates come from an injectable `Clock`**, and loan IDs are deterministic
  (`L0001`, `L0002`, …), so tests and demos are reproducible.
- **Email is optional** and checked with a deliberately simple, educational rule
  (one `@`, non-blank local/domain, a dot in the domain) — not production-grade.
- **Public methods return immutable snapshots in unmodifiable lists.** Live
  `Book`/`Member`/`LoanRecord` objects are never leaked, so external code cannot
  borrow or return by bypassing the library.

## Quick start

Compile:

~~~
javac -Xlint:all -Werror -d out src/librarymanagementsystem/*.java
~~~

Run the CLI commands:

~~~
java -cp out librarymanagementsystem.Main help
java -cp out librarymanagementsystem.Main demo
java -cp out librarymanagementsystem.Main borrow-demo
java -cp out librarymanagementsystem.Main return-demo
java -cp out librarymanagementsystem.Main search-demo
java -cp out librarymanagementsystem.Main overdue-demo
java -cp out librarymanagementsystem.Main history-demo
java -cp out librarymanagementsystem.Main validation-demo
~~~

## Testing

The project ships with a dependency-free test suite (custom assertion helper and
runner — no JUnit, Maven, or Gradle). Run everything with:

~~~
bash scripts/test.sh
~~~

Windows PowerShell:

~~~
.\scripts\test.ps1
~~~

See [TESTING.md](TESTING.md) for exact commands and [TEST_RESULTS.md](TEST_RESULTS.md)
for the latest recorded run.

## Limitations

This is a learning project. It intentionally has:

- no database (in-memory only)
- no HTTP API
- no login/authentication
- no barcode scanner
- no fine/payment system
- no real library integration
- no production library guarantees
- no production deployment
