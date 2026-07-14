package authenticationsystem;

import static authenticationsystem.TestSupport.assertFalse;
import static authenticationsystem.TestSupport.assertThrows;
import static authenticationsystem.TestSupport.assertTrue;
import static authenticationsystem.TestSupport.test;

final class PasswordPolicyTest {
    private static final PasswordPolicy POLICY = new PasswordPolicy();

    private PasswordPolicyTest() {
    }

    static void run() {
        test("strong demo password is accepted", () -> {
            POLICY.validate("CorrectHorse1!".toCharArray());
            assertTrue(POLICY.isAcceptable("LearnJava10!".toCharArray()), "isAcceptable");
        });

        test("password at exactly the minimum length is accepted", () -> {
            POLICY.validate("Aa1!Aa1!Aa".toCharArray());
        });

        test("too-short password is rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> POLICY.validate("Aa1!short".toCharArray()), "9 characters");
        });

        test("over-long password is rejected", () -> {
            char[] tooLong = new char[129];
            java.util.Arrays.fill(tooLong, 'a');
            tooLong[0] = 'A';
            tooLong[1] = '1';
            tooLong[2] = '!';
            assertThrows(IllegalArgumentException.class,
                    () -> POLICY.validate(tooLong), "129 characters");
        });

        test("missing uppercase is rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> POLICY.validate("learnjava10!".toCharArray()), "no uppercase");
        });

        test("missing lowercase is rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> POLICY.validate("LEARNJAVA10!".toCharArray()), "no lowercase");
        });

        test("missing digit is rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> POLICY.validate("LearnJavaNow!".toCharArray()), "no digit");
        });

        test("missing symbol is rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> POLICY.validate("LearnJava100".toCharArray()), "no symbol");
        });

        test("null and whitespace-only passwords are rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> POLICY.validate(null), "null password");
            assertThrows(IllegalArgumentException.class,
                    () -> POLICY.validate("          ".toCharArray()), "spaces only");
            assertFalse(POLICY.isAcceptable(null), "isAcceptable null");
        });
    }
}
