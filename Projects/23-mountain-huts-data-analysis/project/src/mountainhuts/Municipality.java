package mountainhuts;

/**
 * An immutable municipality that can host mountain huts, described by its name,
 * province and altitude.
 */
public final class Municipality {

  private final String name;
  private final String province;
  private final Integer altitude;

  /**
   * @param name     municipality name (required, non-blank, trimmed)
   * @param province province name (required, non-blank, trimmed)
   * @param altitude altitude in metres; if present it must not be negative
   * @throws IllegalArgumentException if a required field is null/blank or the altitude is negative
   */
  public Municipality(String name, String province, Integer altitude) {
    this.name = requireNonBlank(name, "name");
    this.province = requireNonBlank(province, "province");
    if (altitude != null && altitude < 0) {
      throw new IllegalArgumentException("Municipality altitude must not be negative");
    }
    this.altitude = altitude;
  }

  public String getName() {
    return name;
  }

  public String getProvince() {
    return province;
  }

  public Integer getAltitude() {
    return altitude;
  }

  private static String requireNonBlank(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Municipality " + field + " must not be null or blank");
    }
    return value.trim();
  }

  @Override
  public String toString() {
    return name + " (" + province + ")";
  }
}
