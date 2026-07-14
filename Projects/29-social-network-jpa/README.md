# Social Network JPA

## Overview

This educational Java backend models people, friendships, groups, memberships, and posts using JPA/Hibernate over H2. The canonical Maven application is located in [`project/`](project); [`Raw files/`](Raw%20files) preserves the original course version.

## Features

- Register people with unique codes.
- Create symmetric friendships with duplicate and self-friendship protection.
- Create, rename, delete, and manage group memberships.
- Publish posts and retrieve paginated user and friends feeds.
- Calculate deterministic social and group statistics.
- Validate inputs through a facade and custom exception hierarchy.

## What This Project Demonstrates

- JPA entity relationships and Hibernate ORM.
- Facade and repository patterns.
- Self-referential friendships, many-to-many memberships, and one-to-many posts.
- JPQL queries, pagination, transactions, and persistence-unit selection.
- Maven/JUnit testing against isolated H2 state.

## Tech Stack

- Java 21, Maven, and Maven Wrapper.
- Jakarta Persistence and Hibernate ORM 6.
- H2 file-based local storage and in-memory test storage.
- JUnit 5, JaCoCo, and a canonical GitHub Actions workflow.

## Architecture / Design

```text
Social facade → repositories → EntityManager/JPAUtil → Hibernate → H2
```

`socialPU` uses a file-based H2 database for local runs; `socialPUTest` uses an in-memory create/drop schema for tests. The project exposes a Java facade, not an HTTP API.

## Project Structure

```text
.
├── project/                   # Canonical Maven/JPA implementation
│   ├── src/social/
│   ├── resources/META-INF/persistence.xml
│   ├── test/
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

See the [canonical README](project/README.md) for detailed persistence notes and the original requirements.

## Testing

The canonical project includes supplied and custom tests for people, friendships, groups, posts, persistence, queries, and validation. See [`project/TEST_RESULTS.md`](project/TEST_RESULTS.md) for the latest recorded validation results.

## Known Limitations

- Local educational backend using H2 only.
- No REST API, UI, authentication, external services, or deployment profile.
- Generated artifacts inside the preserved raw tree remain for later cleanup review.

## Resume Value

Built a layered JPA/Hibernate social-network backend with entity relationships, repositories, transactions, JPQL pagination, validation, H2 persistence, and automated tests.
