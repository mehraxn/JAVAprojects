package mountainhuts;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * {@code Region} is the aggregate/facade of the Mountain Huts system.
 * <p>
 * It defines altitude ranges, stores municipalities and mountain huts (created on
 * demand and de-duplicated by name), imports data from a semicolon-separated CSV
 * file, and answers Stream-API report queries. Returned collections are defensive
 * copies with a deterministic ordering; report maps are ordered by key.
 */
public class Region {

  private final String name;
  private List<AltitudeRange> ranges;
  private final Map<String, Municipality> municipalities;
  private final Map<String, MountainHut> mountainHuts;

  /**
   * Creates a region with the given name.
   *
   * @param name the name of the region (required, non-blank)
   */
  public Region(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Region name must not be null or blank");
    }
    this.name = name.trim();
    this.ranges = new ArrayList<>();
    this.municipalities = new java.util.HashMap<>();
    this.mountainHuts = new java.util.HashMap<>();
  }

  /**
   * @return the name of the region
   */
  public String getName() {
    return name;
  }

  /**
   * Defines the altitude ranges from their textual representation {@code "min-max"}.
   *
   * @param ranges an array of textual ranges
   * @throws IllegalArgumentException if the array or any range is invalid
   */
  public void setAltitudeRanges(String... ranges) {
    if (ranges == null) {
      throw new IllegalArgumentException("Altitude ranges must not be null");
    }
    this.ranges = java.util.Arrays.stream(ranges).map(AltitudeRange::new).toList();
  }

  /**
   * Returns the label {@code "min-max"} of the range containing the given altitude,
   * or the default {@code "0-INF"} if none matches (or the altitude is {@code null}).
   *
   * @param altitude the geographical altitude
   * @return a string representing the range
   */
  public String getAltitudeRange(Integer altitude) {
    if (altitude == null) {
      return "0-INF";
    }
    return ranges.stream()
        .filter(r -> r.contains(altitude))
        .findFirst()
        .map(AltitudeRange::getLabel)
        .orElse("0-INF");
  }

  /**
   * @return an unmodifiable collection of municipalities, sorted by name
   */
  public Collection<Municipality> getMunicipalities() {
    return municipalities.values().stream()
        .sorted(Comparator.comparing(Municipality::getName))
        .collect(Collectors.toUnmodifiableList());
  }

  /**
   * @return an unmodifiable collection of mountain huts, sorted by name
   */
  public Collection<MountainHut> getMountainHuts() {
    return mountainHuts.values().stream()
        .sorted(Comparator.comparing(MountainHut::getName))
        .collect(Collectors.toUnmodifiableList());
  }

  /**
   * Creates a municipality if not already present, otherwise returns the existing
   * one (duplicates are detected by name).
   */
  public Municipality createOrGetMunicipality(String name, String province, Integer altitude) {
    Municipality existing = municipalities.get(name);
    if (existing != null) {
      return existing;
    }
    Municipality created = new Municipality(name, province, altitude);
    municipalities.put(name, created);
    return created;
  }

  /**
   * Creates a mountain hut without an explicit altitude if not already present,
   * otherwise returns the existing one (duplicates are detected by name).
   */
  public MountainHut createOrGetMountainHut(String name, String category, Integer bedsNumber,
      Municipality municipality) {
    return createOrGetMountainHut(name, null, category, bedsNumber, municipality);
  }

  /**
   * Creates a mountain hut if not already present, otherwise returns the existing
   * one (duplicates are detected by name).
   */
  public MountainHut createOrGetMountainHut(String name, Integer altitude, String category,
      Integer bedsNumber, Municipality municipality) {
    MountainHut existing = mountainHuts.get(name);
    if (existing != null) {
      return existing;
    }
    MountainHut created = new MountainHut(name, altitude, category, bedsNumber, municipality);
    mountainHuts.put(name, created);
    return created;
  }

  /**
   * Creates a new region and loads its data from a semicolon-separated CSV file with
   * the header {@code Province;Municipality;MunicipalityAltitude;Name;Altitude;Category;BedsNumber}.
   * The {@code Altitude} (hut altitude) field may be empty.
   *
   * @param name the name of the region
   * @param file the path of the file
   * @return the populated region
   * @throws IllegalArgumentException if the file is missing/unreadable or a row is malformed
   */
  public static Region fromFile(String name, String file) {
    Region region = new Region(name);
    List<String> lines = readData(file);

    // Row 1 is the header; data starts at index 1 (file line number = index + 1).
    for (int i = 1; i < lines.size(); i++) {
      String line = lines.get(i);
      if (line.isBlank()) {
        continue;
      }
      int rowNumber = i + 1;
      String[] fields = line.split(";");
      if (fields.length < 7) {
        throw new IllegalArgumentException(
            "Row " + rowNumber + ": expected 7 fields but found " + fields.length);
      }

      String province = requireField(fields[0], rowNumber, "Province");
      String municipalityName = requireField(fields[1], rowNumber, "Municipality");
      Integer municipalityAltitude = parseInt(fields[2], rowNumber, "MunicipalityAltitude");
      Municipality municipality =
          region.createOrGetMunicipality(municipalityName, province, municipalityAltitude);

      String hutName = requireField(fields[3], rowNumber, "Name");
      String hutAltitudeStr = fields[4].trim();
      String category = requireField(fields[5], rowNumber, "Category");
      Integer bedsNumber = parseInt(fields[6], rowNumber, "BedsNumber");
      Integer hutAltitude = hutAltitudeStr.isEmpty() ? null : parseInt(fields[4], rowNumber, "Altitude");

      region.createOrGetMountainHut(hutName, hutAltitude, category, bedsNumber, municipality);
    }
    return region;
  }

  /**
   * Reads all lines of a UTF-8 text file (including the header line).
   *
   * @param file path of the file
   * @return a list with one element per line
   * @throws IllegalArgumentException if the path is null/blank, the file does not
   *         exist, or an I/O error occurs (the cause is preserved)
   */
  public static List<String> readData(String file) {
    if (file == null || file.isBlank()) {
      throw new IllegalArgumentException("File path must not be null or blank");
    }
    Path path = Path.of(file);
    if (!Files.isRegularFile(path)) {
      throw new IllegalArgumentException("File not found or not readable: " + file);
    }
    try (BufferedReader in = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      return in.lines().toList();
    } catch (IOException e) {
      throw new IllegalArgumentException("Error reading file: " + file, e);
    }
  }

  /**
   * Counts the number of municipalities per province.
   *
   * @return a map (ordered by province) with the province as key and the count as value
   */
  public Map<String, Long> countMunicipalitiesPerProvince() {
    return municipalities.values().stream()
        .collect(Collectors.groupingBy(Municipality::getProvince, TreeMap::new, Collectors.counting()));
  }

  /**
   * Counts the number of mountain huts per municipality within each province.
   *
   * @return a map (ordered by province, then municipality) of province to (municipality to count)
   */
  public Map<String, Map<String, Long>> countMountainHutsPerMunicipalityPerProvince() {
    return mountainHuts.values().stream()
        .collect(Collectors.groupingBy(
            h -> h.getMunicipality().getProvince(),
            TreeMap::new,
            Collectors.groupingBy(
                h -> h.getMunicipality().getName(),
                TreeMap::new,
                Collectors.counting())));
  }

  /**
   * Counts the number of mountain huts per altitude range, using the hut altitude
   * when available or the municipality altitude otherwise.
   *
   * @return a map (ordered by range label) with the range label as key and the count as value
   */
  public Map<String, Long> countMountainHutsPerAltitudeRange() {
    return mountainHuts.values().stream()
        .collect(Collectors.groupingBy(this::altitudeRangeOf, TreeMap::new, Collectors.counting()));
  }

  /**
   * Computes the total number of beds available per province.
   *
   * @return a map (ordered by province) with the province as key and total beds as value
   */
  public Map<String, Integer> totalBedsNumberPerProvince() {
    return mountainHuts.values().stream()
        .collect(Collectors.groupingBy(
            h -> h.getMunicipality().getProvince(),
            TreeMap::new,
            Collectors.summingInt(MountainHut::getBedsNumber)));
  }

  /**
   * Computes the maximum number of beds in a single mountain hut per altitude range,
   * using the hut altitude when available or the municipality altitude otherwise.
   *
   * @return a map (ordered by range label) with the range label as key and the max beds as value
   */
  public Map<String, Optional<Integer>> maximumBedsNumberPerAltitudeRange() {
    return mountainHuts.values().stream()
        .collect(Collectors.groupingBy(
            this::altitudeRangeOf,
            TreeMap::new,
            Collectors.mapping(MountainHut::getBedsNumber, Collectors.maxBy(Integer::compareTo))));
  }

  /**
   * Computes the municipality names grouped by the number of mountain huts they host.
   * Each list of names is in alphabetical order.
   *
   * @return a map (ordered by count) with the hut count as key and the sorted names as value
   */
  public Map<Long, List<String>> municipalityNamesPerCountOfMountainHuts() {
    Map<String, Long> countPerMunicipality = mountainHuts.values().stream()
        .collect(Collectors.groupingBy(h -> h.getMunicipality().getName(), Collectors.counting()));

    Map<Long, List<String>> result = countPerMunicipality.entrySet().stream()
        .collect(Collectors.groupingBy(
            Map.Entry::getValue,
            TreeMap::new,
            Collectors.mapping(Map.Entry::getKey, Collectors.toList())));

    for (List<String> names : result.values()) {
      Collections.sort(names);
    }
    return result;
  }

  /** Altitude range of a hut: its own altitude if present, else the municipality altitude. */
  private String altitudeRangeOf(MountainHut hut) {
    Integer altitude = hut.getAltitude().orElse(hut.getMunicipality().getAltitude());
    return getAltitudeRange(altitude);
  }

  private static String requireField(String value, int rowNumber, String field) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Row " + rowNumber + ": field '" + field + "' must not be blank");
    }
    return value.trim();
  }

  private static Integer parseInt(String value, int rowNumber, String field) {
    try {
      return Integer.parseInt(value.trim());
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          "Row " + rowNumber + ": invalid integer for field '" + field + "': '" + value.trim() + "'", e);
    }
  }
}
