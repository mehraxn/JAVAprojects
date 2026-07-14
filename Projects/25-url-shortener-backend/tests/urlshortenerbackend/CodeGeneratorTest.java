package urlshortenerbackend;

import java.util.HashSet;
import java.util.Set;

public final class CodeGeneratorTest {
    private CodeGeneratorTest() {
    }

    static void run(Assert t) {
        CodeGenerator generator = new CodeGenerator();

        t.assertThrows(IllegalArgumentException.class,
                () -> generator.generate(null), "null existing-code set rejected");

        Set<String> existing = new HashSet<String>();
        String first = generator.generate(existing);
        t.assertNotNull(first, "generated code is not null");
        t.assertFalse(first.isEmpty(), "generated code is non-empty");
        t.assertEquals(6, first.length(), "generated code is padded to 6 characters");
        t.assertTrue(first.matches("[0-9a-zA-Z]{6}"),
                "generated code uses only Base62 characters");

        // Codes are unique across a sample and always valid short codes
        Set<String> seen = new HashSet<String>();
        CodeGenerator fresh = new CodeGenerator();
        boolean allValid = true;
        for (int i = 0; i < 500; i++) {
            String code = fresh.generate(seen);
            if (!code.matches("[0-9a-zA-Z]{6}")) {
                allValid = false;
            }
            seen.add(code);
        }
        t.assertEquals(500, seen.size(), "500 generated codes are all distinct");
        t.assertTrue(allValid, "all sampled codes use only Base62 characters");

        // The generator skips codes that already exist
        CodeGenerator skipping = new CodeGenerator();
        Set<String> taken = new HashSet<String>();
        taken.add(skipping.generate(new HashSet<String>()));
        CodeGenerator second = new CodeGenerator();
        String skipped = second.generate(taken);
        t.assertFalse(taken.contains(skipped),
                "generator does not return a code that already exists");

        // Deterministic sequence: two fresh generators produce the same first code
        t.assertEquals(new CodeGenerator().generate(new HashSet<String>()),
                new CodeGenerator().generate(new HashSet<String>()),
                "sequential generator is deterministic for a fresh instance");

        // Generated codes pass the short-code validation used by the service
        t.assertEquals(first, UrlEntry.validateShortCode(first),
                "generated codes are valid short codes");
    }
}
