FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace
COPY src ./src
RUN javac -d out src/centralizedloggingstack/*.java

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S appgroup -g 10001 \
    && adduser -S appuser -u 10001 -G appgroup \
    && mkdir -p /var/log/java-app \
    && chown -R 10001:10001 /var/log/java-app
WORKDIR /app
COPY --from=build /workspace/out ./out

USER 10001:10001
CMD ["java", "-cp", "out", "centralizedloggingstack.Main"]
