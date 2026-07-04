package authenticationsystem;

import java.security.GeneralSecurityException;

public class PasswordHasher {
    public String generateSalt() {
        // TODO: Generate random salt bytes with SecureRandom and encode them.
        throw new UnsupportedOperationException("TODO: generate a password salt");
    }

    public String hash(char[] password, String encodedSalt)
            throws GeneralSecurityException {
        // TODO: Derive a hash with PBKDF2WithHmacSHA256.
        throw new UnsupportedOperationException("TODO: hash a password");
    }

    public boolean verify(char[] password, String encodedSalt,
            String expectedHash) throws GeneralSecurityException {
        // TODO: Hash the candidate and compare values safely.
        throw new UnsupportedOperationException("TODO: verify a password");
    }
}
