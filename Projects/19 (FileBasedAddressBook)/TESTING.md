# Testing File-Based Address Book

## Testing approach

Use a disposable file path. Verify both in-memory contact state and the exact result after saving and loading.

## Normal test cases

| Test | Action | Expected result |
|---|---|---|
| Add contacts | Add unique IDs | Contacts appear in sorted listing |
| Search | Search each supported field | Case-insensitive matches are returned |
| Update | Replace valid details | All fields change together |
| Delete | Remove existing ID | Method returns true and contact disappears |
| Save/load | Round-trip several contacts | Every field is preserved |
| Export/import | Export then import into empty book | Contacts are added in sorted order |

## Edge-case test cases

| Test | Action | Expected result |
|---|---|---|
| Missing file | Load path that does not exist | Empty immutable list |
| Zero-byte file | Load empty file | Empty immutable list |
| Blank lines | Load blank-line-only file | Empty immutable list |
| Empty address book | Save then load | Empty list |
| Shared name | Add different IDs with same name | Both contacts are accepted |
| Delete twice | Delete the same ID twice | True then false |

## Invalid input test cases

| Test | Action | Expected result |
|---|---|---|
| Duplicate ID | Add/import existing ID | IllegalArgumentException |
| Invalid contact | Use blank values, invalid email, tab, or newline | IllegalArgumentException |
| Null input | Use null path, list, contact, or book | IllegalArgumentException |
| Malformed row | Load wrong number of fields | IOException |
| Duplicate file IDs | Load repeated ID | IOException |
| Directory path | Load a directory as contact file | IOException |
| Atomic import | Import containing one conflicting ID | No contacts from that import are added |

## Expected file format

Each non-blank line must contain exactly:

~~~text
contact-id<TAB>name<TAB>phone<TAB>email
~~~

## Expected results

Successful save/load operations must preserve all fields. Missing and empty files must return no contacts, while malformed data must fail without partial import.

## Manual testing checklist

- [ ] Compile and run Main without a file path.
- [ ] Run Main with a disposable file path.
- [ ] Inspect saved UTF-8 tab-separated lines.
- [ ] Test missing, empty, and blank-only files.
- [ ] Test malformed and duplicate-ID files.
- [ ] Verify failed imports preserve existing contacts.
- [ ] Verify returned contact lists cannot be modified.
