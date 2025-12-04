package mountainhuts;

import java.util.Optional;

/**
 * Represents a mountain hut
 * * It includes a name, optional altitude, category,
 * number of beds and location municipality.
 * *
 */
public class MountainHut {
	// ADDED FOR R2
	private final String name;
	// ADDED FOR R2
	private final Integer altitude;
	// ADDED FOR R2
	private final String category;
	// ADDED FOR R2
	private final Integer bedsNumber;
	// ADDED FOR R2
	private final Municipality municipality;

	// ADDED FOR R2 (Constructor with all fields)
	public MountainHut(String name, Integer altitude, String category, Integer bedsNumber, Municipality municipality) {
		this.name = name;
		this.altitude = altitude;
		this.category = category;
		this.bedsNumber = bedsNumber;
		this.municipality = municipality;
	}

	/**
	 * Retrieves the name of the hut
	 * @return name of the hut
	 */
	public String getName() {
		return name; // ADDED FOR R2
	}

	/**
	 * Retrieves altituted if available
	 * * @return optional hut altitude
	 */
	public Optional<Integer> getAltitude() {
		return Optional.ofNullable(altitude); // ADDED FOR R2
	}

	/**
	 * Retrieves the category of the hut
	 * @return hut category
	 */
	public String getCategory() {
		return category; // ADDED FOR R2
	}

	/**
	 * Retrieves the number of beds available in the hut
	 * @return number of beds
	 */
	public Integer getBedsNumber() {
		return bedsNumber; // ADDED FOR R2
	}

	/**
	 * Retrieves the municipality of the hut
	 * @return hut municipality
	 */
	public Municipality getMunicipality() {
		return municipality; // ADDED FOR R2
	}
}