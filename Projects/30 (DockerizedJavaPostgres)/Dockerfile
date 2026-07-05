FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /build
COPY pom.xml .
COPY src ./src

# Maven downloads the PostgreSQL JDBC driver during an actual image build.
RUN mvn -q -DskipTests package dependency:copy-dependencies -DincludeScope=runtime

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /build/target/dockerized-java-postgres-1.0.0.jar ./app.jar
COPY --from=build /build/target/dependency ./lib

USER 10001
ENTRYPOINT ["java", "-cp", "app.jar:lib/*", "dockerizedjavapostgres.Main"]
