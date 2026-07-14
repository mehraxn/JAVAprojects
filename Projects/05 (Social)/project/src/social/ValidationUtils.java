package social;

/**
 * Small validation helpers used by the facade. They throw
 * {@link IllegalArgumentException} for invalid input (null/blank fields, invalid
 * pagination), keeping input-validation behavior consistent across the API.
 */
public final class ValidationUtils {

  private ValidationUtils() {
    // utility class
  }

  public static String requireNotBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be null or blank");
    }
    return value;
  }

  /** Page numbers are 1-based. */
  public static int requirePositivePage(int value, String fieldName) {
    if (value < 1) {
      throw new IllegalArgumentException(fieldName + " must be >= 1");
    }
    return value;
  }

  public static int requirePositive(int value, String fieldName) {
    if (value <= 0) {
      throw new IllegalArgumentException(fieldName + " must be > 0");
    }
    return value;
  }
}
