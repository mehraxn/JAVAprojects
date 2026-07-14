# Full validation for the Train Ticket Reservation System (Windows PowerShell).
# Strict-compiles the app and tests, runs the test suite, then every CLI demo.
$ErrorActionPreference = "Stop"

Set-Location (Join-Path $PSScriptRoot "..")

Write-Host "== Cleaning build output =="
Remove-Item -Recurse -Force out, test-out -ErrorAction SilentlyContinue

Write-Host "== Strict compile: application =="
javac -Xlint:all -Werror -d out src/trainticketreservationsystem/*.java
if ($LASTEXITCODE -ne 0) { throw "Application compile failed" }

Write-Host "== Strict compile: tests =="
javac -Xlint:all -Werror -cp out -d test-out tests/trainticketreservationsystem/*.java
if ($LASTEXITCODE -ne 0) { throw "Test compile failed" }

Write-Host "== Running automated tests =="
java -cp "out;test-out" trainticketreservationsystem.TestRunner
if ($LASTEXITCODE -ne 0) { throw "Automated tests failed" }

Write-Host "== CLI demos =="
foreach ($command in @("demo", "reservation-demo", "cancellation-demo", "search-demo", "full-train-demo", "validation-demo")) {
    Write-Host "--- Main $command ---"
    java -cp out trainticketreservationsystem.Main $command
    if ($LASTEXITCODE -ne 0) { throw "Main $command failed" }
}

Write-Host "== Cleaning build output =="
Remove-Item -Recurse -Force out, test-out -ErrorAction SilentlyContinue

Write-Host "== All checks passed =="
