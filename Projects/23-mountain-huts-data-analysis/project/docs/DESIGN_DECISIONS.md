# Design Decisions

## Java 21

The project originally targeted Java 25 (`release` 25), which does not compile on the available
JDK 21. It now targets **Java 21 (LTS)** (`maven.compiler.release=21`); no Java-25 feature was needed.

## Maven

A single-module Maven build with custom `src`/`test` source directories (kept from the lab layout) and
a Maven wrapper for reproducible builds. JUnit 5 (Jupiter, with the Vintage engine available) runs the
tests; JaCoCo provides coverage without an enforced gate.

## No database

The domain is small and file-based. Data is loaded from a semicolon-separated CSV into in-memory
domain objects — no database, ORM, or external service is required for this educational project.

## CSV import

`Region.fromFile` reads UTF-8 with `Files.newBufferedReader` and try-with-resources, validates the
path/existence, and validates each row (7 fields, required non-blank fields, integer fields) with
**row-numbered** error messages. On error it throws `IllegalArgumentException` (preserving the cause)
rather than silently returning empty data. The importer targets the project's expected CSV format
(simple `;`-separated fields), not full RFC-4180 CSV (no quoted-field/embedded-separator support).

## Missing hut altitude

The hut altitude is optional (`Optional<Integer>`, stored as a nullable `Integer`). For
altitude-range classification, a hut uses its own altitude when present, otherwise it falls back to
the altitude of its municipality — the behavior required by the specification.

## Altitude-range membership

Ranges are `"min-max"` labels and membership is **left-open, right-closed** (`min < a <= max`), so e.g.
`2000` belongs to `"1000-2000"` and not to `"2000-3000"`. This exactly matches the professor tests and
is preserved deliberately (it is *not* changed to an inclusive lower bound). Ranges are validated
individually (format, non-negative bounds, `min <= max`).

## Duplicate municipality / hut behavior

`createOrGetMunicipality` and `createOrGetMountainHut` de-duplicate by name: if an entity with the
same name already exists, the existing object is returned (identity preserved), matching the spec and
tests.

## Deterministic output

Report queries collect into `TreeMap`s, and `getMunicipalities`/`getMountainHuts` return
name-sorted, unmodifiable copies. Content is unchanged (tests compare by content), but ordering is now
stable for demos and documentation.

## Known trade-offs / limitations

- CSV importer is format-specific (no quoted fields); H2/DB, REST, and UI are intentionally absent.
- Region keys entities by the given name; the CSV importer trims fields before creating entities, so
  keys and stored names are consistent for the dataset.
