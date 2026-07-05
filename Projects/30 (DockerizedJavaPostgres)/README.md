# Dockerized Java PostgreSQL

## Description

An educational Java project prepared to run an application and PostgreSQL as separate containers. It demonstrates how application code, JDBC-style persistence, environment variables, image construction, and Docker Compose fit together without presenting the setup as production-ready.

## Goal

The goal is to understand the boundary between a Java application and its database: Java owns the model, service, repository, and connection logic; Compose supplies networking, configuration, and PostgreSQL persistence.

## Technologies and concepts used

- Java and standard `java.sql` APIs
- Model, repository, service, and application-entry-point separation
- PostgreSQL connection configuration through environment variables
- Docker multi-stage image concepts
- Docker Compose services, networks, dependencies, and volumes
- Placeholder configuration and secret-handling awareness

## Project structure

```text
src/                         Java application source
pom.xml                      Maven build and PostgreSQL driver declaration
Dockerfile                   Java application image definition
docker-compose.yml           Application and PostgreSQL services
configs/init.sql             Initial tasks table
.env.example                 Placeholder local configuration
README.md                    Project documentation
TESTING.md                   Static and deferred validation guide
```

## Important files explained

- `src/` contains the application entry point and the model, repository, service, and database-configuration classes.
- `pom.xml` sets Java 21 compilation and declares the PostgreSQL JDBC driver as a runtime dependency.
- `Dockerfile` describes how the Java application would be built and placed in a runtime image.
- `docker-compose.yml` connects the application to PostgreSQL by Compose service name and defines database persistence.
- `configs/init.sql` creates the learning tasks table when PostgreSQL initializes an empty data directory.
- `.env.example` documents required variables without containing a real password.
- `TESTING.md` separates checks that can be performed statically from commands requiring an installed runtime.

## Intended real-environment workflow

In an approved local lab, a developer would copy `.env.example` to an ignored `.env`, replace every placeholder, review the resolved Compose configuration, build the application image, start both services, inspect startup logs, and exercise the Java application. PostgreSQL would be reached through its Compose service name rather than `localhost` from inside the application container.

The Java code uses standard JDBC interfaces, but a compatible PostgreSQL JDBC driver must be available through the documented build/runtime setup. No driver was installed during preparation.

## Prepared but not executed

- Java source and JDBC-style classes were prepared.
- Docker and Compose configuration were prepared.
- PostgreSQL environment variables and persistent storage were described.
- No Java compilation, image build, container startup, JDBC connection, SQL statement, or database initialization was executed.
- No successful connection or deployed service is claimed.

## Manual validation checklist

- [ ] Confirm `.env` is ignored and contains only disposable local values.
- [ ] Confirm the password placeholder has been replaced before resolving Compose configuration.
- [ ] Review application and database environment-variable names for exact agreement.
- [ ] Confirm the JDBC URL uses the PostgreSQL Compose service name.
- [ ] Confirm the PostgreSQL volume is mounted to the expected data directory.
- [ ] Confirm the JDBC driver is deliberately provided before attempting a connection.
- [ ] Review container logs without exposing credentials.
- [ ] Verify application behavior and persistence only in an approved disposable environment.

## Common mistakes avoided

- No real database password is committed.
- Compose requires an explicit password variable instead of silently accepting a default password.
- The application does not assume that container-local `localhost` means PostgreSQL.
- Database files are not stored only in the disposable container layer.
- JDBC requirements are documented instead of claiming the standard JDK includes a PostgreSQL driver.
- Container startup order is not described as proof that the database is immediately ready.
- No successful Docker or database execution is claimed.

## Possible future improvements

- Add automated repository and service tests.
- Add bounded database connection retries and clearer startup diagnostics.
- Introduce a reviewed migration mechanism.
- Add connection pooling only when the learning scope justifies it.
- Add container health checks and least-privilege database roles.
- Replace local placeholders with an approved secret-delivery mechanism.
