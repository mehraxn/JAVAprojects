# Git Basics Recap

## Learning goals

- Review the daily Git commands used in Java work.
- Understand commits, branches, merges, and `.gitignore`.
- Avoid committing generated build output.

## Core commands

```bash
git status
git add README.md src/main/java
git commit -m "Add order validation"
git log --oneline
```

`git status` should become a habit. It shows what changed before you commit.

## Branches and merges

```bash
git switch -c feature/order-report
git switch main
git merge feature/order-report
```

A branch lets you work on one change without mixing it with unrelated work.

## `.gitignore`

Java repositories usually ignore generated folders:

```gitignore
target/
out/
*.class
.idea/
.vscode/
```

Do commit source files, tests, documentation, and build configuration.

## Common mistakes

- Committing `target/` or `.class` files.
- Mixing many unrelated changes in one commit.
- Forgetting to check `git status`.
- Resolving merge conflicts without reading both sides carefully.

## Mini exercise

Create a small Java folder, initialize Git, add a `.gitignore`, make two commits, and inspect the history with `git log --oneline`.

## Quick summary

Git records meaningful source changes. Build outputs should be reproducible, not committed.
