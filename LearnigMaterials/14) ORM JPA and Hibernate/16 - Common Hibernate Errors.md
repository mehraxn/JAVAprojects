# Common Hibernate Errors

## Learning goals

- Recognize common Hibernate and JPA errors.
- Understand likely causes.
- Know first troubleshooting steps.

## No persistence provider

Meaning: JPA cannot find an implementation.

Check dependencies and `persistence.xml`.

## Unknown entity

Meaning: a class is not recognized as an entity.

Check `@Entity`, package scanning, and configuration.

## LazyInitializationException

Meaning: lazy data was accessed after the persistence context closed.

Fetch required data earlier or return DTOs.

## Detached entity passed to persist

Meaning: code called `persist` on an entity that already has database identity.

Use `merge` when appropriate.

## Unsaved transient instance

Meaning: an entity references another unsaved entity without cascade or explicit save.

Persist the referenced entity first or configure cascade correctly.

## Constraint violation

Meaning: database rejected data.

Check not-null, unique, foreign key, and length constraints.

## Wrong dialect or schema generation issue

Meaning: Hibernate SQL does not match the database or schema configuration.

Check dialect, URL, and schema generation settings.

## Common mistakes

- Reading only the last line of a long stack trace.
- Setting everything eager to hide lazy loading errors.
- Using `update` schema generation as a substitute for migrations.
- Ignoring the first real cause.

## Mini exercises

1. Diagnose an unknown entity error.
2. Explain how to fix detached entity passed to persist.
3. Explain why a unique constraint violation is useful.

## Quick summary

Hibernate errors are easier to solve when you identify whether the issue is configuration, mapping, lifecycle, query, or constraint related.
