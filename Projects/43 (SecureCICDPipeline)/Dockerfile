# Secure CI/CD pipeline example image.
# NOTE: This image is NOT built in this repository. The pipeline that would
# build, scan, and sign it is a non-executed template (see ci/ and README.md).
#
# Security choices worth noting:
#   - multi-stage build: the JDK never ships in the final image
#   - pinned, minimal JRE base
#   - dedicated non-root user
#   - OCI labels for provenance / traceability

FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY src ./src
RUN javac -d out src/securecicdpipeline/*.java

FROM eclipse-temurin:21-jre-alpine

# Provenance metadata. In CI these ARGs would be filled from the build context.
ARG APP_VERSION=0.0.0
ARG GIT_SHA=unknown
LABEL org.opencontainers.image.title="secure-cicd-app" \
      org.opencontainers.image.description="Example app for the secure CI/CD pipeline lab" \
      org.opencontainers.image.version="${APP_VERSION}" \
      org.opencontainers.image.revision="${GIT_SHA}" \
      org.opencontainers.image.source="https://github.com/example/secure-cicd-app"

WORKDIR /app
COPY --from=build /workspace/out ./out

# Dedicated unprivileged account.
RUN addgroup -S app && adduser -S -G app -u 10001 app
USER 10001

ENV APP_VERSION=${APP_VERSION} \
    GIT_SHA=${GIT_SHA}

ENTRYPOINT ["java", "-cp", "out", "securecicdpipeline.Main"]
