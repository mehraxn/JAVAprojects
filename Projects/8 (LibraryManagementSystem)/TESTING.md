# Testing Library Management System

The project has no external test dependencies. Compile and run `Main`, or use a small Java driver for the cases below.

## Manual test cases

1. Add two books and two members; verify each can be retrieved by ID.
2. Borrow an available book and verify `isAvailable()` becomes `false`.
3. Verify the borrower's ISBN set contains the borrowed book.
4. Try borrowing the same book with another member; expect `IllegalStateException`.
5. Return the book with the correct member and verify it becomes available.
6. Try returning an available book; expect `IllegalStateException`.
7. Try returning a borrowed book with the wrong member; expect `IllegalStateException`.
8. Search with partial title text and different letter case; verify the expected book is returned.
9. Search by partial author name; verify the expected book is returned.
10. Add duplicate ISBN or member ID values; expect `IllegalArgumentException`.
11. Borrow using an unknown ISBN or member ID; expect `IllegalArgumentException`.
12. Verify `listAvailableBooks()` excludes borrowed books.
