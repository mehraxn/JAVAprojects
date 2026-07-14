# Full local validation: clean, strict compile, tests, CLI demos.
$ErrorActionPreference = "Stop"
Set-Location (Join-Path $PSScriptRoot "..")

Write-Host "[1/6] Cleaning generated folders..."
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue

Write-Host "[2/6] Compiling application with strict flags..."
javac -Xlint:all -Werror -d out src/notificationservice/*.java
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "[3/6] Compiling tests with strict flags..."
javac -Xlint:all -Werror -cp out -d test-out tests/notificationservice/*.java
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "[4/6] Running automated tests..."
java -cp "out;test-out" notificationservice.TestRunner
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "[5/6] Running CLI demo..."
java -cp out notificationservice.Main demo
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "[6/6] Running retry and validation demos..."
java -cp out notificationservice.Main retry-demo
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
java -cp out notificationservice.Main validation-demo
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host ""
Write-Host "All validation steps passed."
Write-Host "Note: out/ and test-out/ contain generated classes and are gitignored."
