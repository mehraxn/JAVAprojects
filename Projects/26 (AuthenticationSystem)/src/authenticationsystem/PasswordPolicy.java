package authenticationsystem;

/**
 * Password strength rules, kept deliberately simple for a learning project:
 * 10-128 characters with at least one uppercase letter, one lowercase letter,
 * one digit, and one symbol. Works on char[] so no String copy of the raw
 * password is created here.
 */
public class PasswordPolicy {
    public static final int MINIMUM_LENGTH = 10;
    public static final int MAXIMUM_LENGTH = 128;

    /** Throws IllegalArgumentException when the password breaks a rule. */
    public void validate(char[] password) {
        if (password == null || password.length < MINIMUM_LENGTH) {
            throw new IllegalArgumentException(
                    "Password must contain at least " + MINIMUM_LENGTH + " characters.");
        }
        if (password.length > MAXIMUM_LENGTH) {
            throw new IllegalArgumentException(
                    "Password cannot exceed " + MAXIMUM_LENGTH + " characters.");
        }
        boolean upper = false;
        boolean lower = false;
        boolean digit = false;
        boolean symbol = false;
        boolean nonSpace = false;
        for (char character : password) {
            upper |= Character.isUpperCase(character);
            lower |= Character.isLowerCase(character);
            digit |= Character.isDigit(character);
            symbol |= !Character.isLetterOrDigit(character) && !Character.isWhitespace(character);
            nonSpace |= !Character.isWhitespace(character);
        }
        if (!nonSpace) {
            throw new IllegalArgumentException("Password cannot be only whitespace.");
        }
        if (!(upper && lower && digit && symbol)) {
            throw new IllegalArgumentException(
                    "Password must include uppercase, lowercase, digit, and symbol characters.");
        }
    }

    /** Convenience check that reports the result instead of throwing. */
    public boolean isAcceptable(char[] password) {
        try {
            validate(password);
            return true;
        } catch (IllegalArgumentException rejected) {
            return false;
        }
    }
}
