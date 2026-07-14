FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY services/notification-service/src ./src
RUN javac --add-modules jdk.httpserver -d out src/notificationservice/*.java

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S appgroup -g 10001 && adduser -S appuser -u 10001 -G appgroup
WORKDIR /app
COPY --from=build /workspace/out ./out
USER 10001:10001
EXPOSE 8083
CMD ["java", "--add-modules", "jdk.httpserver", "-cp", "out", "notificationservice.Main"]
