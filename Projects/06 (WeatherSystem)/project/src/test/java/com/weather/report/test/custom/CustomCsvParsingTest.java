package com.weather.report.test.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.weather.report.util.CsvUtils;

/** Phase 5 tests for the dependency-free CSV line parser ({@link CsvUtils}). */
class CustomCsvParsingTest {

  @Test
  void simpleRowIsSplitOnCommas() {
    assertEquals(List.of("a", "b", "c"), CsvUtils.parseLine("a,b,c"));
  }

  @Test
  void quotedValueHasQuotesRemoved() {
    assertEquals(List.of("John Smith", "Course"), CsvUtils.parseLine("\"John Smith\",Course"));
  }

  @Test
  void commaInsideQuotesStaysInOneField() {
    assertEquals(List.of("New York, USA", "25"), CsvUtils.parseLine("\"New York, USA\",25"));
  }

  @Test
  void escapedDoubleQuoteBecomesSingleQuote() {
    assertEquals(List.of("Book \"Advanced Java\"", "49.99"),
        CsvUtils.parseLine("\"Book \"\"Advanced Java\"\"\",49.99"));
  }

  @Test
  void emptyFieldsArePreserved() {
    assertEquals(List.of("a", "", "c"), CsvUtils.parseLine("a,,c"));
  }

  @Test
  void trailingEmptyFieldIsPreserved() {
    assertEquals(List.of("a", "b", ""), CsvUtils.parseLine("a,b,"));
  }

  @Test
  void columnCountCanBeChecked() {
    assertEquals(5, CsvUtils.parseLine("2025-11-16 08:00:00, NET_01, GW_0101, S_010101, 20.45").size());
    assertEquals(3, CsvUtils.parseLine("a,b,c").size());
  }

  @Test
  void blankLineDetection() {
    assertTrue(CsvUtils.isBlankLine(""));
    assertTrue(CsvUtils.isBlankLine("   "));
    assertTrue(CsvUtils.isBlankLine(null));
    assertFalse(CsvUtils.isBlankLine("a,b"));
  }
}
