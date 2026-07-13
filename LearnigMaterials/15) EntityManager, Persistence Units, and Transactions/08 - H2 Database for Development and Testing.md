# H2 Database for Development and Testing

## Learning goals

- Understand why H2 is useful for Java learning.
- Learn in-memory and file database URLs.
- Know its limits.

## What is H2?

H2 is a lightweight relational database often used for development and tests.

## In-memory database

```xml
<property name="jakarta.persistence.jdbc.url" value="jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"/>
```

The data exists in memory while the JVM is running.

## File database

```xml
<property name="jakarta.persistence.jdbc.url" value="jdbc:h2:file:./data/devdb"/>
```

The data is stored in local files.

## Why it is useful

- Fast startup.
- Good for repository tests.
- No separate database installation for simple exercises.
- Easy to reset between tests.

## Limits

H2 is not the same as every production database. SQL dialects and behavior can differ, so important applications should also test with the target database.

## Common mistakes

- Assuming H2 behavior exactly matches another database.
- Sharing the same test database across tests without cleanup.
- Accidentally using a persistent file database for tests.

## Mini exercise

Create a test persistence unit using H2 in-memory mode and explain how you would clean data between tests.

## Quick summary

H2 is excellent for learning and tests, but it does not replace understanding the real database used by an application.
