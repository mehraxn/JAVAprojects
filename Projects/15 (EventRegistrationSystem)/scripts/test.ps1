$ErrorActionPreference = "Stop"
try {
    Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
    javac -Xlint:all -Werror -d out src/eventregistrationsystem/*.java
    javac -Xlint:all -Werror -cp out -d test-out tests/eventregistrationsystem/*.java
    java -cp "out;test-out" eventregistrationsystem.TestRunner
    java -cp out eventregistrationsystem.Main demo
    java -cp out eventregistrationsystem.Main registration-demo
    java -cp out eventregistrationsystem.Main capacity-demo
    java -cp out eventregistrationsystem.Main cancellation-demo
    java -cp out eventregistrationsystem.Main search-demo
    java -cp out eventregistrationsystem.Main validation-demo
} finally {
    Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
}
