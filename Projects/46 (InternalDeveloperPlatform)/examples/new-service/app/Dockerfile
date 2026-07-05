# Rendered image for payments-api. NOT built.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY src ./src
RUN javac -d out src/service/*.java

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/out ./out
RUN addgroup -S app && adduser -S -G app -u 10001 app
USER 10001
EXPOSE 8080
ENV SERVICE_NAME=payments-api \
    SERVICE_PORT=8080
ENTRYPOINT ["java", "-cp", "out", "service.Main"]
