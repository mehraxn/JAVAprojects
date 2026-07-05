# Testing Testing Coverage Quality Gate

No Java, Maven, JUnit, JaCoCo, report, quality gate, or CI workflow was executed while preparing this project.

## Static validation checklist

- [ ] Review subtotal, discount, and rounding behavior.
- [ ] Confirm null, negative, zero, and boundary values are addressed.
- [ ] Confirm test names describe behavior and assertions verify results.
- [ ] Confirm the configured coverage counter, ratio, and minimum are intentional.
- [ ] Confirm exclusions are narrow and documented.

## File existence checks

- [ ] Main source files exist under `src/main/java`.
- [ ] JUnit files exist under `src/test/java`.
- [ ] `pom.xml` exists.
- [ ] `configs/quality-gate.properties` exists.
- [ ] `.github/workflows/quality.yml` exists.
- [ ] `docs/QUALITY_GATE.md`, `README.md`, and `TESTING.md` exist.

## Configuration review checklist

- [ ] Java release, JUnit version, and plugin versions are explicit.
- [ ] Surefire is configured to discover JUnit 5 tests.
- [ ] JaCoCo prepares its agent before tests and checks coverage during verification.
- [ ] The report path and CI artifact path agree.
- [ ] The workflow cache points to this project's `pom.xml`.
- [ ] The nested workflow limitation is documented.

## Security checks

- [ ] No real secret or credential is present.
- [ ] No production endpoint is present.
- [ ] No test embeds a token, password, or private key.
- [ ] CI permissions remain minimal.

## Commands normally used - NOT executed

```text
mvn test
mvn verify
```

In CI, the prepared workflow would normally run `mvn --batch-mode --no-transfer-progress verify`. None of these commands were executed.

## Expected results in a proper environment

- Source and tests compile with the declared dependencies.
- JUnit reports all prepared normal, boundary, validation, and rounding tests.
- JaCoCo writes an HTML report under `target/site/jacoco`.
- `mvn verify` fails when tests fail or line coverage is below 80%.
- A passing automated gate is followed by successful manual quality review.
