# Design Decisions

## Java and build

Java 21 is an LTS release with broad tooling support. Maven and the original `src/` plus `test/` layout are retained for professor-test compatibility. The included wrapper selects Maven 3.9.16, and JaCoCo generates reports without enforcing a coverage threshold.

## Intentionally compact model

`Student` and `Course` remain private static classes within `University`. This keeps the first project approachable while still separating record-specific behavior into small methods. Data stays in memory; no persistence or service layers are introduced.

## Fixed capacities

The assignment's fixed limits remain: 1000 students, 50 courses, 25 courses per student, and 100 attendees per course. Public operations check capacities and throw `IllegalStateException` instead of overflowing arrays.

## Identity and validation

Student IDs begin at 10000 and course codes at 10, increasing with successful creation. Names, titles, and teacher values are trimmed and must be non-blank. Missing registration/exam IDs produce `IllegalArgumentException`; original lookup methods retain their established null or empty-string behavior.

## Duplicate behavior

Registration is idempotent. A student/course pair appears once on both sides. Exams use one grade per student/course; a repeated exam updates the existing grade so both averages stay consistent.

## Grades, ranking, and logging

Grades must be in the inclusive 0–30 range. Ranking uses the assignment formula and deterministic name/ID tie-breaking. The public logger name and professor-tested messages are preserved; duplicate registration does not emit a second registration event because no state changes.

## Trade-offs

Linear searches and fixed arrays are appropriate at these small educational limits. A larger system would use indexed collections and persistence, but those additions would obscure the intended introductory OOP focus.
