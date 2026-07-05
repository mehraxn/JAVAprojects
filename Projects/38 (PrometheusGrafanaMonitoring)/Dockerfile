FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY src ./src
RUN javac -d out src/prometheusgrafanamonitoring/*.java

USER 10001
CMD ["java", "-cp", "out", "prometheusgrafanamonitoring.Main"]
