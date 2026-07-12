# Hospital Queue Management

An educational, in-memory Java hospital triage queue focused on priority queues, OOP, defensive data handling, and clear business rules. It is a learning project—not clinical software.

## What it demonstrates

- A validated `Patient` domain model with arrival, treatment-start, and discharge timestamps.
- `TriageLevel` medical priority and `PatientStatus` lifecycle models.
- Priority ordering, emergency overrides, service/discharge workflow, and historical records.
- Defensive patient copies and unmodifiable collection snapshots.
- Queue statistics, command-based demos, dependency-free tests, and strict compilation.

## Features and rules

Patients have unique IDs. Waiting patients are served by:

1. Medical priority: `EMERGENCY`, `URGENT`, `STANDARD`, `NON_URGENT`.
2. Earlier arrival time.
3. Lexicographically earlier patient ID as the deterministic final tie-breaker.

The lifecycle is `WAITING -> IN_TREATMENT -> DISCHARGED`. `IN_TREATMENT -> WAITING` is supported and retains the treatment-start timestamp as visit history. `WAITING -> DISCHARGED` means the patient left without treatment and is removed from the queue. `DISCHARGED` is terminal. Only serving enters treatment; only waiting patients can receive priority updates.

Public patient-returning operations provide copies, and queue/record views are unmodifiable. The service can report waiting and record totals, waiting counts by triage level, the longest-waiting patient, average current wait (zero for an empty queue), and average served wait (empty until someone is served).

## Quick start

```text
javac -Xlint:all -Werror -d out src/hospitalqueuemanagement/*.java
java -cp out hospitalqueuemanagement.Main help
java -cp out hospitalqueuemanagement.Main demo
java -cp out hospitalqueuemanagement.Main queue-demo
java -cp out hospitalqueuemanagement.Main emergency-demo
java -cp out hospitalqueuemanagement.Main status-demo
java -cp out hospitalqueuemanagement.Main statistics-demo
java -cp out hospitalqueuemanagement.Main validation-demo
```

See [TESTING.md](TESTING.md) for exact test commands and [TEST_RESULTS.md](TEST_RESULTS.md) for the latest honest validation record.

## Limitations

This project has no database, HTTP API, login/authentication, scheduling system, medical-records integration, or real hospital integration. It has no production clinical-safety guarantee and is not intended for deployment in patient care.
