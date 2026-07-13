# CI CD Basics

## Learning goals

- Understand continuous integration.
- Understand the idea of continuous delivery/deployment.
- Know why automated builds matter.

## What is CI?

Continuous integration means running checks automatically when code changes. Common checks include:

- compile the code;
- run tests;
- inspect formatting or style;
- package the application.

## What is CD?

Continuous delivery means the application can be prepared for release automatically.

Continuous deployment means releases can be deployed automatically after checks pass.

For learning projects, CI is usually the most important first step.

## Typical CI flow

```text
push code
   ↓
checkout repository
   ↓
set up Java
   ↓
run Maven tests
   ↓
show pass/fail result
```

## Why CI matters

- Catches mistakes before merging.
- Proves the build works outside your machine.
- Makes testing visible.
- Encourages small, clean commits.

## Common mistakes

- CI uses a different Java version than local development.
- Generated files are required but not committed.
- Tests depend on local machine paths.
- Build output is committed instead of generated.

## Mini exercise

Write a CI checklist for a Java application: Java version, build command, test command, and ignored output folders.

## Quick summary

CI is an automated safety check for every important code change.
