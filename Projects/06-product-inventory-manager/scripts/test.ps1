# Full validation for the Product Inventory Manager (Windows PowerShell).
# Strict-compiles the app and tests, runs the test suite, then every CLI demo.
$ErrorActionPreference = "Stop"

Set-Location (Join-Path $PSScriptRoot "..")

Write-Host "== Cleaning build output =="
Remove-Item -Recurse -Force out, test-out -ErrorAction SilentlyContinue

Write-Host "== Strict compile: application =="
javac -Xlint:all -Werror -d out src/productinventorymanager/*.java
if ($LASTEXITCODE -ne 0) { throw "Application compile failed" }

Write-Host "== Strict compile: tests =="
javac -Xlint:all -Werror -cp out -d test-out tests/productinventorymanager/*.java
if ($LASTEXITCODE -ne 0) { throw "Test compile failed" }

Write-Host "== Running automated tests =="
java -cp "out;test-out" productinventorymanager.TestRunner
if ($LASTEXITCODE -ne 0) { throw "Automated tests failed" }

Write-Host "== CLI demos =="
foreach ($command in @("demo", "stock-demo", "search-demo", "sort-demo", "report-demo", "validation-demo")) {
    Write-Host "--- Main $command ---"
    java -cp out productinventorymanager.Main $command
    if ($LASTEXITCODE -ne 0) { throw "Main $command failed" }
}

Write-Host "== Cleaning build output =="
Remove-Item -Recurse -Force out, test-out -ErrorAction SilentlyContinue

Write-Host "== All checks passed =="
