# Logging Basics

## Learning goals

- Understand why logging is better than `System.out` for applications.
- Learn log levels.
- Avoid sensitive or noisy logs.

## Why logging?

`System.out.println` is fine for tiny demos. Applications need logs that can be filtered, formatted, stored, and disabled by level.

## Log levels

| Level | Use |
|---|---|
| `trace` | Very detailed diagnostic information |
| `debug` | Developer troubleshooting |
| `info` | Normal important events |
| `warn` | Suspicious but recoverable situation |
| `error` | Failure that needs attention |

## Logger per class

```java
private static final Logger log = LogManager.getLogger(ProductImportService.class);
```

## Example

```java
log.info("Import started for file {}", path);
log.warn("Skipping row {} because price is missing", rowNumber);
log.error("Import failed", exception);
```

## Common mistakes

- Logging passwords, tokens, or private data.
- Using `printStackTrace` everywhere.
- Logging too much inside loops.
- Catching exceptions only to log and ignore them.

## Mini exercise

Choose log levels for: import started, skipped row, invalid file path, and successful report generation.

## Quick summary

Logging makes application behavior visible without tying code to console printing.
