# Golden-path service image for __SERVICE_NAME__.
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

EXPOSE __SERVICE_PORT__
ENV SERVICE_NAME=__SERVICE_NAME__ \
    SERVICE_PORT=__SERVICE_PORT__

ENTRYPOINT ["java", "-cp", "out", "app.Main"]
