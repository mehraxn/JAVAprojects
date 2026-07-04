# File-Based Address Book

An in-memory Java address book with optional UTF-8 file persistence.

## Implemented features

- Add contacts with unique IDs.
- Search case-insensitively across ID, name, phone, and email.
- Update contact details after validating all new values.
- Delete contacts and list contacts sorted by name and ID.
- Save and load contacts with standard `java.nio.file` APIs.
- Return an empty list when the file is missing or contains no contact rows.
- Reject malformed rows and duplicate IDs in stored data.
- Import without partially modifying the address book when IDs conflict.

## File format

Each UTF-8 line contains four tab-separated fields:

```text
contact-id<TAB>name<TAB>phone<TAB>email
```

Contact fields therefore reject tabs and line breaks.

## Structure

- `Contact` owns validated contact details and search matching.
- `AddressBook` manages contacts and sorted searches.
- `FileStore` handles loading, saving, importing, and exporting.
- `Main` demonstrates contact operations and optionally file persistence.

Source files are under `src/filebasedaddressbook` and use only standard Java.

## Run

```powershell
javac -d out src\filebasedaddressbook\*.java
java -cp out filebasedaddressbook.Main
java -cp out filebasedaddressbook.Main contacts.tsv
```

The first command performs no file writes. Pass a path to demonstrate persistence.

See `TESTING.md` for manual test cases.
