package it.polito.oop.test;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

public class TestBranchUtils {
    private static String currentBranch;
    public static String currentBranch(){
        if(currentBranch==null){
            currentBranch = System.getenv("CI_COMMIT_REF_NAME");
            if(currentBranch==null){ // not in CI!
                try {
                    String head = Files.readString(Path.of(".git/HEAD"));
                    if( head.startsWith("ref:")){
                        // branch checked-out
                        currentBranch = head.substring(head.lastIndexOf("/") + 1).trim();
                    }else{ 
                        currentBranch = "main"; // in case of detached HEAD behave like main branch
                    }
                } catch (IOException _) {
                    // in case branch cannot be determined, assume `main`
                    currentBranch = "main";
                }
            }
        }
        return currentBranch;
    }

    private static final Pattern reqRE = Pattern.compile("^r(\\d+)-|-r(\\d+)-|-r(\\d+)$");

    
    /// Checks whether the tests for the specific requirement number
    /// are meaningful for the current branch.
    ///
    /// Specifically tests for requirement Ri are meaningful for
    /// a branch where requirement Rj is developed if i <= j.
    /// That is, it is reasonable to run tests for the requirement
    /// being developed in the branch and any previous requirement.
    /// 
    /// If that is the case or the current branch name is `main`or
    /// it does not mention any requirements (i.e. no `r#` in the name)
    /// the execution proceeds; otherwise the assumption is not
    /// satisfied and the test that invoked this method will be
    /// skipped.
    /// 
    /// The association between branch name and requirement being developed
    /// is performed by matching the string `r#` that refer to requirement.
    /// For instance in GitLab when a branch is generated from and issue
    /// with title `"R1 Arithmentic operations"` the default name
    /// is `1-r1-arithmetic-operations`, therefore using the statement
    /// `assumeRequirementAtLeast(1)` in a test will execute them only in
    /// the matching branches or in the `main` branch.
    /// 
    /// @param reqNo the requirement number of the tests be considered
    /// 
    public static void assumeRequirementAtLeast(int reqNo){
        int[] reqs = 
        reqRE.matcher(currentBranch()).results().
                    flatMap(mr -> {
                                String[] matches = new String[mr.groupCount()];
                                Arrays.setAll(matches, i -> mr.group(i+1));
                                return Arrays.stream(matches);
                            }).
                    filter(Objects::nonNull).
                    mapToInt(Integer::parseInt).
                    toArray();

        assumeTrue(currentBranch().equals("main") ||
                   reqs.length == 0 ||
                   Arrays.stream(reqs).anyMatch(r -> r >= reqNo),
                   "Skipping since tests for requirement R%d are not compatible with branch %s".formatted(reqNo, currentBranch())
                  );
    }
}

