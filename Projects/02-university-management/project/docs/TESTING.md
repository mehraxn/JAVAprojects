# Testing

## Commands

Linux or macOS:

```bash
./mvnw clean test
bash scripts/test.sh
```

Windows PowerShell:

```powershell
.\mvnw.cmd clean test
.\scripts\test.ps1
```

Use `mvn clean test` when Maven is installed globally. Generate the non-gating JaCoCo report with `./mvnw clean test jacoco:report`; Maven writes it to `target/site/jacoco/index.html`.

## Test organization

- `test/it/polito/po/test`: professor/base tests.
- `test/example`: supplied usage examples.
- `test/custom`: validation, registration, exam, average, capacity, and ranking edge cases.

Every test creates its own `University` instance and must not depend on method or class execution order. The public logger is restored by tests that install filters. Compiled classes, Surefire results, and coverage output live under the generated `target/` directory, which is ignored and removed from the final repository state.

## Common failures

- `release version 21 not supported`: Maven is running on an older JDK.
- Wrapper download failure: verify network access or use a local Maven 3.9.x installation.
- PowerShell execution-policy failure: invoke `powershell -ExecutionPolicy Bypass -File scripts/test.ps1` or run `mvnw.cmd` directly.
