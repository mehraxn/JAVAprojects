package mountainhuts;

/**
 * An immutable altitude range described by a label of the form {@code "min-max"}
 * (e.g. {@code "1000-2000"}).
 * <p>
 * Membership is <b>left-open, right-closed</b>: an altitude {@code a} belongs to the
 * range when {@code min < a <= max}. This matches the Mountain Huts specification,
 * where e.g. {@code 2000} belongs to {@code "1000-2000"} but not to {@code "2000-3000"}.
 */
public final class AltitudeRange {

  private final int min;
  private final int max;
  private final String label;

  /**
   * Builds a range from its textual label {@code "min-max"}.
   *
   * @param label the range label
   * @throws IllegalArgumentException if the label is null/blank, malformed, has
   *         negative bounds, or has {@code min > max}
   */
  public AltitudeRange(String label) {
    if (label == null || label.isBlank()) {
      throw new IllegalArgumentException("Altitude range must not be null or blank");
    }
    String trimmed = label.trim();
    String[] parts = trimmed.split("-");
    if (parts.length != 2) {
      throw new IllegalArgumentException(
          "Invalid altitude range '" + label + "', expected format 'min-max'");
    }
    int lower;
    int upper;
    try {
      lower = Integer.parseInt(parts[0].trim());
      upper = Integer.parseInt(parts[1].trim());
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          "Invalid altitude range '" + label + "', bounds must be integers", e);
    }
    if (lower < 0 || upper < 0) {
      throw new IllegalArgumentException("Altitude range bounds must not be negative: '" + label + "'");
    }
    if (lower > upper) {
      throw new IllegalArgumentException(
          "Altitude range lower bound must be <= upper bound: '" + label + "'");
    }
    this.min = lower;
    this.max = upper;
    this.label = trimmed;
  }

  /**
   * @param altitude the altitude to test
   * @return {@code true} if {@code min < altitude <= max}
   */
  public boolean contains(int altitude) {
    return altitude > min && altitude <= max;
  }

  public int getMin() {
    return min;
  }

  public int getMax() {
    return max;
  }

  /** @return the textual label {@code "min-max"} */
  public String getLabel() {
    return label;
  }

  @Override
  public String toString() {
    return label;
  }
}
