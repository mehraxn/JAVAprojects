# File-Based Address Book

## Description

File-Based Address Book is a Java project for contact management with optional UTF-8 text-file persistence. It is the only Phase 1 project that writes application data to files.

## Features

- Add contacts with unique IDs.
- Search ID, name, phone, or email case-insensitively.
- Update validated contact details.
- Delete and sort contacts.
- Save contacts to a UTF-8 file.
- Load contacts from a file.
- Treat missing, empty, and blank-line-only files as empty.
- Reject malformed rows and duplicate stored IDs.
- Import without partially modifying data when IDs conflict.

## Java concepts practiced

- Map and List collections
- Encapsulation and validation
- Sorting and defensive collections
- Path, Files, UTF-8, and IOException
- Parsing a simple tab-separated format
- Pre-validation before state changes

## Main classes

- Contact: owns validated contact details and search matching.
- AddressBook: manages contact CRUD, searching, and sorting.
- FileStore: loads, saves, imports, and exports contacts.
- Main: demonstrates contact operations and optional persistence.

## How the program works

AddressBook stores Contact objects by ID. FileStore writes four tab-separated fields per UTF-8 line: ID, name, phone, and email. Fields reject tabs and line breaks. Loading a missing or empty file returns an empty immutable list; malformed data produces IOException.

## Example usage

Run without file output:

~~~powershell
javac -d out src\filebasedaddressbook\*.java
java -cp out filebasedaddressbook.Main
~~~

Run with a persistence file:

~~~powershell
java -cp out filebasedaddressbook.Main contacts.tsv
~~~

## Possible future improvements

- Add CSV quoting support.
- Add multiple phone numbers and addresses.
- Add contact groups.
- Add backup-file creation.
- Add duplicate detection by email or phone.
