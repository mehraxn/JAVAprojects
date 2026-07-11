# Full local validation: clean, strict compile, tests, CLI smoke test.
$ErrorActionPreference = "Stop"
Set-Location (Join-Path $PSScriptRoot "..")

Write-Host "[1/5] Cleaning generated folders..."
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue

Write-Host "[2/5] Compiling application with strict flags..."
javac -Xlint:all -Werror -d out src/jobapplicationtracker/*.java
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "[3/5] Compiling tests with strict flags..."
javac -Xlint:all -Werror -cp out -d test-out tests/jobapplicationtracker/*.java
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "[4/5] Running automated tests..."
java -cp "out;test-out" jobapplicationtracker.TestRunner
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "[5/5] Running CLI smoke test (demo)..."
java -cp out jobapplicationtracker.Main demo
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host ""
Write-Host "All validation steps passed."
Write-Host "Note: out/ and test-out/ contain generated classes and are gitignored."
