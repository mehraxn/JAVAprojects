package custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import mountainhuts.AltitudeRange;

class CustomAltitudeRangeTest {

  @Test
  void validRangeParsesBoundsAndLabel() {
    AltitudeRange r = new AltitudeRange("1000-2000");
    assertEquals(1000, r.getMin());
    assertEquals(2000, r.getMax());
    assertEquals("1000-2000", r.getLabel());
  }

  @Test
  void containsIsLeftOpenRightClosed() {
    AltitudeRange r = new AltitudeRange("1000-2000");
    assertFalse(r.contains(1000), "lower bound is excluded");
    assertTrue(r.contains(1001));
    assertTrue(r.contains(1500));
    assertTrue(r.contains(2000), "upper bound is included");
    assertFalse(r.contains(2001));
  }

  @Test
  void nullOrBlankLabelRejected() {
    assertThrows(IllegalArgumentException.class, () -> new AltitudeRange(null));
    assertThrows(IllegalArgumentException.class, () -> new AltitudeRange("   "));
  }

  @Test
  void malformedLabelRejected() {
    assertThrows(IllegalArgumentException.class, () -> new AltitudeRange("1000"));
    assertThrows(IllegalArgumentException.class, () -> new AltitudeRange("a-b"));
  }

  @Test
  void negativeBoundsRejected() {
    assertThrows(IllegalArgumentException.class, () -> new AltitudeRange("-100-200"));
  }

  @Test
  void lowerGreaterThanUpperRejected() {
    assertThrows(IllegalArgumentException.class, () -> new AltitudeRange("2000-1000"));
  }
}
