FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY src ./src
RUN javac -d out src/centralizedloggingstack/*.java
RUN mkdir -p /var/log/phase3 && chown -R 10001:0 /var/log/phase3

USER 10001
CMD ["java", "-cp", "out", "centralizedloggingstack.Main"]
