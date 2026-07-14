# Validates the Restaurant Ordering System project: strict compile, tests, CLI demos.
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
    javac -Xlint:all -Werror -d out (Get-ChildItem src/restaurantorderingsystem/*.java).FullName
}
Invoke-Step "Strict compile: tests" {
    javac -Xlint:all -Werror -cp out -d test-out (Get-ChildItem tests/restaurantorderingsystem/*.java).FullName
}
Invoke-Step "Automated tests" {
    java -cp "out;test-out" restaurantorderingsystem.TestRunner
}
Invoke-Step "CLI demo" {
    java -cp out restaurantorderingsystem.Main demo
}
Invoke-Step "CLI order-demo" {
    java -cp out restaurantorderingsystem.Main order-demo
}
Invoke-Step "CLI discount-demo" {
    java -cp out restaurantorderingsystem.Main discount-demo
}
Invoke-Step "CLI status-demo" {
    java -cp out restaurantorderingsystem.Main status-demo
}
Invoke-Step "CLI validation-demo" {
    java -cp out restaurantorderingsystem.Main validation-demo
}

Write-Host "== Clean up generated files =="
Remove-Item -Recurse -Force out, test-out -ErrorAction SilentlyContinue

Write-Host "ALL CHECKS PASSED"
