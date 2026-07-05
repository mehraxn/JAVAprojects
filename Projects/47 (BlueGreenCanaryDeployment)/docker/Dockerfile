FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY src ./src
RUN javac -d out src/bluegreencanarydeployment/*.java

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/out ./out
USER 10001
CMD ["java", "-cp", "out", "bluegreencanarydeployment.Main"]

# TODO: Add a named non-root user and probes after HTTP endpoints exist.
