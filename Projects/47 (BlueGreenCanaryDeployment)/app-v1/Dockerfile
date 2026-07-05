# Placeholder image for app-v1. Would be tagged my-java-app:v1. NOT built here.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY src ./src
RUN javac -d out src/app/*.java

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/out ./out
RUN addgroup -S app && adduser -S -G app -u 10001 app
USER 10001
EXPOSE 8080
ENV APP_VERSION=v1 \
    APP_PORT=8080
ENTRYPOINT ["java", "-cp", "out", "app.Main"]
