package mountainhuts;

/**
 * Class representing a municipality that hosts a mountain hut.
 * It is a data class with getters for name, province, and altitude
 * */
public class Municipality {
	
	// ADDED FOR R2
	private final String name;
	// ADDED FOR R2
	private final String province;
	// ADDED FOR R2
	private final Integer altitude;

	public Municipality(String name, String province, Integer altitude) {
		// ADDED FOR R2
		this.name = name;
		// ADDED FOR R2
		this.province = province;
		// ADDED FOR R2
		this.altitude = altitude;
	}

	public String getName() {
		return name; // ADDED FOR R2
	}

	public String getProvince() {
		return province; // ADDED FOR R2
	}

	public Integer getAltitude() {
		return altitude; // ADDED FOR R2
	}

}