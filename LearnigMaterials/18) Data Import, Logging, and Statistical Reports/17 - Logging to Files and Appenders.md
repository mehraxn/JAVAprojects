# Logging to Files and Appenders

## Learning goals

- Understand appenders.
- Configure console and file logging.
- Avoid sensitive or noisy logs.

## What is an appender?

An appender is a logging destination. Common appenders write to:

- console;
- file;
- rolling file.

## Console and file example

```xml
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d %-5level %logger - %msg%n"/>
        </Console>
        <File name="File" fileName="logs/app.log">
            <PatternLayout pattern="%d %-5level %logger - %msg%n"/>
        </File>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="File"/>
        </Root>
    </Loggers>
</Configuration>
```

## Rolling file idea

A rolling file appender creates new files when logs become large or when a time period changes.

## Logger per class

```java
private static final Logger log = LogManager.getLogger(InvoiceImportService.class);
```

## Common mistakes

- Logging private customer data or payment details.
- Logging at `debug` in a tight loop for large imports.
- Forgetting log directory permissions.
- Writing all messages as `error`.

## Mini exercises

1. Add a console appender.
2. Add a file appender.
3. Decide which log level fits "row skipped".

## Quick summary

Appenders control where logs go. Use levels and patterns intentionally.
