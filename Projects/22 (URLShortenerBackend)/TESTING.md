# Testing URL Shortener Backend

## Planned service tests

- Shorten a valid URL.
- Resolve a code and increment hit count.
- Generate different codes after collisions.
- Save and reload mappings.

## Planned HTTP tests

- Reject unsupported methods.
- Return clear status codes for invalid URLs and unknown codes.
- Verify manually created response bodies are escaped correctly.

## Planned validation tests

- Reject blank, malformed, or unsupported URLs.
- Reject blank short codes.
- Handle missing and empty storage files.
- Reject duplicate codes during loading.

## Manual checklist

- [ ] Implement deterministic collision tests.
- [ ] Implement file parsing before enabling persistence.
- [ ] Implement HttpServer contexts and method checks.
- [ ] Keep Main from blocking unless server mode is requested.
- [ ] Compile and run only when a JDK is available.
