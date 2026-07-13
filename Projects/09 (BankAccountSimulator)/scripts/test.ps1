# Full validation for the Bank Account Simulator (Windows PowerShell).
# Strict-compiles the app and tests, runs the test suite, then every CLI demo.
$ErrorActionPreference = "Stop"

Set-Location (Join-Path $PSScriptRoot "..")

Write-Host "== Cleaning build output =="
Remove-Item -Recurse -Force out, test-out -ErrorAction SilentlyContinue

Write-Host "== Strict compile: application =="
javac -Xlint:all -Werror -d out src/bankaccountsimulator/*.java
if ($LASTEXITCODE -ne 0) { throw "Application compile failed" }

Write-Host "== Strict compile: tests =="
javac -Xlint:all -Werror -cp out -d test-out tests/bankaccountsimulator/*.java
if ($LASTEXITCODE -ne 0) { throw "Test compile failed" }

Write-Host "== Running automated tests =="
java -cp "out;test-out" bankaccountsimulator.TestRunner
if ($LASTEXITCODE -ne 0) { throw "Automated tests failed" }

Write-Host "== CLI demos =="
foreach ($command in @("demo", "deposit-demo", "withdraw-demo", "transfer-demo", "statement-demo", "validation-demo")) {
    Write-Host "--- Main $command ---"
    java -cp out bankaccountsimulator.Main $command
    if ($LASTEXITCODE -ne 0) { throw "Main $command failed" }
}

Write-Host "== Cleaning build output =="
Remove-Item -Recurse -Force out, test-out -ErrorAction SilentlyContinue

Write-Host "== All checks passed =="
