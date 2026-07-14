package custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mountainhuts.Municipality;
import mountainhuts.Region;

class CustomRegionTest {

  private Region r;

  @BeforeEach
  void setUp() {
    r = new Region("Piemonte");
  }

  @Test
  void regionNameRejectedWhenBlank() {
    assertThrows(IllegalArgumentException.class, () -> new Region("  "));
  }

  @Test
  void defaultAltitudeRange() {
    assertEquals("0-INF", r.getAltitudeRange(0));
    assertEquals("0-INF", r.getAltitudeRange(null));
  }

  @Test
  void invalidAltitudeRangeFormatRejected() {
    assertThrows(IllegalArgumentException.class, () -> r.setAltitudeRanges("0-1000", "bad"));
  }

  @Test
  void duplicateMunicipalityReturnsExisting() {
    Municipality a = r.createOrGetMunicipality("Torino", "TO", 245);
    Municipality b = r.createOrGetMunicipality("Torino", "TO", 245);
    assertSame(a, b);
    assertEquals(1, r.getMunicipalities().size());
  }

  @Test
  void duplicateMountainHutReturnsExisting() {
    Municipality m = r.createOrGetMunicipality("Torino", "TO", 245);
    var h1 = r.createOrGetMountainHut("Alpe", "Rifugio", 10, m);
    var h2 = r.createOrGetMountainHut("Alpe", "Rifugio", 10, m);
    assertSame(h1, h2);
    assertEquals(1, r.getMountainHuts().size());
  }

  @Test
  void returnedCollectionsAreUnmodifiable() {
    r.createOrGetMunicipality("Torino", "TO", 245);
    assertThrows(UnsupportedOperationException.class,
        () -> r.getMunicipalities().clear());
    assertThrows(UnsupportedOperationException.class,
        () -> r.getMountainHuts().clear());
  }

  @Test
  void emptyRegionQueriesReturnEmptyMaps() {
    assertTrue(r.countMunicipalitiesPerProvince().isEmpty());
    assertTrue(r.countMountainHutsPerAltitudeRange().isEmpty());
    assertTrue(r.totalBedsNumberPerProvince().isEmpty());
    assertTrue(r.municipalityNamesPerCountOfMountainHuts().isEmpty());
  }

  @Test
  void countMunicipalitiesPerProvinceIsOrderedByProvince() {
    r.createOrGetMunicipality("Cuneo", "CN", 500);
    r.createOrGetMunicipality("Torino", "TO", 245);
    r.createOrGetMunicipality("Alessandria", "AL", 95);
    Map<String, Long> res = r.countMunicipalitiesPerProvince();
    assertEquals(List.of("AL", "CN", "TO"), new ArrayList<>(res.keySet()));
  }

  @Test
  void endToEndQueries() {
    r.setAltitudeRanges("0-1000", "1000-2000", "2000-3000");
    Municipality cuneo = r.createOrGetMunicipality("Acceglio", "CN", 1200);
    Municipality torino = r.createOrGetMunicipality("Bardonecchia", "TO", 1300);

    r.createOrGetMountainHut("HutA", 1500, "Rifugio", 20, cuneo); // 1000-2000
    r.createOrGetMountainHut("HutB", 2500, "Rifugio", 40, cuneo); // 2000-3000
    r.createOrGetMountainHut("HutC", null, "Bivacco", 10, torino); // falls back to 1300 -> 1000-2000

    assertEquals(Long.valueOf(2), r.countMountainHutsPerAltitudeRange().get("1000-2000"));
    assertEquals(Long.valueOf(1), r.countMountainHutsPerAltitudeRange().get("2000-3000"));
    assertEquals(Integer.valueOf(60), r.totalBedsNumberPerProvince().get("CN"));
    assertEquals(Integer.valueOf(10), r.totalBedsNumberPerProvince().get("TO"));
    assertEquals(Optional.of(40), r.maximumBedsNumberPerAltitudeRange().get("2000-3000"));

    Map<Long, List<String>> byCount = r.municipalityNamesPerCountOfMountainHuts();
    assertEquals(List.of("Bardonecchia"), byCount.get(1L));
    assertEquals(List.of("Acceglio"), byCount.get(2L));
  }
}
