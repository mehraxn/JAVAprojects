package mountainhuts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors; // ADDED FOR R4

/**
 * Class {@code Region} represents the main facade
 * class for the mountains hut system.
 * * It allows defining and retrieving information about
 * municipalities and mountain huts.
 *
 */
public class Region {

	// ADDED FOR R1
	private final String name;
	// ADDED FOR R1
	private List<AltitudeRange> ranges;
	// ADDED FOR R2
	private final Map<String, Municipality> municipalities;
	// ADDED FOR R2
	private final Map<String, MountainHut> mountainHuts;

	/**
	 * Create a region with the given name.
	 * * @param name
	 * the name of the region
	 */
	public Region(String name) {
		this.name = name; // ADDED FOR R1
		this.ranges = new ArrayList<>(); // ADDED FOR R1
		this.municipalities = new HashMap<>(); // ADDED FOR R2
		this.mountainHuts = new HashMap<>(); // ADDED FOR R2
	}

	/**
	 * Return the name of the region.
	 * * @return the name of the region
	 */
	public String getName() {
		return name; // ADDED FOR R1
	}

	/**
	 * Create the ranges given their textual representation in the format
	 * "[minValue]-[maxValue]".
	 * * @param ranges
	 * an array of textual ranges
	 */
	public void setAltitudeRanges(String... ranges) {
		// ADDED FOR R1
		this.ranges = Arrays.stream(ranges)
				.map(AltitudeRange::new)
				.toList();
	}

	/**
	 * Return the textual representation in the format "[minValue]-[maxValue]" of
	 * the range including the given altitude or return the default range "0-INF".
	 * * @param altitude
	 * the geographical altitude
	 * @return a string representing the range
	 */
	public String getAltitudeRange(Integer altitude) {
		// ADDED FOR R1
		if (altitude == null) return "0-INF";
		
		// ADDED FOR R1
		return ranges.stream()
				.filter(r -> r.contains(altitude))
				.findFirst()
				.map(AltitudeRange::getLabel)
				.orElse("0-INF");
	}

	/**
	 * Return all the municipalities available.
	 * * The returned collection is unmodifiable
	 * * @return a collection of municipalities
	 */
	public Collection<Municipality> getMunicipalities() {
		return Collections.unmodifiableCollection(municipalities.values()); // ADDED FOR R2
	}

	/**
	 * Return all the mountain huts available.
	 * * The returned collection is unmodifiable
	 * * @return a collection of mountain huts
	 */
	public Collection<MountainHut> getMountainHuts() {
		return Collections.unmodifiableCollection(mountainHuts.values()); // ADDED FOR R2
	}

	/**
	 * Create a new municipality if it is not already available or find it.
	 * Duplicates must be detected by comparing the municipality names.
	 * * @param name
	 * the municipality name
	 * @param province
	 * the municipality province
	 * @param altitude
	 * the municipality altitude
	 * @return the municipality
	 */
	public Municipality createOrGetMunicipality(String name, String province, Integer altitude) {
		// ADDED FOR R2
		if (municipalities.containsKey(name)) {
			return municipalities.get(name);
		}
		
		Municipality newMunicipality = new Municipality(name, province, altitude);
		municipalities.put(name, newMunicipality);
		return newMunicipality;
	}

	/**
	 * Create a new mountain hut if it is not already available or find it.
	 * Duplicates must be detected by comparing the mountain hut names.
	 *
	 * @param name
	 * the mountain hut name
	 * @param category
	 * the mountain hut category
	 * @param bedsNumber
	 * the number of beds in the mountain hut
	 * @param municipality
	 * the municipality in which the mountain hut is located
	 * @return the mountain hut
	 */
	public MountainHut createOrGetMountainHut(String name, String category, 
											  Integer bedsNumber, Municipality municipality) {
		// ADDED FOR R2
		return createOrGetMountainHut(name, null, category, bedsNumber, municipality);
	}

	/**
	 * Create a new mountain hut if it is not already available or find it.
	 * Duplicates must be detected by comparing the mountain hut names.
	 * * @param name
	 * the mountain hut name
	 * @param altitude
	 * the mountain hut altitude
	 * @param category
	 * the mountain hut category
	 * @param bedsNumber
	 * the number of beds in the mountain hut
	 * @param municipality
	 * the municipality in which the mountain hut is located
	 * @return a mountain hut
	 */
	public MountainHut createOrGetMountainHut(String name, Integer altitude, String category, 
											  Integer bedsNumber, Municipality municipality) {
		// ADDED FOR R2
		if (mountainHuts.containsKey(name)) {
			return mountainHuts.get(name);
		}
		
		MountainHut newHut = new MountainHut(name, altitude, category, bedsNumber, municipality);
		mountainHuts.put(name, newHut);
		return newHut;
	}

	/**
	 * Creates a new region and loads its data from a file.
	 * * The file must be a CSV file and it must contain the following fields:
	 * <ul>
	 * <li>{@code "Province"},
	 * <li>{@code "Municipality"},
	 * <li>{@code "MunicipalityAltitude"},
	 * <li>{@code "Name"},
	 * <li>{@code "Altitude"},
	 * <li>{@code "Category"},
	 * <li>{@code "BedsNumber"}
	 * </ul>
	 * * The fields are separated by a semicolon (';'). The field {@code "Altitude"}
	 * may be empty.
	 * * @param name
	 * the name of the region
	 * @param file
	 * the path of the file
	 */
	public static Region fromFile(String name, String file) {
		// TASK R3
		Region region = new Region(name);
		// TASK R3
		List<String> lines = readData(file);

		// TASK R3
		// We iterate starting from index 1 to skip the header row
		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);
			String[] fields = line.split(";");

