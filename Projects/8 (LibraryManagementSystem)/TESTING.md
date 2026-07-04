# Testing Library Management System

## Testing approach

Run Main for a basic demonstration, then call Library methods directly for validation and state-consistency tests.

## Normal test cases

| Test | Action | Expected result |
|---|---|---|
| Add records | Add two books and two members | All records can be retrieved |
| Borrow book | Borrow an available ISBN | Book becomes unavailable and member stores ISBN |
| Return book | Return through the correct member | Book becomes available and ISBN is removed |
| Search title | Search using partial title text | Matching books are returned |
| Search author | Search using partial author text | Matching books are returned |
| Available list | Borrow one of two books | Only the other book is listed |

## Edge-case test cases

| Test | Action | Expected result |
|---|---|---|
| Blank search | Search with an empty string | All books are returned in title order |
| Shared title | Add different ISBNs with the same title | Both books are accepted |
| Failed borrow | Try an unavailable book | Book and member state remain unchanged |
| Failed return | Return through the wrong member | Both records remain unchanged |
| Member with no books | Read borrowed ISBN set | Empty unmodifiable set |

## Invalid input test cases

| Test | Action | Expected result |
|---|---|---|
| Duplicate ISBN | Add the same ISBN twice | IllegalArgumentException |
| Duplicate member | Add the same member ID twice | IllegalArgumentException |
| Unknown record | Borrow using unknown ISBN or member ID | IllegalArgumentException |
| Double borrow | Borrow an unavailable book | IllegalStateException |
| Wrong return | Return through another member | IllegalStateException |
| Blank/null data | Use blank fields, null search, or null objects | IllegalArgumentException |

## Manual testing checklist

- [ ] Compile and run Main.
- [ ] Verify Book and Member update together.
- [ ] Verify failed operations preserve both objects.
- [ ] Test title and author searches with different letter case.
- [ ] Test duplicate identifiers.
- [ ] Test returning an already available book.
- [ ] Verify returned lists and sets cannot be modified.
