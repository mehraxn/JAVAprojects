# Testing the CI Pipeline Java App

This guide lists the exact commands to run every pipeline stage locally from the project root. The results of actually running them are recorded in `TEST_RESULTS.md`.

Prerequisite: a JDK 21 (`java -version`, `javac -version`). No build tool or external dependency is needed.

## A) Clean previous generated files

Linux/macOS/Git Bash:

```bash
rm -rf out test-out dist
```

Windows PowerShell:

```powershell
Remove-Item -Recurse -Force out,test-out,dist -ErrorAction SilentlyContinue
```

## B) Compile application source

```bash
javac -d out src/cipipelinejavaapp/*.java
```

## C) Compile tests

```bash
javac -cp out -d test-out test/cipipelinejavaapp/*.java
```

Tests compile into `test-out`, separate from `out`, so they can never leak into the application JAR.

## D) Run tests

Linux/macOS/Git Bash (classpath separator `:`):

```bash
java -cp "out:test-out" cipipelinejavaapp.GreetingServiceTest
```

Windows PowerShell/Command Prompt (classpath separator `;`):

```powershell
java -cp "out;test-out" cipipelinejavaapp.GreetingServiceTest
```

Expected on success: `All tests passed (7 checks).` and exit code 0. On a failed check the runner prints `TEST FAILED: ...` and exits 1, which stops a CI pipeline.

## E) Package executable JAR

Linux/macOS/Git Bash:

```bash
mkdir -p dist
jar --create --file dist/ci-pipeline-java-app.jar --main-class cipipelinejavaapp.Main -C out cipipelinejavaapp
```

Windows PowerShell:

```powershell
New-Item -ItemType Directory -Force dist | Out-Null
jar --create --file dist/ci-pipeline-java-app.jar --main-class cipipelinejavaapp.Main -C out cipipelinejavaapp
```

Only the application package from `out` is included — no test classes.

## F) Run the executable JAR

```bash
java -jar dist/ci-pipeline-java-app.jar
java -jar dist/ci-pipeline-java-app.jar "GitHub"
```

Expected: `Hello, CI learner!` for the first command, `Hello, GitHub!` for the second, both with exit code 0.

## G) GitHub Actions note

The workflow file is included under `.github/workflows/ci.yml` inside this project folder, where it is a template only — GitHub discovers workflows exclusively in the repository-level `.github/workflows` directory. To activate it in a portfolio monorepo, copy it to the repository-level `.github/workflows/ci.yml` and adjust the `working-directory` and artifact `path` values to match the actual repo layout (in this repository: `Projects/31 (CIPipelineJavaApp)`).

## H) Cleanup

Linux/macOS/Git Bash:

```bash
rm -rf out test-out dist
```

Windows PowerShell:

```powershell
Remove-Item -Recurse -Force out,test-out,dist -ErrorAction SilentlyContinue
```

Generated files (`out/`, `test-out/`, `dist/`, `*.class`, `*.jar`) are gitignored and must not be committed.
