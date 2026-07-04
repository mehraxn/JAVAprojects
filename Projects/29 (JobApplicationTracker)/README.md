# Job Application Tracker

## Status

Application model, JDBC boundary, repository, services, and safe Main skeleton created.

## Planned features

- Add, update, search, and delete applications.
- Track application stages.
- Store applied and reminder dates.
- Find reminders that are due.
- Filter by stage and search text.
- Persist records through standard JDBC code.

## Current classes

- JobApplication: application model and stage enum.
- DatabaseConnection: JDBC configuration boundary.
- JdbcApplicationRepository: planned CRUD and reminder queries.
- TrackerService: application operations and stage rules.
- ReminderService: due-reminder queries.
- Main: runner that does not connect automatically.

## Runtime requirement

Database execution requires an existing JDBC driver, URL, and schema. No driver is included or installed.

## Source layout

Source files are under src/jobapplicationtracker.
