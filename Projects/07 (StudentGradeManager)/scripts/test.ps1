$ErrorActionPreference = "Stop"

Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue

try {
    javac -Xlint:all -Werror -d out src/studentgrademanager/*.java
    javac -Xlint:all -Werror -cp out -d test-out tests/studentgrademanager/*.java

    java -cp "out;test-out" studentgrademanager.TestRunner
    java -cp out studentgrademanager.Main demo
    java -cp out studentgrademanager.Main grade-demo
    java -cp out studentgrademanager.Main report-demo
    java -cp out studentgrademanager.Main ranking-demo
    java -cp out studentgrademanager.Main search-demo
    java -cp out studentgrademanager.Main validation-demo
} finally {
    Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
}
