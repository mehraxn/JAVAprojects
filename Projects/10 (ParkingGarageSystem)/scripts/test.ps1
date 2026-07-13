# Full validation for the Parking Garage System (Windows PowerShell).
# Strict-compiles the app and tests, runs the test suite, then every CLI demo.
$ErrorActionPreference = "Stop"

Set-Location (Join-Path $PSScriptRoot "..")

Write-Host "== Cleaning build output =="
Remove-Item -Recurse -Force out, test-out -ErrorAction SilentlyContinue

Write-Host "== Strict compile: application =="
javac -Xlint:all -Werror -d out src/parkinggaragesystem/*.java
if ($LASTEXITCODE -ne 0) { throw "Application compile failed" }

Write-Host "== Strict compile: tests =="
javac -Xlint:all -Werror -cp out -d test-out tests/parkinggaragesystem/*.java
if ($LASTEXITCODE -ne 0) { throw "Test compile failed" }

Write-Host "== Running automated tests =="
java -cp "out;test-out" parkinggaragesystem.TestRunner
if ($LASTEXITCODE -ne 0) { throw "Automated tests failed" }

Write-Host "== CLI demos =="
foreach ($command in @("demo", "parking-demo", "exit-demo", "fee-demo", "full-garage-demo", "report-demo", "validation-demo")) {
    Write-Host "--- Main $command ---"
    java -cp out parkinggaragesystem.Main $command
    if ($LASTEXITCODE -ne 0) { throw "Main $command failed" }
}

Write-Host "== Cleaning build output =="
Remove-Item -Recurse -Force out, test-out -ErrorAction SilentlyContinue

Write-Host "== All checks passed =="
