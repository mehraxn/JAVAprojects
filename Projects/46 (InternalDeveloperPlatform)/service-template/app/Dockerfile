# Golden-path service image. __TOKEN__ values are set by the generator.
# NOT built in this repo.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY src ./src
RUN javac -d out src/service/*.java

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/out ./out
RUN addgroup -S app && adduser -S -G app -u 10001 app
USER 10001
EXPOSE __SERVICE_PORT__
ENV SERVICE_NAME=__SERVICE_NAME__ \
    SERVICE_PORT=__SERVICE_PORT__
ENTRYPOINT ["java", "-cp", "out", "service.Main"]
