# Weather System JPA

## Overview

This Maven-based Java application manages environmental measurements across networks, gateways, and sensors. It uses a layered JPA/Hibernate design with H2 persistence, CSV import, threshold alert logging, authorization rules, and statistical reports. The canonical application is in [`project/`](project); [`Raw files/`](Raw%20files) preserves the original project.

## Features

- Manage users, operators, networks, gateways, sensors, parameters, and thresholds.
- Persist topology and measurements through JPA repositories.
- Import UTF-8 CSV measurements with partial success and row-numbered errors.
- Enforce validation, topology rules, and operation-level authorization.
- Log threshold alerts without contacting an external notification provider.
- Produce network, gateway, and sensor reports with mean, sample variance, standard deviation, outliers, histograms, and load ratios.
- Query measurements by date range through JPQL/typed queries.

## What This Project Demonstrates

- Layered facade, operations, services, repositories, and persistence components.
- Jakarta Persistence, Hibernate ORM, H2, transactions, and entity relationships.
- Repository pattern with generic CRUD and specialized measurement queries.
- CSV parsing, date validation, custom exceptions, and log4j2 logging.
- Statistical edge-case handling and immutable report results.
- JUnit/Mockito testing and documented phased refactoring.

## Tech Stack

- Java 21, Maven, and Maven Wrapper.
- Jakarta Persistence, Hibernate ORM, and H2.
- JUnit 5, Mockito, log4j2, and JaCoCo.
- GitHub Actions workflow in the canonical project.

## Architecture / Design

```text
WeatherReport facade
  → operation classes
  → import/alerting/report services
  → repositories
  → JPA/Hibernate
  → H2
```

The runtime and test persistence units are both local, in-memory H2 configurations. `PersistenceManager` selects the appropriate unit, while repository and operation layers keep persistence concerns away from the facade.

## Project Structure

```text
.
├── project/                   # Canonical Maven/JPA implementation
│   ├── src/main/java/com/weather/report/
│   ├── src/main/resources/META-INF/persistence.xml
│   ├── src/test/java/com/weather/report/test/
│   ├── docs/ and scripts/
│   ├── pom.xml
│   └── TEST_RESULTS.md
├── ProjectExplanation files/
└── Raw files/                # Preserved original project
```

## How to Run

```bash
cd project
./mvnw clean test
```

Windows PowerShell:

```powershell
cd project
.\mvnw.cmd clean test
```

The [canonical README](project/README.md) contains the detailed API specification, report semantics, CSV format, persistence notes, and coverage command.

## Testing

The canonical suite contains supplied and custom tests for repositories, queries, reports, statistics, imports, validation, authorization, topology, persistence configuration, threshold alerting, and an end-to-end workflow. See [`project/TEST_RESULTS.md`](project/TEST_RESULTS.md) for the latest recorded validation results.

## Known Limitations

- Educational local application using in-memory H2; no production datastore or deployment profile.
- No REST API, frontend, Docker packaging, or real-time ingestion pipeline.
- Threshold alerts are log messages, not real email/SMS/push delivery.
- The CSV parser does not support multiline quoted fields, and no large-dataset benchmark is included.
- Generated artifacts inside the preserved raw tree remain for later cleanup review.

## Resume Value

Built a layered Java monitoring-data application with JPA/Hibernate repositories, H2 persistence, CSV imports, authorization, threshold alerting, statistical reports, extensive automated tests, and documented architecture decisions.
