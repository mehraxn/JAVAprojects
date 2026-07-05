# Multi-stage build for the autoscaling lab app.
# NOTE: This image is NOT built in this repository. The commands to build and
# push it are documented (as NOT executed) in README.md and TESTING.md.

FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY src ./src
RUN javac -d out src/kubernetesautoscalinglab/*.java

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/out ./out

# Run as an unprivileged, non-root user.
RUN addgroup -S app && adduser -S -G app -u 10001 app
USER 10001

EXPOSE 8080
ENV PORT=8080 \
    MAX_WORK_MS=2000

# Container-level healthcheck against the liveness endpoint. Kubernetes uses
# the probes in k8s/deployment.yaml instead of this when running in-cluster.
HEALTHCHECK --interval=15s --timeout=3s --start-period=10s --retries=3 \
    CMD wget -q -O /dev/null http://127.0.0.1:8080/health || exit 1

CMD ["java", "-cp", "out", "kubernetesautoscalinglab.Main"]
