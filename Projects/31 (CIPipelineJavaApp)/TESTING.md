# Testing CI Pipeline Java App

No Java command, build command, test process, packaging process, or CI workflow was executed while preparing this project.

## Static validation checklist

- [ ] Confirm `GreetingService` rejects null, blank, and oversized names.
- [ ] Confirm normal names are trimmed and formatted correctly.
- [ ] Confirm test assertions fail with a non-zero process result.
- [ ] Confirm workflow stages appear in checkout, setup, compile, test, and package order.
- [ ] Confirm packaging reads only from the application output directory.

## File existence checks

- [ ] `src/cipipelinejavaapp/GreetingService.java` exists.
- [ ] `src/cipipelinejavaapp/Main.java` exists.
- [ ] `test/cipipelinejavaapp/GreetingServiceTest.java` exists.
- [ ] `.github/workflows/ci.yml` exists.
- [ ] `docs/PIPELINE.md`, `README.md`, and `TESTING.md` exist.

## Configuration review checklist

- [ ] Workflow working directories point to project 31.
- [ ] Java version and distribution are consistent across steps.
- [ ] Application and tests use separate output directories.
- [ ] A test failure prevents packaging.
- [ ] Artifact upload requires the expected JAR.
- [ ] Manual-only triggering and nested placement are documented.

## Security checks

- [ ] No real secret or credential is present.
- [ ] No production endpoint is present.
- [ ] Workflow permissions are read-only unless a reviewed step needs more.
- [ ] No untrusted script or downloaded executable is invoked.

## Commands normally used - NOT executed

```text
javac -d out src/cipipelinejavaapp/*.java
javac -cp out -d test-out test/cipipelinejavaapp/*.java
java -cp "out;test-out" cipipelinejavaapp.GreetingServiceTest
jar --create --file dist/ci-pipeline-java-app.jar --main-class cipipelinejavaapp.Main -C out cipipelinejavaapp
```

GitHub Actions would normally run equivalent Linux commands after the workflow is moved to repository scope. None were run here.

## Expected results in a proper environment

- Application and test classes compile cleanly.
- All prepared greeting checks pass.
- Invalid behavior causes the test stage and workflow to fail.
- An executable application-only JAR is created after successful tests.
- CI uploads the expected artifact only after every prior stage succeeds.
