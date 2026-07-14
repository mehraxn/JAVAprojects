# Build the dependency-free Java application.
FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace
COPY src ./src
RUN javac --add-modules jdk.httpserver -d out src/kubernetesdeploymentjavaapp/*.java

# Run as a non-root user in a smaller runtime image.
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S appgroup -g 10001 && adduser -S appuser -u 10001 -G appgroup
WORKDIR /app
COPY --from=build /workspace/out ./out

USER 10001:10001
EXPOSE 8080

CMD ["java", "--add-modules", "jdk.httpserver", "-cp", "out", "kubernetesdeploymentjavaapp.Main"]