			String province = fields[0];
			String municipalityName = fields[1];
			Integer municipalityAltitude = Integer.parseInt(fields[2]);

			Municipality municipality = region.createOrGetMunicipality(municipalityName, province, municipalityAltitude);

			String hutName = fields[3];
			String hutAltitudeStr = fields[4];
			String category = fields[5];
			Integer bedsNumber = Integer.parseInt(fields[6]);

			Integer hutAltitude = null;
			if (!hutAltitudeStr.isEmpty()) {
				hutAltitude = Integer.parseInt(hutAltitudeStr);
			}

			region.createOrGetMountainHut(hutName, hutAltitude, category, bedsNumber, municipality);
		}

		// TASK R3
		return region;
	}

	/**
	 * Reads the lines of a text file.
	 *
	 * @param file path of the file
	 * @return a list with one element per line
	 */
	public static List<String> readData(String file) {
		try (BufferedReader in = new BufferedReader(new FileReader(file))) {
			return in.lines().toList();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return new ArrayList<>();
		}
	}

	/**
	 * Count the number of municipalities with at least a mountain hut per each
	 * province.
	 * * @return a map with the province as key and the number of municipalities as
	 * value
	 */
	public Map<String, Long> countMunicipalitiesPerProvince() {
		// TASK R4
		return municipalities.values().stream()
				.collect(Collectors.groupingBy(
						m -> m.getProvince(),
						Collectors.counting()
				));
	}

	/**
	 * Count the number of mountain huts per each municipality within each province.
	 * * @return a map with the province as key and, as value, a map with the
	 * municipality as key and the number of mountain huts as value
	 */
	public Map<String, Map<String, Long>> countMountainHutsPerMunicipalityPerProvince() {
		// TASK R4
		return mountainHuts.values().stream()
				.collect(Collectors.groupingBy(
						h -> h.getMunicipality().getProvince(),
						Collectors.groupingBy(
								h -> h.getMunicipality().getName(),
								Collectors.counting()
						)
				));
	}

	/**
	 * Count the number of mountain huts per altitude range. If the altitude of the
	 * mountain hut is not available, use the altitude of its municipality.
	 * * @return a map with the altitude range as key and the number of mountain huts
	 * as value
	 */
	public Map<String, Long> countMountainHutsPerAltitudeRange() {
		// TASK R4
		return mountainHuts.values().stream()
				.collect(Collectors.groupingBy(
						h -> getHutAltitudeRange(h), // Use helper method defined below
						Collectors.counting()
				));
	}

	/**
	 * Compute the total number of beds available in the mountain huts per each
	 * province.
	 * * @return a map with the province as key and the total number of beds as value
	 */
	public Map<String, Integer> totalBedsNumberPerProvince() {
		// TASK R4
		return mountainHuts.values().stream()
				.collect(Collectors.groupingBy(
						h -> h.getMunicipality().getProvince(),
						Collectors.summingInt(h -> h.getBedsNumber())
				));
	}

	/**
	 * Compute the maximum number of beds available in a single mountain hut per
	 * altitude range. If the altitude of the mountain hut is not available, use the
	 * altitude of its municipality.
	 * * @return a map with the altitude range as key and the maximum number of beds
	 * as value
	 */
	public Map<String, Optional<Integer>> maximumBedsNumberPerAltitudeRange() {
		// TASK R4
		return mountainHuts.values().stream()
				.collect(Collectors.groupingBy(
						h -> getHutAltitudeRange(h), // Use helper method
						Collectors.mapping(
								h -> h.getBedsNumber(),
								Collectors.maxBy(Integer::compareTo)
						)
				));
	}

	/**
	 * Compute the municipality names per number of mountain huts in a municipality.
	 * The lists of municipality names must be in alphabetical order.
	 * * @return a map with the number of mountain huts in a municipality as key and a
	 * list of municipality names as value
	 */
	public Map<Long, List<String>> municipalityNamesPerCountOfMountainHuts() {
		// TASK R4
		// Step 1: Count how many huts each municipality has
		Map<String, Long> countPerMunicipality = mountainHuts.values().stream()
				.collect(Collectors.groupingBy(
						h -> h.getMunicipality().getName(),
						Collectors.counting()
				));

		// TASK R4
		// Step 2: Invert the map (Count -> List of Names)
		Map<Long, List<String>> result = countPerMunicipality.entrySet().stream()
				.collect(Collectors.groupingBy(
						Map.Entry::getValue,
						Collectors.mapping(Map.Entry::getKey, Collectors.toList())
				));

		// TASK R4
		// Step 3: Sort the lists explicitly (Simple junior way)
		for (List<String> names : result.values()) {
			Collections.sort(names);
		}

		return result;
	}

	// ADDED FOR R4 (Helper to calculate range for a specific hut)
	private String getHutAltitudeRange(MountainHut h) {
		// If hut altitude is present, use it. Otherwise use municipality altitude.
		Integer altitude = h.getAltitude().orElse(h.getMunicipality().getAltitude());
		return getAltitudeRange(altitude);
	}

	// ADDED FOR R1
	private static class AltitudeRange {
		private final int min;
		private final int max;
		private final String label;

		public AltitudeRange(String label) {
			this.label = label;
			String[] values = label.split("-");
			this.min = Integer.parseInt(values[0]);
			this.max = Integer.parseInt(values[1]);
		}

		public boolean contains(int altitude) {
			return altitude > min && altitude <= max;
		}

		public String getLabel() {
			return label;
		}
	}

}