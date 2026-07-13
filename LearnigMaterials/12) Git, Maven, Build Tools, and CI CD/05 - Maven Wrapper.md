# Maven Wrapper

## Learning goals

- Understand why Maven Wrapper exists.
- Learn `mvnw` vs installed Maven.
- Know the Windows and Linux/macOS commands.

## What is Maven Wrapper?

Maven Wrapper lets a repository run a specific Maven version without requiring every developer to install Maven globally.

Typical wrapper files:

```text
mvnw
mvnw.cmd
.mvn/wrapper/
```

## Commands

Linux/macOS:

```bash
./mvnw clean test
```

Windows PowerShell:

```powershell
.\mvnw.cmd clean test
```

Installed Maven:

```bash
mvn clean test
```

## Why use it?

- Consistent Maven version.
- Easier setup for new developers.
- Easier CI configuration.
- Less dependence on local machine setup.

## Common mistakes

- Forgetting to commit wrapper files.
- Running `mvnw` without execute permission on Unix-like systems.
- Mixing wrapper and installed Maven when troubleshooting without noticing.

## Mini exercise

Generate a Maven Wrapper in a small Maven application and run tests with the wrapper command.

## Quick summary

Maven Wrapper makes the build easier to reproduce across machines and CI.
