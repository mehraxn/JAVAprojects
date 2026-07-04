# Event Registration System

An in-memory Java application for events, participants, capacity, and searching.

## Implemented features

- Add events with unique IDs, dates, categories, and positive capacities.
- Register participants and generate event-specific registration IDs.
- Prevent duplicate participant registration for the same event.
- Reject registrations when an event reaches capacity.
- Cancel registrations and restore available capacity.
- Show participants and registrations for each event.
- Search events by partial name, exact date, or partial category.
- Return events ordered by date and then name.

Full events reject additional registrations; this version does not create a waitlist.

## Structure

- `Attendee` stores participant identity and basic email validation.
- `Registration` records an attendee and registration time.
- `Event` owns capacity and participant registrations.
- `EventRegistrationSystem` manages events and search operations.
- `Main` demonstrates registration, searching, and cancellation.

Source files are under `src/eventregistrationsystem` and use only standard Java.

## Run

```powershell
javac -d out src\eventregistrationsystem\*.java
java -cp out eventregistrationsystem.Main
```

See `TESTING.md` for manual test cases.
