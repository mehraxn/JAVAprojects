# Canonical image build for the secure CI/CD pipeline lab.
#
# Security choices worth noting:
#   - multi-stage build: the JDK never ships in the final image
#   - minimal JRE base image
#   - dedicated non-root user
#   - OCI labels for provenance / traceability
#
# Base images use versioned tags (e.g. eclipse-temurin:21-jre-alpine) for
# readability. They are NOT digest-pinned. A production pipeline should pin
# base images by digest (FROM image@sha256:...) and update the digests through
# a controlled, reviewed process.

FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY src ./src
RUN javac -Xlint:all -Werror -d out src/securecicdpipeline/*.java

FROM eclipse-temurin:21-jre-alpine

# Apply Alpine security updates published after the base image was cut, so the
# image scan gate isn't failed by already-patched OS CVEs.
RUN apk -U upgrade --no-cache

# Provenance metadata. CI fills these ARGs from the build context.
ARG APP_VERSION=0.0.0
ARG GIT_SHA=unknown
LABEL org.opencontainers.image.title="secure-cicd-java-app" \
      org.opencontainers.image.description="Example app for the secure CI/CD pipeline lab" \
      org.opencontainers.image.version="${APP_VERSION}" \
      org.opencontainers.image.revision="${GIT_SHA}" \
      org.opencontainers.image.source="https://github.com/example/secure-cicd-java-app"

WORKDIR /app
COPY --from=build /workspace/out ./out

# Dedicated unprivileged account.
RUN addgroup -S app && adduser -S -G app -u 10001 app
USER 10001

ENV APP_VERSION=${APP_VERSION} \
    GIT_SHA=${GIT_SHA}

ENTRYPOINT ["java", "-cp", "out", "securecicdpipeline.Main"]
