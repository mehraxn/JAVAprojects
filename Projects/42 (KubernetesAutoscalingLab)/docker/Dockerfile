FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY src ./src
RUN javac -d out src/kubernetesautoscalinglab/*.java

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/out ./out
USER 10001
CMD ["java", "-cp", "out", "kubernetesautoscalinglab.Main"]

# TODO: Add a defined non-root account and health check after HTTP endpoints exist.
