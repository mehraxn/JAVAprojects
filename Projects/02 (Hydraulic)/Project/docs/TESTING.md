# Testing

## Run the suite

From the project root:

```bash
./mvnw clean test
```

On Windows PowerShell:

```powershell
.\mvnw.cmd clean test
```

The convenience scripts provide the same behavior and fall back to a system Maven installation:

```bash
bash scripts/test.sh
```

```powershell
.\scripts\test.ps1
```

To regenerate the JaCoCo HTML report, run `./mvnw clean test jacoco:report`. The report is written to `target/site/jacoco/index.html`.

## Test organization

- `test/it/polito/oop/test`: original professor/base behavior tests.
- `test/example`: course examples.
- `test/custom`: validation, simulation, branching safety, deletion, builder, and rendering edge cases.

Tests create fresh systems and observers, do not use shared mutable fixtures, and do not rely on execution order. Maven writes compiled classes, reports, and coverage data under `target/`; that generated directory is ignored and should not be committed.

## Common failures

- `release version 21 not supported`: run Maven with JDK 21 or newer.
- Wrapper download failure: check network access or use an installed Maven 3.9.x.
- PowerShell execution-policy error: invoke `powershell -ExecutionPolicy Bypass -File scripts/test.ps1` or use `mvnw.cmd` directly.
