# CI Test Reports and Artifacts

## Learning goals

- Understand CI test reports.
- Upload artifacts in GitHub Actions.
- Use logs and summaries to diagnose failures.

## What is a test report?

Maven test plugins produce report files under `target/surefire-reports` and, for integration tests, often `target/failsafe-reports`.

These files help CI show which tests failed.

## Artifact upload example

```yaml
- name: Upload test reports
  if: always()
  uses: actions/upload-artifact@v4
  with:
    name: test-reports
    path: |
      target/surefire-reports
      target/failsafe-reports
```

`if: always()` uploads reports even when tests fail.

## Build status badges

A badge can show current CI status in a README. It is useful only if CI is actually running meaningful checks.

## Common mistakes

- Uploading no reports because the path is wrong.
- Uploading large generated folders without need.
- Hiding logs by making scripts too quiet.
- Treating a green badge as proof of full quality.

## Mini exercises

1. Add artifact upload for Surefire reports.
2. Explain why `if: always()` is useful.
3. Add a CI summary line for test results.

## Quick summary

CI artifacts preserve evidence from builds so failures can be reviewed after the job ends.
