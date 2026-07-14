# Full local validation: clean, strict compile, tests, CLI demos.
$ErrorActionPreference = "Stop"
Set-Location (Join-Path $PSScriptRoot "..")

Write-Host "[1/6] Cleaning generated folders..."
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue

Write-Host "[2/6] Compiling application with strict flags..."
javac -Xlint:all -Werror -d out src/authenticationsystem/*.java
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "[3/6] Compiling tests with strict flags..."
javac -Xlint:all -Werror -cp out -d test-out tests/authenticationsystem/*.java
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "[4/6] Running automated tests (PBKDF2 makes this take a few seconds)..."
java -cp "out;test-out" authenticationsystem.TestRunner
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "[5/6] Running CLI demo..."
java -cp out authenticationsystem.Main demo
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "[6/6] Running authorization and expiry demos..."
java -cp out authenticationsystem.Main authorization-demo
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
java -cp out authenticationsystem.Main expiry-demo
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host ""
Write-Host "All validation steps passed."
Write-Host "Note: out/ and test-out/ contain generated classes and are gitignored."
