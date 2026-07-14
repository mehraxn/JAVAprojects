# Diet and Takeaway Management System

## Overview

This educational Java domain application combines nutritional modeling with restaurant and takeaway-order workflows. The maintained Maven implementation is located in [`Project/`](Project), while [`Raw files/`](Raw%20files) retains the original course project for comparison.

## Features

- Raw materials and packaged products with nutritional data.
- Recipes normalized per 100 grams and complete menu calculations.
- Restaurants, menus, customers, orders, payment methods, and statuses.
- Opening-hour intervals, including overnight ranges and delivery-time adjustment.
- Queries for open restaurants and orders by status.
- Deterministic collections, validation, and automated workflow tests.

## What This Project Demonstrates

- Rich OOP domain modeling and connected object workflows.
- Collections, sorting, filtering, and formatted query results.
- Nutritional aggregation and unit-aware calculations.
- Opening-hour and order-lifecycle business rules.
- Maven testing while retaining an existing educational API.

## Tech Stack

- Java 21.
- Maven and Maven Wrapper.
- JUnit 4/JUnit 5 through the existing test setup.
- JaCoCo for optional local coverage reports.

## Project Structure

```text
.
├── Project/                   # Canonical implementation and Maven build
│   ├── src/diet/
│   ├── test/
│   ├── docs/
│   ├── scripts/
│   ├── pom.xml
│   └── TEST_RESULTS.md
├── Raw files/                # Preserved original course project
└── issue and implementation notes
```

## How to Run

```bash
cd Project
./mvnw clean test
```

Windows PowerShell:

```powershell
cd Project
.\mvnw.cmd clean test
```

With global Maven, use `mvn clean test`. See the [canonical README](Project/README.md) for nutritional formulas, the restaurant workflow, and optional coverage commands.

## Testing

Tests under `Project/test/` cover raw materials, products, recipes, menus, restaurants, customers, orders, queries, validation, and an end-to-end workflow. See [`Project/TEST_RESULTS.md`](Project/TEST_RESULTS.md) for the recorded validation history.

## Known Limitations

- Educational, in-memory model with no persistence.
- No database, REST API, authentication, frontend, payment integration, or deployment setup.
- Raw course materials and their generated artifacts remain preserved for later cleanup review.

## Resume Value

Built a Java diet and takeaway system covering nutritional calculations, recipes, menus, restaurant hours, customers, ordering workflows, validation, and automated tests.
