package com.weather.report.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Summary of a CSV import: how many data rows were read, imported and skipped,
 * plus the row-numbered errors and any warnings.
 * <p>
 * It is populated by {@link DataImportingService} during an import (via the
 * package-private {@code record*} methods) and then handed back to callers. All
 * collection getters return unmodifiable views so callers cannot mutate the result.
 */
public final class ImportResult {

  private int rowsRead;
  private int rowsImported;
  private int rowsSkipped;
  private final List<ImportError> errors = new ArrayList<>();
  private final List<String> warnings = new ArrayList<>();

  ImportResult() {
    // populated by DataImportingService
  }

  void recordRead() {
    rowsRead++;
  }

  void recordImported() {
    rowsImported++;
  }

  void recordSkipped(ImportError error) {
    rowsSkipped++;
    if (error != null) {
      errors.add(error);
    }
  }

  void addWarning(String warning) {
    if (warning != null) {
      warnings.add(warning);
    }
  }

  /** Number of non-blank data rows encountered (excludes the header). */
  public int getRowsRead() {
    return rowsRead;
  }

  /** Number of rows successfully imported. */
  public int getRowsImported() {
    return rowsImported;
  }

  /** Number of rows skipped because of a validation error. */
  public int getRowsSkipped() {
    return rowsSkipped;
  }

  /** Row-numbered errors for skipped rows (unmodifiable). */
  public List<ImportError> getErrors() {
    return Collections.unmodifiableList(errors);
  }

  /** Non-fatal warnings, e.g. an unexpected header (unmodifiable). */
  public List<String> getWarnings() {
    return Collections.unmodifiableList(warnings);
  }

  @Override
  public String toString() {
    return "ImportResult{read=" + rowsRead + ", imported=" + rowsImported
        + ", skipped=" + rowsSkipped + ", errors=" + errors.size()
        + ", warnings=" + warnings.size() + "}";
  }
}
