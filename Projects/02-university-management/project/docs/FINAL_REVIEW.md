# Final Review

## Status

The project targets Java 21, preserves its professor-facing API and output formats, validates unsafe input, prevents duplicate registrations, keeps exam averages synchronized, and provides repeatable tests through Maven, wrapper scripts, and CI.

## Strengths

- Compact OOP facade with private student and course models.
- Stable progressive student IDs and course codes.
- Bidirectional, idempotent registration.
- Validated and updatable exam records.
- Student/course averages and deterministic top-three ranking.
- Professor-compatible activity logging.
- Edge-case tests, coverage reporting, scripts, and documentation.

## Remaining limitations

- In-memory educational model with fixed capacities.
- Linear record lookup, appropriate for the assignment scale.
- No persistence, concurrency, API, UI, or deployment layer.

## GitHub checklist

- [x] Java 21 Maven configuration
- [x] Maven wrapper
- [x] Cross-platform test scripts
- [x] GitHub Actions workflow
- [x] Generated files ignored and removed
- [x] Professor and custom tests
- [x] Architecture and testing documentation
- [x] Honest results record

## Resume line

Built and validated a Java university-management system with student enrollment, course activation, registrations, exam recording, grade averages, top-student ranking, logging, automated tests, and clean Maven project documentation.
