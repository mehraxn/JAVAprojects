# Library Management System

An in-memory Java application for tracking books, members, borrowing, and returns.

## Implemented features

- Add books with unique ISBNs.
- Register members with unique IDs.
- Borrow and return books while keeping book and member state consistent.
- Prevent borrowing an unavailable book.
- Prevent returns by the wrong member.
- Search case-insensitively by title or author.
- List currently available books.
- Validate blank, duplicate, and unknown identifiers.

## Structure

- `Book` owns bibliographic and availability state.
- `Member` tracks ISBNs currently borrowed by that member.
- `Library` coordinates books and members.
- `Main` demonstrates adding, borrowing, searching, and returning.

Source files are under `src/librarymanagementsystem` and use only standard Java.

## Run

```powershell
javac -d out src\librarymanagementsystem\*.java
java -cp out librarymanagementsystem.Main
```

See `TESTING.md` for manual test cases.
