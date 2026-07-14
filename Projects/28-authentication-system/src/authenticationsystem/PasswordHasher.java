package authenticationsystem;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHasher {
    private static final int SALT_BYTES = 16;
    private static final int ITERATIONS = 120_000;
    private static final int KEY_BITS = 256;
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateSalt() {
        byte[] salt = new byte[SALT_BYTES];
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public String hash(char[] password, String encodedSalt)
            throws GeneralSecurityException {
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        if (encodedSalt == null || encodedSalt.trim().isEmpty()) {
            throw new IllegalArgumentException("Encoded salt cannot be empty.");
        }

        byte[] salt;
        try {
            salt = Base64.getDecoder().decode(encodedSalt);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Encoded salt is not valid Base64.", exception);
        }
        PBEKeySpec specification = new PBEKeySpec(password, salt, ITERATIONS, KEY_BITS);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(specification).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } finally {
            specification.clearPassword();
        }
    }

    public boolean verify(char[] password, String encodedSalt,
            String expectedHash) throws GeneralSecurityException {
        if (expectedHash == null || expectedHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Expected hash cannot be empty.");
        }
        String candidateHash = hash(password, encodedSalt);
        byte[] expected;
        byte[] candidate;
        try {
            expected = Base64.getDecoder().decode(expectedHash);
            candidate = Base64.getDecoder().decode(candidateHash);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Password hash is not valid Base64.", exception);
        }
        return MessageDigest.isEqual(expected, candidate);
    }
}
