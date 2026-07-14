# Mountain Huts Data Analysis

## Overview

This educational Java project imports regional mountain-hut data from a semicolon-separated CSV file and answers grouped statistical queries with the Stream API. The canonical Maven implementation is in [`project/`](project); [`Raw files/`](Raw%20files) preserves the original course project.

## Features

- Model regions, municipalities, huts, and configurable altitude ranges.
- Import UTF-8 data with validated, row-numbered errors.
- Use municipality altitude when a hut altitude is unavailable.
- Group huts and municipalities by province, municipality, and altitude range.
- Calculate hut counts, bed totals, and maximum bed capacity.
- Return deterministic, unmodifiable query results.

## What This Project Demonstrates

- Java domain modeling and aggregate ownership.
- Format-specific CSV parsing and validation.
- Stream API grouping, mapping, counting, summing, and maxima.
- Optional data, altitude classification, and deterministic reports.
- Maven, JUnit, and preserved professor-test compatibility.

## Tech Stack

- Java 21 and Java Stream API.
- Maven and Maven Wrapper.
- JUnit 5 and JaCoCo.
- GitHub Actions workflow in the canonical project.

## Architecture / Design

`Region` owns municipalities, huts, and altitude ranges. CSV rows are validated and converted into immutable domain objects; query methods then use streams to produce ordered report maps. Storage is file/import based, not database backed.

## Project Structure

```text
.
├── project/                   # Canonical Maven implementation
│   ├── src/mountainhuts/
│   ├── test/
│   ├── data/mountain_huts.csv
│   ├── docs/ and scripts/
│   ├── pom.xml
│   └── TEST_RESULTS.md
└── Raw files/                # Preserved original course project
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

See the [canonical README](project/README.md) for the dataset format, query names, and optional coverage command.

## Testing

The canonical test tree contains supplied and custom tests for altitude ranges, municipalities, huts, CSV import, queries, and validation. See [`project/TEST_RESULTS.md`](project/TEST_RESULTS.md) for the latest recorded validation results.

## Known Limitations

- Educational, local data-analysis project with no database, REST API, or UI.
- The importer targets the supplied semicolon-separated format and does not implement full RFC 4180 quoting.
- The bundled dataset covers a single region.
- Generated artifacts inside the preserved raw tree remain for a later cleanup review.

## Resume Value

Built a Java data-analysis application that imports and validates mountain-hut CSV data and produces deterministic grouped reports with the Stream API.
