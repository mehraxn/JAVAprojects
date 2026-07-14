package diet;

import java.time.LocalTime;

final class ValidationUtils {
	private ValidationUtils() {
	}

	static String requireNotBlank(String value, String fieldName) {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException(fieldName + " cannot be null or blank");
		}
		return value.trim();
	}

	static double requireNonNegative(double value, String fieldName) {
		if (value < 0.0) {
			throw new IllegalArgumentException(fieldName + " cannot be negative");
		}
		return value;
	}

	static double requirePositive(double value, String fieldName) {
		if (value <= 0.0) {
			throw new IllegalArgumentException(fieldName + " must be positive");
		}
		return value;
	}

	static int requirePositiveInt(int value, String fieldName) {
		if (value <= 0) {
			throw new IllegalArgumentException(fieldName + " must be positive");
		}
		return value;
	}

	static LocalTime parseTime(String value, String fieldName) {
		String time = requireNotBlank(value, fieldName);
		if (!time.matches("\\d{1,2}:\\d{2}")) {
			throw new IllegalArgumentException(fieldName + " must use H:mm or HH:mm format");
		}

		String[] parts = time.split(":");
		int hour = Integer.parseInt(parts[0]);
		int minute = Integer.parseInt(parts[1]);
		if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
			throw new IllegalArgumentException(fieldName + " is outside the valid time range");
		}
		return LocalTime.of(hour, minute);
	}

	static String formatTime(LocalTime time) {
		return "%02d:%02d".formatted(time.getHour(), time.getMinute());
	}
}
