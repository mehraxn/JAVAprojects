package custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import mountainhuts.Region;

class CustomCsvImportTest {

  private static final String HEADER =
      "Province;Municipality;MunicipalityAltitude;Name;Altitude;Category;BedsNumber";

  @TempDir
  Path tempDir;

  private String write(String name, String... lines) throws IOException {
    Path file = tempDir.resolve(name);
    Files.writeString(file, String.join("\n", lines), StandardCharsets.UTF_8);
    return file.toString();
  }

  @Test
  void importsRealDatasetWithExpectedCounts() {
    // The project ships data/mountain_huts.csv; tests run from the project root.
    Region r = Region.fromFile("Piemonte", "data/mountain_huts.csv");
    assertEquals(94, r.getMunicipalities().size(), "Wrong number of municipalities");
    assertEquals(167, r.getMountainHuts().size(), "Wrong number of mountain huts");
  }

  @Test
  void emptyOptionalHutAltitudeIsAccepted() throws IOException {
    String file = write("optional-altitude.csv", HEADER,
        "VCO;BACENO;655;SESTO CALENDE;;Rifugio Alpino;0");
    Region r = Region.fromFile("R", file);
    assertEquals(1, r.getMountainHuts().size());
    assertTrue(r.getMountainHuts().iterator().next().getAltitude().isEmpty());
  }

  @Test
  void missingFileIsRejected() {
    String missing = tempDir.resolve("nope.csv").toString();
    assertThrows(IllegalArgumentException.class, () -> Region.fromFile("R", missing));
  }

  @Test
  void nullOrBlankPathRejected() {
    assertThrows(IllegalArgumentException.class, () -> Region.readData(null));
    assertThrows(IllegalArgumentException.class, () -> Region.readData("  "));
  }

  @Test
  void malformedRowIsRejectedWithRowNumber() throws IOException {
    String file = write("malformed.csv", HEADER, "TO;Torino;245;OnlyThreeFields");
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> Region.fromFile("R", file));
    assertTrue(ex.getMessage().contains("Row 2"), "Message should reference the row number");
  }

  @Test
  void invalidNumericFieldIsRejected() throws IOException {
    String file = write("bad-number.csv", HEADER,
        "TO;Torino;NotANumber;Alpe;1000;Rifugio;10");
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> Region.fromFile("R", file));
    assertTrue(ex.getMessage().contains("MunicipalityAltitude"));
  }

  @Test
  void blankRequiredFieldIsRejected() throws IOException {
    String file = write("blank-field.csv", HEADER, "TO; ;245;Alpe;1000;Rifugio;10");
    assertThrows(IllegalArgumentException.class, () -> Region.fromFile("R", file));
  }
}
