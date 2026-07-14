package custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import mountainhuts.MountainHut;
import mountainhuts.Municipality;

class CustomMunicipalityAndHutTest {

  // ---- Municipality ----

  @Test
  void validMunicipalityAndGetters() {
    Municipality m = new Municipality("Torino", "TO", 245);
    assertEquals("Torino", m.getName());
    assertEquals("TO", m.getProvince());
    assertEquals(Integer.valueOf(245), m.getAltitude());
  }

  @Test
  void municipalityFieldsAreTrimmed() {
    Municipality m = new Municipality("  Cuneo  ", "  CN  ", 534);
    assertEquals("Cuneo", m.getName());
    assertEquals("CN", m.getProvince());
  }

  @Test
  void municipalityRejectsBlankNameOrProvinceAndNegativeAltitude() {
    assertThrows(IllegalArgumentException.class, () -> new Municipality(" ", "TO", 245));
    assertThrows(IllegalArgumentException.class, () -> new Municipality("Torino", null, 245));
    assertThrows(IllegalArgumentException.class, () -> new Municipality("Torino", "TO", -1));
  }

  // ---- MountainHut ----

  @Test
  void validHutWithOwnAltitude() {
    Municipality m = new Municipality("Torino", "TO", 245);
    MountainHut h = new MountainHut("Alpe", 1660, "Rifugio", 32, m);
    assertEquals("Alpe", h.getName());
    assertEquals(Optional.of(1660), h.getAltitude());
    assertEquals("Rifugio", h.getCategory());
    assertEquals(Integer.valueOf(32), h.getBedsNumber());
    assertEquals(m, h.getMunicipality());
  }

  @Test
  void validHutWithoutOwnAltitude() {
    Municipality m = new Municipality("Torino", "TO", 245);
    MountainHut h = new MountainHut("Tappa", null, "Bivacco", 0, m);
    assertTrue(h.getAltitude().isEmpty());
    assertEquals(Integer.valueOf(0), h.getBedsNumber());
  }

  @Test
  void hutRejectsInvalidInput() {
    Municipality m = new Municipality("Torino", "TO", 245);
    assertThrows(IllegalArgumentException.class, () -> new MountainHut(" ", 100, "Rifugio", 5, m));
    assertThrows(IllegalArgumentException.class, () -> new MountainHut("Alpe", 100, "  ", 5, m));
    assertThrows(IllegalArgumentException.class, () -> new MountainHut("Alpe", 100, "Rifugio", -1, m));
    assertThrows(IllegalArgumentException.class, () -> new MountainHut("Alpe", -5, "Rifugio", 5, m));
    assertThrows(IllegalArgumentException.class, () -> new MountainHut("Alpe", 100, "Rifugio", 5, null));
  }
}
