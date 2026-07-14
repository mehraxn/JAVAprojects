FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY services/payment-service/src ./src
RUN javac --add-modules jdk.httpserver -d out src/paymentservice/*.java

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S appgroup -g 10001 && adduser -S appuser -u 10001 -G appgroup
WORKDIR /app
COPY --from=build /workspace/out ./out
USER 10001:10001
EXPOSE 8082
CMD ["java", "--add-modules", "jdk.httpserver", "-cp", "out", "paymentservice.Main"]
