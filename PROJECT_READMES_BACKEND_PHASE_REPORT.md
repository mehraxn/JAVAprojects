# Backend and Intermediate Project README Phase Report

## Scope

Prompt 5 handled the 15 current project folders numbered 16–30 under `Projects/`. The phase covers intermediate domain workflows, file/CSV persistence and analytics, JDBC boundaries, framework-free HTTP APIs, authentication concepts, and nested Maven/JPA applications. Final folder names and difficulty order came from the repository audit and rename report.

## Projects Processed

| # | Project folder | README status | Maven | Tests detected | TEST_RESULTS | Backend/DB/API tech | Notes |
|---:|---|---|:---:|:---:|:---:|---|---|
| 16 | `16-train-ticket-reservation-system` | UPDATED | NO | YES | YES | In-memory | Added complete portfolio sections while preserving reservation and lifecycle rules. |
| 17 | `17-mini-ecommerce-backend` | UPDATED | NO | YES | YES | In-memory service | Clarified that this is a service/CLI backend model, not an HTTP API. |
| 18 | `18-notification-service` | UPDATED | NO | YES | YES | In-memory, mock channels | Real email/SMS/push delivery is explicitly not claimed. |
| 19 | `19-file-based-address-book` | UPDATED | NO | YES | YES | File-based TSV | Added design, structure, evidence, limitations, and resume value. |
| 20 | `20-expense-tracker` | UPDATED | NO | YES | YES | CSV | Documented the store abstraction, `BigDecimal` reporting, and local persistence. |
| 21 | `21-job-application-tracker` | UPDATED | NO | YES | YES | CSV, repository | Added explicit repository/service architecture and local-storage limitations. |
| 22 | `22-csv-analytics-engine` | UPDATED | NO | YES | YES | CSV analytics | Documented the parser-to-model-to-analytics pipeline and precise statistics. |
| 23 | `23-mountain-huts-data-analysis` | CREATED | YES | YES | YES | CSV, Stream API | Added a root landing README; canonical Maven project remains in `project/`. |
| 24 | `24-task-manager-jdbc` | UPDATED | NO | YES | YES | JDBC, in-memory test repository | JDBC code uses prepared statements, but no driver/schema/database integration is bundled. |
| 25 | `25-url-shortener-backend` | UPDATED | NO | YES | YES | HTTP server, CSV | Preserved verified routes and clarified explicit CSV persistence and framework-free scope. |
| 26 | `26-contacts-rest-api` | UPDATED | NO | YES | YES | REST-style HTTP, in-memory | Preserved the implemented endpoint table and manual JSON behavior. |
| 27 | `27-blog-api` | UPDATED | NO | YES | YES | REST-style HTTP, in-memory | Reorganized verified user/post/comment routes and cascading cleanup. |
| 28 | `28-authentication-system` | UPDATED | NO | YES | YES | PBKDF2, in-memory sessions | Removed the broad “fully covered” claim and retained explicit educational security limits. |
| 29 | `29-social-network-jpa` | CREATED | YES | YES | YES | JPA/Hibernate, H2 | Added a root landing README; documented file-based runtime H2 and in-memory test H2. |
| 30 | `30-weather-system-jpa` | CREATED | YES | YES | YES | JPA/Hibernate, H2, CSV | Added a concise root landing README for the layered monitoring-data application. |

Totals: 3 READMEs created, 12 updated, 0 left as-is, and 0 skipped.

## Important Notes

- Java source code was not modified.
- Maven POMs, wrappers, tests, scripts, CI files, persistence configuration, and application configuration were not modified.
- Project folders were not renamed, moved, or flattened.
- Generated files were not deleted or edited.
- READMEs are based on inspected source classes, tests, scripts, POMs, persistence units, canonical documentation, route tables, and `TEST_RESULTS.md` evidence.
- Projects 25–27 use the JDK's lightweight `HttpServer`; they are not described as Spring Boot applications.
- Projects 17 and 24 do not expose HTTP APIs. Project 24 provides a JDBC repository boundary, not a bundled runnable database environment.
- Projects 29 and 30 use JPA/Hibernate with H2. Social uses file-based H2 for local runtime and in-memory H2 for tests; WeatherSystem uses in-memory H2 for both runtime and test persistence units.
- WeatherSystem remains at final position 30 and is described as an advanced layered Java persistence project. It is not described as a REST service or production monitoring deployment.
- Authentication claims remain conservative: PBKDF2, random salts/tokens, expiry, revocation, and role checks are implemented, but the project is not presented as a real identity provider.
- The root repository README remained accurate and was not changed.

## Projects Needing Code Cleanup Later

### `21-job-application-tracker`

- CSV writes do not yet provide atomic replacement or backups.
- Status changes are accepted without a constrained transition workflow.

### `24-task-manager-jdbc`

- No JDBC driver, database instance, schema migration, or sample schema is bundled.
- Real JDBC integration tests are absent; repository behavior tests use the in-memory implementation, while JDBC SQL is compiled and statically checked.
- The intended database vendor and local setup remain deliberately unspecified.

### `25-url-shortener-backend`

- CSV persistence is explicit rather than automatically wired into server lifecycle.
- CSV storage does not coordinate concurrent writers or support multiline values.
- Sequential codes are predictable; this is documented as an educational limitation.

### `28-authentication-system`

- No rate limiting, account lockout, MFA, password reset, persistence, or audit-log integration is included.
- Timing hardening is educational and should not be treated as complete side-channel protection.

### `23-mountain-huts-data-analysis`, `29-social-network-jpa`, and `30-weather-system-jpa`

- Each retains a generated `target/` inside its preserved `Raw files/` tree. A later cleanup must distinguish raw course material from the canonical project before removing artifacts.

No project in this phase is missing automated-test source or `TEST_RESULTS.md`. Project 24 is the only project with a materially partial database-integration story; its README states that limitation directly.
