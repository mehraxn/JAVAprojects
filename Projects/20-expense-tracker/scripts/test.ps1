# Validates the Expense Tracker project: strict compile, automated tests, CLI demos.
# Run from the project root: .\scripts\test.ps1
$ErrorActionPreference = "Stop"

Set-Location (Join-Path $PSScriptRoot "..")

function Invoke-Step {
    param([string]$Name, [scriptblock]$Body)
    Write-Host "== $Name =="
    & $Body
    if ($LASTEXITCODE -ne 0) {
        Write-Error "$Name failed with exit code $LASTEXITCODE"
        exit 1
    }
}

Write-Host "== Clean =="
Remove-Item -Recurse -Force out, test-out -ErrorAction SilentlyContinue

Invoke-Step "Strict compile: application" {
    javac -Xlint:all -Werror -d out (Get-ChildItem src/expensetracker/*.java).FullName
}
Invoke-Step "Strict compile: tests" {
    javac -Xlint:all -Werror -cp out -d test-out (Get-ChildItem tests/expensetracker/*.java).FullName
}
Invoke-Step "Automated tests" {
    java -cp "out;test-out" expensetracker.TestRunner
}
Invoke-Step "CLI demo" {
    java -cp out expensetracker.Main demo
}
Invoke-Step "CLI csv-demo" {
    java -cp out expensetracker.Main csv-demo
}
Invoke-Step "CLI report-demo" {
    java -cp out expensetracker.Main report-demo
}
Invoke-Step "CLI validation-demo" {
    java -cp out expensetracker.Main validation-demo
}

Write-Host "== Clean up generated files =="
Remove-Item -Recurse -Force out, test-out -ErrorAction SilentlyContinue

Write-Host "ALL CHECKS PASSED"
