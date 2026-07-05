# Build stage: Maven compiles the Java application and copies its runtime driver.
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /workspace
COPY pom.xml .
RUN mvn --batch-mode --no-transfer-progress dependency:go-offline
COPY src ./src
RUN mvn --batch-mode --no-transfer-progress package

# Runtime stage: the final image does not contain Maven or the source tree.
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=build /workspace/target/docker-compose-full-stack-backend-1.0.0.jar app.jar
COPY --from=build /workspace/target/dependency ./lib

USER appuser
EXPOSE 8080

CMD ["java", "--add-modules", "jdk.httpserver", "-cp", "app.jar:lib/*", "dockercomposefullstack.Main"]
