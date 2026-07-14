$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$rootDir = Split-Path -Parent $scriptDir
Set-Location $rootDir

if (Test-Path ".\mvnw.cmd") {
    .\mvnw.cmd clean test
} else {
    mvn clean test
}
