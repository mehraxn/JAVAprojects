package com.weather.report.services;

/**
 * Immutable description of a single row that could not be imported.
 *
 * @param rowNumber 1-based line number in the source file (header is line 1)
 * @param rawLine   the raw text of the offending line
 * @param fieldName the field that failed validation (or {@code "row"} for whole-row issues)
 * @param message   a human-readable explanation
 */
public record ImportError(int rowNumber, String rawLine, String fieldName, String message) {

  @Override
  public String toString() {
    return "row " + rowNumber + " [" + fieldName + "]: " + message;
  }
}
