# Event Registration System

## Description

Event Registration System is an in-memory Java project for dated events, participant registration, capacity enforcement, cancellation, and searching.

## Features

- Add events with IDs, names, dates, categories, and capacities.
- Reject duplicate IDs and duplicate event definitions.
- Register participants with generated registration IDs.
- Prevent duplicate registration within one event.
- Reject registrations when capacity is full.
- Cancel registrations and restore capacity.
- List event participants.
- Search by partial name, exact date, or partial category.

## Java concepts practiced

- LocalDate and LocalDateTime
- Map and List collections
- Capacity and duplicate validation
- Searching, sorting, and generated IDs
- Object composition and unmodifiable query results

## Main classes

- Attendee: stores participant identity and email.
- Registration: stores attendee and registration time.
- Event: owns capacity and registrations.
- EventRegistrationSystem: manages events and searches.
- Main: demonstrates registration, search, and cancellation.

## How the program works

Events are stored by ID. Event owns its participant registrations and checks duplicates and capacity before adding one. Full events reject additional participants; this version intentionally has no waitlist.

## Example usage

~~~powershell
javac -d out src\eventregistrationsystem\*.java
java -cp out eventregistrationsystem.Main
~~~

The demo fills a two-person workshop, searches by category, then cancels one registration.

## Possible future improvements

- Add a waitlist.
- Add event times and locations.
- Add participant check-in.
- Add cancellation deadlines.
- Save events and registrations to files.
