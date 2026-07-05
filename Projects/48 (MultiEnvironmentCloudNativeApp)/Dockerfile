# Builds the app in app/. ONE immutable image is built and promoted through all
# environments (build once, configure per env). See TESTING.md for a local build.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY app/src ./src
RUN javac -d out src/multienvironmentcloudnativeapp/*.java

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/out ./out
RUN addgroup -S app && adduser -S -G app -u 10001 app
USER 10001
EXPOSE 8080
# APP_PORT only; env-specific config (APP_ENVIRONMENT, LOG_LEVEL, flags) is
# injected at deploy time via ConfigMap/Secret, never baked into the image.
ENV APP_PORT=8080
ENTRYPOINT ["java", "-cp", "out", "multienvironmentcloudnativeapp.Main"]
