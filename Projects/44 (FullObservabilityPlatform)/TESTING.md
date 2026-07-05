# Testing Full Observability Platform

## Static checks

- [ ] Confirm metric names, log fields, trace attributes, and service names align.
- [ ] Confirm collector pipelines use only local/example destinations.
- [ ] Confirm dashboards and alerts contain no fabricated results.
- [ ] Confirm no secret, credential, or production endpoint exists.

## Deferred checks

- [ ] Compile and instrument the Java app.
- [ ] Validate each observability configuration with approved tooling.
- [ ] Correlate one safe request across metrics, logs, and traces.

No Java, Docker, collector, monitoring, logging, tracing, or dashboard command was executed.
