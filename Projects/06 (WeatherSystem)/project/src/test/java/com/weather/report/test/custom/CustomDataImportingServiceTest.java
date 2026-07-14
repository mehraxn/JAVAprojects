package com.weather.report.test.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.weather.report.model.entities.Measurement;
import com.weather.report.repositories.MeasurementRepository;
import com.weather.report.services.DataImportingService;
import com.weather.report.services.ImportError;
import com.weather.report.services.ImportResult;
import com.weather.report.test.BasePersistenceTest;

/**
 * Phase 5 tests for {@link DataImportingService}: file-path validation, header and
 * row validation, row-numbered errors, and the partial-import strategy. Uses
 * temporary CSV files (never committed).
 */
class CustomDataImportingServiceTest extends BasePersistenceTest {

  private static final String HEADER = "date, networkCode, gatewayCode, sensorCode, value";
  private static final String VALID_ROW = "2025-11-16 08:00:00, NET_01, GW_0101, S_010101, 20.45";

  @TempDir
  Path tempDir;

  private String writeCsv(String name, String... lines) throws IOException {
    Path file = tempDir.resolve(name);
    Files.writeString(file, String.join("\n", lines), StandardCharsets.UTF_8);
    return file.toString();
  }

  // ---- file path validation ----

  @Test
  void nullPathIsRejected() {
    assertThrows(IllegalArgumentException.class,
        () -> DataImportingService.storeMeasurementsWithResult(null));
  }

  @Test
  void blankPathIsRejected() {
    assertThrows(IllegalArgumentException.class,
        () -> DataImportingService.storeMeasurementsWithResult("   "));
  }

  @Test
  void missingFileIsRejected() {
    String missing = tempDir.resolve("does-not-exist.csv").toString();
    assertThrows(IllegalArgumentException.class,
        () -> DataImportingService.storeMeasurementsWithResult(missing));
  }

  // ---- header / empty file ----

  @Test
  void emptyFileImportsNothing() throws IOException {
    ImportResult result = DataImportingService.storeMeasurementsWithResult(writeCsv("empty.csv", ""));
    assertEquals(0, result.getRowsRead());
    assertEquals(0, result.getRowsImported());
  }

  @Test
  void headerOnlyFileImportsNothing() throws IOException {
    ImportResult result = DataImportingService.storeMeasurementsWithResult(writeCsv("header-only.csv", HEADER));
    assertEquals(0, result.getRowsRead());
    assertEquals(0, result.getRowsImported());
  }

  @Test
  void wrongHeaderProducesWarningButRowsStillParsedPositionally() throws IOException {
    ImportResult result = DataImportingService.storeMeasurementsWithResult(
        writeCsv("wrong-header.csv", "a,b,c,d,e", VALID_ROW));
    assertFalse(result.getWarnings().isEmpty(), "an unexpected header should be flagged as a warning");
    assertEquals(1, result.getRowsImported());
  }

  // ---- row validation / row-numbered errors ----

  @Test
  void nonNumericValueProducesRowNumberedError() throws IOException {
    ImportResult result = DataImportingService.storeMeasurementsWithResult(
        writeCsv("bad-value.csv", HEADER, "2025-11-16 08:00:00, NET_01, GW_0101, S_010101, NaNumber"));
    assertEquals(1, result.getRowsRead());
    assertEquals(0, result.getRowsImported());
    assertEquals(1, result.getRowsSkipped());
    assertEquals(1, result.getErrors().size());
    ImportError error = result.getErrors().get(0);
    assertEquals(2, error.rowNumber());
    assertEquals("value", error.fieldName());
  }

  @Test
  void invalidDateProducesRowNumberedError() throws IOException {
    ImportResult result = DataImportingService.storeMeasurementsWithResult(
        writeCsv("bad-date.csv", HEADER, "16/11/2025, NET_01, GW_0101, S_010101, 20.45"));
    assertEquals(1, result.getRowsSkipped());
    assertEquals("date", result.getErrors().get(0).fieldName());
  }

  @Test
  void tooFewColumnsProducesRowError() throws IOException {
    ImportResult result = DataImportingService.storeMeasurementsWithResult(
        writeCsv("short.csv", HEADER, "2025-11-16 08:00:00, NET_01, GW_0101"));
    assertEquals(1, result.getRowsSkipped());
    assertEquals("row", result.getErrors().get(0).fieldName());
  }

  @Test
  void blankRequiredFieldProducesError() throws IOException {
    ImportResult result = DataImportingService.storeMeasurementsWithResult(
        writeCsv("blank-field.csv", HEADER, "2025-11-16 08:00:00, , GW_0101, S_010101, 20.45"));
    assertEquals(1, result.getRowsSkipped());
    assertEquals("networkCode", result.getErrors().get(0).fieldName());
  }

  // ---- successful / partial import ----

  @Test
  void validRowsAreImportedAndPersisted() throws IOException {
    String path = writeCsv("valid.csv", HEADER, VALID_ROW,
        "2025-11-16 09:00:00, NET_01, GW_0101, S_010101, 21.00");
    ImportResult result = DataImportingService.storeMeasurementsWithResult(path);

    assertEquals(2, result.getRowsImported());
    assertEquals(0, result.getRowsSkipped());
    List<Measurement> persisted = new MeasurementRepository().findBySensorCode("S_010101");
    assertEquals(2, persisted.size());
  }

  @Test
  void mixedValidAndInvalidRowsFollowPartialImportStrategy() throws IOException {
    String path = writeCsv("mixed.csv", HEADER,
        VALID_ROW,                                                   // ok
        "2025-11-16 09:00:00, NET_01, GW_0101, S_010101, oops",      // bad value
        "2025-11-16 10:00:00, NET_01, GW_0101, S_010101, 22.00");    // ok
    ImportResult result = DataImportingService.storeMeasurementsWithResult(path);

    assertEquals(3, result.getRowsRead());
    assertEquals(2, result.getRowsImported());
    assertEquals(1, result.getRowsSkipped());
    assertEquals(2, new MeasurementRepository().findBySensorCode("S_010101").size());
  }

  @Test
  void blankLinesAreIgnored() throws IOException {
    String path = writeCsv("blank-lines.csv", HEADER, VALID_ROW, "", "   ",
        "2025-11-16 10:00:00, NET_01, GW_0101, S_010101, 22.00");
    ImportResult result = DataImportingService.storeMeasurementsWithResult(path);
    assertEquals(2, result.getRowsRead());
    assertEquals(2, result.getRowsImported());
  }

  @Test
  void importResultErrorListIsUnmodifiable() throws IOException {
    ImportResult result = DataImportingService.storeMeasurementsWithResult(
        writeCsv("bad.csv", HEADER, "2025-11-16 08:00:00, NET_01, GW_0101, S_010101, x"));
    assertThrows(UnsupportedOperationException.class, () -> result.getErrors().clear());
    assertTrue(result.getWarnings() != null);
  }
}
