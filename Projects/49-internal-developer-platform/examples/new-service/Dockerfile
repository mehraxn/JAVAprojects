# Golden-path service image for payments-api.
# Multi-stage build: compile with the JDK, run on a small JRE as a non-root user.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY src ./src
RUN javac -d out src/app/*.java

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/out ./out

# Run as an unprivileged user, never root.
RUN addgroup -S app && adduser -S -G app -u 10001 app
USER 10001

EXPOSE 8080
ENV SERVICE_NAME=payments-api \
    SERVICE_PORT=8080

ENTRYPOINT ["java", "-cp", "out", "app.Main"]
