package mountainhuts;

import java.util.Optional;

/**
 * An immutable mountain hut: a name, an optional altitude, a category, a number of
 * beds and the municipality it belongs to.
 * <p>
 * The altitude is optional: when absent, altitude-range classification falls back to
 * the municipality altitude (see {@link Region}).
 */
public final class MountainHut {

  private final String name;
  private final Integer altitude;
  private final String category;
  private final Integer bedsNumber;
  private final Municipality municipality;

  /**
   * @param name         hut name (required, non-blank, trimmed)
   * @param altitude     hut altitude in metres; may be {@code null}, but if present must not be negative
   * @param category     hut category (required, non-blank, trimmed)
   * @param bedsNumber   number of beds (required, non-negative)
   * @param municipality owning municipality (required)
   * @throws IllegalArgumentException if a required field is invalid
   */
  public MountainHut(String name, Integer altitude, String category, Integer bedsNumber,
      Municipality municipality) {
    this.name = requireNonBlank(name, "name");
    this.category = requireNonBlank(category, "category");
    if (altitude != null && altitude < 0) {
      throw new IllegalArgumentException("Mountain hut altitude must not be negative");
    }
    if (bedsNumber == null || bedsNumber < 0) {
      throw new IllegalArgumentException("Mountain hut beds number must not be null or negative");
    }
    if (municipality == null) {
      throw new IllegalArgumentException("Mountain hut municipality must not be null");
    }
    this.altitude = altitude;
    this.bedsNumber = bedsNumber;
    this.municipality = municipality;
  }

  public String getName() {
    return name;
  }

  /** @return the hut altitude if present, otherwise an empty optional */
  public Optional<Integer> getAltitude() {
    return Optional.ofNullable(altitude);
  }

  public String getCategory() {
    return category;
  }

  public Integer getBedsNumber() {
    return bedsNumber;
  }

  public Municipality getMunicipality() {
    return municipality;
  }

  private static String requireNonBlank(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Mountain hut " + field + " must not be null or blank");
    }
    return value.trim();
  }

  @Override
  public String toString() {
    return name + " [" + category + "]";
  }
}
