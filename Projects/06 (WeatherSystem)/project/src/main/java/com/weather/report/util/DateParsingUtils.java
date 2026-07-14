package com.weather.report.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.weather.report.WeatherReport;

/**
 * Centralised parsing/validation of the project's date-time format
 * ({@link WeatherReport#DATE_FORMAT} = {@code yyyy-MM-dd HH:mm:ss}).
 * <p>
 * Replaces the ad-hoc {@code split(...)}/{@code substring(...)} parsing that was
 * duplicated across the import service and report classes. Throws
 * {@link IllegalArgumentException} (unchecked) so it can be used from code paths
 * that do not declare the project's checked exceptions (e.g. import, reports).
 */
public final class DateParsingUtils {

  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern(WeatherReport.DATE_FORMAT);

  private DateParsingUtils() {
    // utility class
  }

  /**
   * Parses a required date-time value.
   *
   * @param value the date-time string (must not be null/blank)
   * @return the parsed {@link LocalDateTime}
   * @throws IllegalArgumentException if the value is null/blank or not in the expected format
   */
  public static LocalDateTime parseDateTime(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Date value must not be null or blank");
    }
    try {
      return LocalDateTime.parse(value.trim(), FORMATTER);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException(
          "Invalid date '" + value + "', expected format " + WeatherReport.DATE_FORMAT, e);
    }
  }

  /**
   * Parses an optional date-time value.
   *
   * @param value the date-time string, or {@code null}
   * @return the parsed {@link LocalDateTime}, or {@code null} if the input is {@code null}
   * @throws IllegalArgumentException if a non-null value is blank or malformed
   */
  public static LocalDateTime parseNullable(String value) {
    return value == null ? null : parseDateTime(value);
  }

  /**
   * Validates that a (possibly open) range is well ordered.
   *
   * @throws IllegalArgumentException if both bounds are present and {@code start} is after {@code end}
   */
  public static void validateRange(LocalDateTime start, LocalDateTime end) {
    if (start != null && end != null && start.isAfter(end)) {
      throw new IllegalArgumentException("Start date must not be after end date");
    }
  }
}
