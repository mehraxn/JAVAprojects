package com.weather.report.util;

/**
 * Small, unchecked validation helpers for the import/utility layer.
 * <p>
 * These throw {@link IllegalArgumentException} and are intended for code paths
 * that do not declare the project's checked {@code WeatherReportException} family
 * (import service, utilities). The operations layer keeps its own checked-exception
 * validation (e.g. {@code InvalidInputDataException}) unchanged, so this utility is
 * deliberately not forced onto it.
 */
public final class ValidationUtils {

  private ValidationUtils() {
    // utility class
  }

  /**
   * @return {@code value} if it is non-null and not blank
   * @throws IllegalArgumentException otherwise
   */
  public static String requireNotBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be null or blank");
    }
    return value;
  }

  /**
   * @return {@code value} if it is non-null
   * @throws IllegalArgumentException otherwise
   */
  public static <T> T requireNonNull(T value, String fieldName) {
    if (value == null) {
      throw new IllegalArgumentException(fieldName + " must not be null");
    }
    return value;
  }
}
