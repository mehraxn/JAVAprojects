# Build Checklist for Java Projects

## Learning goals

- Use a practical pre-commit or pre-release checklist.
- Catch common Java build problems.
- Keep repositories clean.

## Checklist

Before sharing a Java codebase:

- [ ] Run `mvn clean test` or the documented build command.
- [ ] Confirm the expected Java version.
- [ ] Confirm tests pass.
- [ ] Check `git status`.
- [ ] Check `.gitignore`.
- [ ] Ensure generated folders such as `target`, `out`, and `.class` files are not committed.
- [ ] Review README run instructions.
- [ ] Review test instructions.
- [ ] Confirm CI uses the same Java version.
- [ ] Confirm CI uploads useful test reports if needed.

## Optional checks

- [ ] Run coverage report.
- [ ] Run integration tests.
- [ ] Check dependency updates.
- [ ] Review logs for warnings.

## Common mistakes

- Running tests locally but not in CI.
- Forgetting generated files.
- Updating code but not documentation.
- Ignoring failing tests because the app seems to run manually.

## Mini exercises

1. Create a checklist for a small Maven app.
2. Add generated folders to `.gitignore`.
3. Compare local Java version with CI Java version.

## Quick summary

A build checklist prevents avoidable mistakes before code is shared.
