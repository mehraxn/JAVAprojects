package com.weather.report.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.weather.report.model.ThresholdType;
import com.weather.report.model.entities.Measurement;
import com.weather.report.model.entities.Network;
import com.weather.report.model.entities.Operator;
import com.weather.report.model.entities.Sensor;
import com.weather.report.model.entities.Threshold;
import com.weather.report.repositories.CRUDRepository;
import com.weather.report.util.CsvUtils;
import com.weather.report.util.DateParsingUtils;
import com.weather.report.util.ValidationUtils;

/**
 * Service responsible for importing measurements from CSV files and validating
 * them against sensor thresholds, triggering notifications when needed (see README).
 * <p>
 * CSV format (with header): {@code date, networkCode, gatewayCode, sensorCode, value}
 * where {@code date} uses {@link com.weather.report.WeatherReport#DATE_FORMAT}.
 * <p>
 * Import strategy: <b>partial import</b>. Valid rows are persisted; malformed rows
 * are skipped and recorded (with row numbers) in the returned {@link ImportResult}
 * and logged as warnings. Only an unreadable/missing file aborts the whole import
 * (with an unchecked exception, preserving the original public behaviour).
 */
public class DataImportingService {

  private static final Logger logger = LogManager.getLogger(DataImportingService.class);

  private static final double EPSILON = 0.000000001;

  /** Expected header columns (compared case-insensitively, after trimming). */
  private static final List<String> EXPECTED_HEADER =
      List.of("date", "networkCode", "gatewayCode", "sensorCode", "value");

  private DataImportingService() {
    // utility class
  }

  /**
   * Reads measurements from a CSV file, persists the valid ones and invokes
   * {@link #checkMeasurement(Measurement)} after each insertion.
   *
   * @param filePath path to the CSV file to import
   */
  public static void storeMeasurements(String filePath) {
    storeMeasurementsWithResult(filePath);
  }

  /**
   * Same as {@link #storeMeasurements(String)} but returns a detailed
   * {@link ImportResult} (rows read/imported/skipped, row-numbered errors, warnings).
   *
   * @param filePath path to the CSV file to import
   * @return the import summary
   * @throws IllegalArgumentException if the path is null/blank or the file cannot be found/read
   * @throws RuntimeException         if an I/O error occurs while reading the file (cause preserved)
   */
  public static ImportResult storeMeasurementsWithResult(String filePath) {
    File file = resolveReadableFile(filePath);
    ImportResult result = new ImportResult();

    try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
      String header = reader.readLine();
      if (header == null) {
        logger.warn("CSV file is empty (no header): {}", filePath);
        return result;
      }
      validateHeader(header, result);

      CRUDRepository<Measurement, Long> measurementRepository = new CRUDRepository<>(Measurement.class);

      String line;
      int rowNumber = 1; // header is line 1
      while ((line = reader.readLine()) != null) {
        rowNumber++;
        if (CsvUtils.isBlankLine(line)) {
          continue;
        }
        result.recordRead();

        ParsedRow parsed = parseRow(line, rowNumber);
        if (parsed.error() != null) {
          result.recordSkipped(parsed.error());
          logger.warn("Skipping {}", parsed.error());
          continue;
        }

        measurementRepository.create(parsed.measurement());
        checkMeasurement(parsed.measurement());
        result.recordImported();
      }
    } catch (IOException e) {
      // Preserve the original cause instead of swallowing it.
      throw new RuntimeException("Error reading CSV file: " + filePath, e);
    }

