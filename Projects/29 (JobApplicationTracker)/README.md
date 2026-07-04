# Job Application Tracker

A standard-Java application for recording job applications, searching and filtering them, reviewing status totals, and preserving records in a CSV file.

## Features

- Add applications with company, role, application date, status, and notes.
- Assign stable positive IDs automatically.
- Update an application's status.
- List all applications in insertion order.
- Search company and role text using case-insensitive partial matching.
- Filter by application status.
- Show total applications and counts for every status.
- Save and load UTF-8 CSV files.
- Handle missing, empty, whitespace-only, and header-only files safely.
- Reject malformed CSV, duplicate IDs, invalid dates/statuses, and empty required fields.

## Repository structure

- `JobApplication` — validated application model and status enum.
- `ApplicationRepository` — file-persistence contract.
- `CsvApplicationRepository` — CSV parser and writer with quoted-field support.
- `TrackerService` — in-memory collection, searches, status updates, summaries, and persistence coordination.
- `Main` — console demonstration with optional CSV save/load.

`TrackerService` owns the active in-memory collection and delegates file operations to `ApplicationRepository`. Loading validates the entire file before replacing the current collection.

## CSV format

The required header is:

```csv
id,company,role,applicationDate,status,notes
```

Dates use `yyyy-MM-dd`. Status values are `APPLIED`, `SCREENING`, `INTERVIEW`, `OFFER`, `REJECTED`, or `WITHDRAWN`. Commas and quotation marks are supported inside quoted fields. Multiline fields are intentionally not supported.

## Example usage

Compile and run the in-memory demonstration:

```text
javac -d out src/jobapplicationtracker/*.java
java -cp out jobapplicationtracker.Main
```

Pass a file path to demonstrate saving and loading:

```text
java -cp out jobapplicationtracker.Main applications.csv
```

Example CSV:

```csv
id,company,role,applicationDate,status,notes
1,Northwind,Java Developer,2026-06-24,INTERVIEW,Technical interview scheduled
2,Contoso,Backend Engineer,2026-06-30,SCREENING,"Recruiter call, Tuesday"
```

## Java concepts practiced

- Classes, interfaces, enums, encapsulation, and service/repository separation
- `List`, `Map`, `Set`, and insertion-ordered collections
- `LocalDate` parsing and validation
- File I/O with `Path`, `Files`, and UTF-8
- Manual CSV parsing and escaping
- Defensive collection views and exception handling

## Possible future improvements

- Editing company, role, date, and notes
- Date-range searches and response-rate statistics
- Follow-up reminders
- Interactive console menus
- Atomic file replacement and backup copies
- Automated unit tests
