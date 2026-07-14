# Test Results

Date: 2026-07-11

All commands below were actually executed on a Windows 11 machine from this project directory. No system-wide JDK was installed, so validation used a portable Eclipse Temurin JDK 21.0.11. Nothing in this file is estimated or assumed.

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK Temurin 21.0.11+10 (portable; no system JDK installed) |
| Compile application source | PASS | `javac -d out src/cipipelinejavaapp/*.java`, exit code 0, no warnings |
| Compile test source | PASS | `javac -cp out -d test-out test/cipipelinejavaapp/*.java`, exit code 0 |
| Run tests | PASS | `java -cp "out;test-out" cipipelinejavaapp.GreetingServiceTest` printed `All tests passed (7 checks).`, exit code 0 |
| Test failure exits non-zero | PASS | A scratch copy of the runner with one deliberately wrong expectation printed `TEST FAILED: Expected 'Hello, WRONG!' but got 'Hello, Java!'.` and exited 1 (project sources were not modified) |
| Package executable JAR | PASS | `jar --create --file dist/ci-pipeline-java-app.jar --main-class cipipelinejavaapp.Main -C out cipipelinejavaapp`, exit code 0 |
| JAR contents | PASS | `jar --list` shows only `META-INF/` and `cipipelinejavaapp/GreetingService.class` + `Main.class` — no test classes |
| Run executable JAR (default) | PASS | `java -jar dist/ci-pipeline-java-app.jar` printed `Hello, CI learner!`, exit code 0 |
| Run executable JAR (argument) | PASS | `java -jar dist/ci-pipeline-java-app.jar "GitHub"` printed `Hello, GitHub!`, exit code 0 |
| Workflow YAML parse | PASS | `.github/workflows/ci.yml` parsed successfully with a YAML parser |

## GitHub Actions validation

| Check | Result | Notes |
|---|---:|---|
| GitHub Actions run | NOT RUN | The workflow is a template inside the project folder; GitHub discovers workflows only at the repository-level `.github/workflows` directory, and it was not executed in GitHub |
| Artifact upload | NOT RUN | Not proven unless the workflow actually runs in GitHub |

## Tools unavailable

- No system-wide JDK was installed on the validation machine; a portable Temurin JDK 21.0.11 was downloaded solely for this validation. Anyone reproducing the results only needs any JDK 21.

## Known limitations

- This is a local Java CI learning project with a deliberately tiny codebase.
- The workflow file is included as a template inside the project folder.
- In a portfolio monorepo, the workflow must be copied to the repository-level `.github/workflows` directory before it can run.
- Artifact upload is not proven unless the workflow actually runs in GitHub.
- `out/`, `test-out/`, and `dist/` are generated locally and not committed.
