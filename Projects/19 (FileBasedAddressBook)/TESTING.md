# Testing File-Based Address Book

The project has no external test dependencies. Use a disposable path when manually testing persistence.

## Manual test cases

1. Add several uniquely identified contacts and verify sorted listing.
2. Add a duplicate contact ID; expect `IllegalArgumentException`.
3. Search by partial ID, name, phone, and email with different letter case.
4. Use a blank search and verify all contacts are returned in sorted order.
5. Update a contact and verify all changed details.
6. Submit one invalid update field and verify no contact fields change.
7. Delete an existing contact, then delete it again; expect `true` followed by `false`.
8. Save contacts and load them into a list; verify every field survives the round trip.
9. Save an empty list and load it; expect an empty list.
10. Load a missing file; expect an empty list without an exception.
11. Load a file containing only blank lines; expect an empty list.
12. Load a malformed row with the wrong field count; expect `IOException`.
13. Load duplicate IDs or invalid contact data; expect `IOException`.
14. Import contacts with no conflicts and verify they are added.
15. Import when one ID already exists; expect `IllegalArgumentException` and no imported contacts.
16. Export an address book and verify records are ordered by name and ID.
17. Use null paths, lists, contacts, or address books; expect `IllegalArgumentException`.
18. Use blank fields, invalid email, tabs, or line breaks; expect `IllegalArgumentException`.
19. Try modifying returned contact or loaded-contact lists; expect `UnsupportedOperationException`.

## Validation review additions

- Confirm missing files, zero-byte files, and blank-line-only files all load as empty immutable lists.
- Verify a malformed or duplicate-ID import changes none of the existing address book.
- Save an empty address book and confirm a subsequent load safely returns no contacts.
- Verify duplicate contact names are allowed when IDs differ, while duplicate IDs remain rejected.
