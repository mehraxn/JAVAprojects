package com.weather.report.test.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.weather.report.util.DateParsingUtils;
import com.weather.report.util.ValidationUtils;

/** Phase 5 tests for centralised date parsing and validation helpers. */
class CustomDateParsingValidationTest {

  // ---- DateParsingUtils ----

  @Test
  void validDateIsParsed() {
    assertEquals(LocalDateTime.of(2025, 11, 16, 10, 0, 0),
        DateParsingUtils.parseDateTime("2025-11-16 10:00:00"));
  }

  @Test
  void invalidFormatIsRejectedWithValueInMessage() {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> DateParsingUtils.parseDateTime("16/11/2025"));
    org.junit.jupiter.api.Assertions.assertTrue(ex.getMessage().contains("16/11/2025"));
  }

  @Test
  void nullableAcceptsNullButRejectsBlank() {
    assertNull(DateParsingUtils.parseNullable(null));
    assertThrows(IllegalArgumentException.class, () -> DateParsingUtils.parseNullable("   "));
  }

  @Test
  void boundaryDatesAreAccepted() {
    assertEquals(LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        DateParsingUtils.parseDateTime("2025-01-01 00:00:00"));
    assertEquals(LocalDateTime.of(2025, 12, 31, 23, 59, 59),
        DateParsingUtils.parseDateTime("2025-12-31 23:59:59"));
  }

  @Test
  void startAfterEndIsRejected() {
    LocalDateTime start = LocalDateTime.of(2025, 11, 20, 0, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 11, 16, 0, 0, 0);
    assertThrows(IllegalArgumentException.class, () -> DateParsingUtils.validateRange(start, end));
  }

  @Test
  void openOrOrderedRangesAreAccepted() {
    LocalDateTime start = LocalDateTime.of(2025, 11, 16, 0, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 11, 20, 0, 0, 0);
    DateParsingUtils.validateRange(start, end);
    DateParsingUtils.validateRange(null, end);
    DateParsingUtils.validateRange(start, null);
    DateParsingUtils.validateRange(null, null);
  }

  // ---- ValidationUtils ----

  @Test
  void requireNotBlankRejectsNullAndBlank() {
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireNotBlank(null, "x"));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireNotBlank("  ", "x"));
    assertEquals("ok", ValidationUtils.requireNotBlank("ok", "x"));
  }

  @Test
  void requireNonNullRejectsNull() {
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireNonNull(null, "x"));
    assertEquals("v", ValidationUtils.requireNonNull("v", "x"));
  }
}
