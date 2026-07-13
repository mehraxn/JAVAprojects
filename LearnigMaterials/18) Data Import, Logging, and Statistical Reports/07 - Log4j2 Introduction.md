# Log4j2 Introduction

## Learning goals

- Understand basic Log4j2 setup.
- See a simple `log4j2.xml`.
- Use parameterized log messages.

## Dependency idea

In Maven, Log4j2 is added as dependencies. Exact versions change over time, so check current documentation when creating a real application.

## Basic usage

```java
private static final Logger log = LogManager.getLogger(OrderReportService.class);

public void buildReport() {
    log.info("Building order report");
}
```

## Example log4j2.xml

```xml
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss} %-5level %logger{36} - %msg%n"/>
        </Console>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="Console"/>
        </Root>
    </Loggers>
</Configuration>
```

## Parameterized logging

Prefer:

```java
log.info("Imported {} rows from {}", count, fileName);
```

Instead of string concatenation.

## Common mistakes

- Logging sensitive data.
- Setting root level to `trace` permanently.
- Forgetting configuration files in packaged applications.
- Logging and swallowing exceptions.

## Mini exercise

Write a Log4j2 configuration that prints `info` and above to the console.

## Quick summary

Log4j2 provides configurable application logging with clear levels and formatting.
