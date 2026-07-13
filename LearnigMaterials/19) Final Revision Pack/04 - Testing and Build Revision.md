# Testing and Build Revision

## Key concepts

- Tests prove behavior, not implementation details.
- Workflow tests should include success and failure paths.
- Failed operations should leave state unchanged.
- Maven standardizes compile, test, and package commands.
- CI runs checks automatically outside your local machine.

## Maven reminders

```bash
mvn clean test
mvn clean package
```

With wrapper:

```bash
./mvnw clean test
```

Windows:

```powershell
.\mvnw.cmd clean test
```

## CI checklist

- checkout source;
- set up Java;
- run tests;
- fail when tests fail;
- avoid relying on local-only files.

## Common mistakes

- Committing generated output.
- Using a different Java version locally and in CI.
- Testing only happy paths.
- Sharing dirty test state.

## Quick summary

Build and test discipline makes Java code repeatable and reviewable.