    logger.info("Import summary for {}: {}", filePath, result);
    return result;
  }

  /**
   * Resolves the CSV file, tolerating percent-encoded paths (e.g. produced by
   * {@code URL.getPath()}, which encodes spaces as {@code %20}).
   */
  private static File resolveReadableFile(String filePath) {
    ValidationUtils.requireNotBlank(filePath, "filePath");

    File direct = new File(filePath);
    if (direct.isFile()) {
      return direct;
    }
    // The path may be percent-encoded (URL.getPath() style). Try a decoded variant.
    String decoded = URLDecoder.decode(filePath, StandardCharsets.UTF_8);
    File decodedFile = new File(decoded);
    if (decodedFile.isFile()) {
      return decodedFile;
    }
    throw new IllegalArgumentException("CSV file not found or not readable: " + filePath);
  }

  private static void validateHeader(String header, ImportResult result) {
    List<String> columns = CsvUtils.parseLine(header).stream().map(String::trim).toList();
    boolean matches = columns.size() == EXPECTED_HEADER.size();
    if (matches) {
      for (int i = 0; i < EXPECTED_HEADER.size(); i++) {
        if (!EXPECTED_HEADER.get(i).equalsIgnoreCase(columns.get(i))) {
          matches = false;
          break;
        }
      }
    }
    if (!matches) {
      String warning = "Unexpected CSV header " + columns + ", expected " + EXPECTED_HEADER
          + " (rows are still parsed positionally)";
      result.addWarning(warning);
      logger.warn(warning);
    }
  }

  /** Parses and validates a single data line into a {@link Measurement} or an {@link ImportError}. */
  private static ParsedRow parseRow(String line, int rowNumber) {
    List<String> fields = CsvUtils.parseLine(line);
    if (fields.size() < EXPECTED_HEADER.size()) {
      return ParsedRow.error(new ImportError(rowNumber, line, "row",
          "expected " + EXPECTED_HEADER.size() + " columns but found " + fields.size()));
    }

    String dateStr = fields.get(0).trim();
    String networkCode = fields.get(1).trim();
    String gatewayCode = fields.get(2).trim();
    String sensorCode = fields.get(3).trim();
    String valueStr = fields.get(4).trim();

    ImportError blank = firstBlankField(rowNumber, line,
        new String[] {"date", "networkCode", "gatewayCode", "sensorCode", "value"},
        new String[] {dateStr, networkCode, gatewayCode, sensorCode, valueStr});
    if (blank != null) {
      return ParsedRow.error(blank);
    }

    double value;
    try {
      value = Double.parseDouble(valueStr);
    } catch (NumberFormatException e) {
      return ParsedRow.error(new ImportError(rowNumber, line, "value", "not a number: '" + valueStr + "'"));
    }

    LocalDateTime timestamp;
    try {
      timestamp = DateParsingUtils.parseDateTime(dateStr);
    } catch (IllegalArgumentException e) {
      return ParsedRow.error(new ImportError(rowNumber, line, "date", e.getMessage()));
    }

    return ParsedRow.ok(new Measurement(networkCode, gatewayCode, sensorCode, value, timestamp));
  }

  private static ImportError firstBlankField(int rowNumber, String line, String[] names, String[] values) {
    for (int i = 0; i < names.length; i++) {
      if (values[i] == null || values[i].isBlank()) {
        return new ImportError(rowNumber, line, names[i], "must not be blank");
      }
    }
    return null;
  }

  /** Result of parsing a single row: either a measurement or an error (never both). */
  private record ParsedRow(Measurement measurement, ImportError error) {
    static ParsedRow ok(Measurement m) {
      return new ParsedRow(m, null);
    }

    static ParsedRow error(ImportError e) {
      return new ParsedRow(null, e);
    }
  }

  /**
   * Validates the saved measurement against the threshold of the corresponding
   * sensor and notifies operators when the value is out of bounds.
   * <p>
   * NOTE: the structure of this method (looking the sensor up through
   * {@code new CRUDRepository<>(Sensor.class).read()} and using the
   * {@code currentSensor} variable) is required by the professor tests, which mock
   * {@code CRUDRepository} construction. It must not be restructured.
   */
  private static void checkMeasurement(Measurement measurement) {

    CRUDRepository<Sensor, String> sensorRepository = new CRUDRepository<>(Sensor.class);
    Sensor currentSensor = sensorRepository.read().stream()
        .filter(s -> measurement.getSensorCode().equals(s.getCode()))
        .findFirst()
        .orElse(null);

    boolean sensorExists = currentSensor != null;
    if (sensorExists) {
      Threshold sensorThreshold = currentSensor.getThreshold();
      boolean sensorHasThreshold = sensorThreshold != null;

      if (sensorHasThreshold) {
        double measurementValue = measurement.getValue();
        double thresholdValue = sensorThreshold.getValue();
        ThresholdType thresholdType = sensorThreshold.getType();

        boolean violationDetected = false;

        if (thresholdType == ThresholdType.LESS_THAN) {
          violationDetected = measurementValue < thresholdValue;
        } else if (thresholdType == ThresholdType.GREATER_THAN) {
          violationDetected = measurementValue > thresholdValue;
        } else if (thresholdType == ThresholdType.LESS_OR_EQUAL) {
          violationDetected = measurementValue <= thresholdValue;
        } else if (thresholdType == ThresholdType.GREATER_OR_EQUAL) {
          violationDetected = measurementValue >= thresholdValue;
        } else if (thresholdType == ThresholdType.EQUAL) {
          violationDetected = Math.abs(measurementValue - thresholdValue) < EPSILON;
        } else if (thresholdType == ThresholdType.NOT_EQUAL) {
          violationDetected = Math.abs(measurementValue - thresholdValue) >= EPSILON;
        }

        if (violationDetected) {
          CRUDRepository<Network, String> networkRepository = new CRUDRepository<>(Network.class);
          Network network = networkRepository.read(measurement.getNetworkCode());

          if (network != null) {
            Collection<Operator> operators = network.getOperators();
            if (operators != null && !operators.isEmpty()) {
              AlertingService.notifyThresholdViolation(operators, currentSensor.getCode());
            }
          }
        }
      }
    }
  }

}
