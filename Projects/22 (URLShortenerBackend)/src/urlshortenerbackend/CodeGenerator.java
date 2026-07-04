package urlshortenerbackend;

import java.util.Set;

public class CodeGenerator {
    private static final char[] CHARACTERS =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private long sequence = 1;

    public synchronized String generate(Set<String> existingCodes) {
        if (existingCodes == null) {
            throw new IllegalArgumentException("Existing-code set cannot be null.");
        }
        while (sequence > 0) {
            String candidate = pad(encode(sequence));
            sequence++;
            if (!existingCodes.contains(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("No more short codes can be generated.");
    }

    private String encode(long value) {
        StringBuilder result = new StringBuilder();
        do {
            result.append(CHARACTERS[(int) (value % CHARACTERS.length)]);
            value /= CHARACTERS.length;
        } while (value > 0);
        return result.reverse().toString();
    }

    private String pad(String value) {
        StringBuilder result = new StringBuilder(value);
        while (result.length() < 6) {
            result.insert(0, '0');
        }
        return result.toString();
    }
}
