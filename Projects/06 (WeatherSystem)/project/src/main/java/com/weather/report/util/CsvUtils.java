package com.weather.report.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal, dependency-free CSV line parser.
 * <p>
 * Supports the common CSV features needed by this project:
 * <ul>
 *   <li>simple comma-separated values;</li>
 *   <li>double-quoted fields (quotes are removed from the returned value);</li>
 *   <li>commas inside quoted fields (kept as part of the field);</li>
 *   <li>escaped double quotes inside quoted fields ({@code ""} -&gt; {@code "});</li>
 *   <li>empty and trailing-empty fields (preserved).</li>
 * </ul>
 * Field values are returned verbatim (no trimming); callers decide whether to trim.
 * This is intentionally small — it is not a full RFC-4180 implementation (e.g. it
 * does not join fields across physical lines).
 */
public final class CsvUtils {

  private CsvUtils() {
    // utility class
  }

  /** @return {@code true} if the line is {@code null} or only whitespace. */
  public static boolean isBlankLine(String line) {
    return line == null || line.trim().isEmpty();
  }

  /**
   * Splits a single CSV line into its fields.
   *
   * @param line the raw line (without the trailing line separator); may be {@code null}
   * @return the parsed fields (never {@code null}); a {@code null} line yields an empty list
   */
  public static List<String> parseLine(String line) {
    List<String> fields = new ArrayList<>();
    if (line == null) {
      return fields;
    }

    StringBuilder current = new StringBuilder();
    boolean inQuotes = false;
    int i = 0;
    while (i < line.length()) {
      char c = line.charAt(i);
      if (inQuotes) {
        if (c == '"') {
          if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
            current.append('"'); // escaped quote
            i += 2;
          } else {
            inQuotes = false; // closing quote
            i++;
          }
        } else {
          current.append(c);
          i++;
        }
      } else {
        if (c == '"') {
          inQuotes = true;
          i++;
        } else if (c == ',') {
          fields.add(current.toString());
          current.setLength(0);
          i++;
        } else {
          current.append(c);
          i++;
        }
      }
    }
    fields.add(current.toString()); // last (possibly empty) field
    return fields;
  }
}
