# Hospital Queue Management

## Overview

This educational Java project models a hospital triage queue with explicit patient states and deterministic priority ordering. It demonstrates queue mechanics and business rules only; it is not clinical software.

## Features

- Register patients with unique identifiers and triage levels.
- Order waiting patients by medical priority, arrival time, and patient ID.
- Support emergency priority updates.
- Model `WAITING`, `IN_TREATMENT`, and `DISCHARGED` lifecycle transitions.
- Requeue a patient from treatment while retaining visit history.
- Report queue totals, counts by triage level, wait statistics, and the longest-waiting patient.
- Return defensive patient copies and unmodifiable collection views.
- CLI demonstrations for queues, emergencies, lifecycle rules, statistics, and validation.

## What This Project Demonstrates

- Priority queues and multi-field comparator ordering.
- OOP domain modeling with enums and explicit lifecycle transitions.
- Time-based statistics using deterministic inputs.
- Defensive copying and state-consistent service methods.
- Dependency-free automated tests and strict compilation.

## Tech Stack

- Java 21 standard library.
- Plain `javac`/`java`; no Maven or external dependencies.
- Dependency-free custom tests.
- Bash and PowerShell validation scripts.

## Project Structure

```text
.
├── src/hospitalqueuemanagement/     # Patient model, queue service, enums, CLI
├── tests/hospitalqueuemanagement/   # Dependency-free tests and runner
├── scripts/                          # test.sh and test.ps1
├── TESTING.md
└── TEST_RESULTS.md
```

## How to Run

```bash
javac -Xlint:all -Werror -d out src/hospitalqueuemanagement/*.java
java -cp out hospitalqueuemanagement.Main demo
```

Other commands include `help`, `queue-demo`, `emergency-demo`, `status-demo`, `statistics-demo`, and `validation-demo`.

## Testing

```bash
bash scripts/test.sh
```

Windows PowerShell:

```powershell
.\scripts\test.ps1
```

See [TESTING.md](TESTING.md) for exact commands and [TEST_RESULTS.md](TEST_RESULTS.md) for the latest recorded validation record.

## Known Limitations

- In-memory learning model with no persistence.
- No HTTP API, authentication, appointment scheduling, medical-record integration, or hospital-system integration.
- The triage labels and rules provide a programming example, not clinical guidance or a patient-safety guarantee.

## Resume Value

Built a Java triage-queue simulator with deterministic priority ordering, patient lifecycle management, emergency updates, queue statistics, defensive data handling, and automated tests.
