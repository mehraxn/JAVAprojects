package custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import social.NoSuchCodeException;
import social.PersonExistsException;

class CustomPersonAndFriendshipTest extends CustomTestBase {

  // ---- persistence / person ----

  @Test
  void addAndGetPerson() throws Exception {
    social.addPerson("mario", "Mario", "Rossi");
    assertEquals("mario Mario Rossi", social.getPerson("mario"));
  }

  @Test
  void duplicatePersonIsRejected() throws Exception {
    social.addPerson("mario", "Mario", "Rossi");
    assertThrows(PersonExistsException.class, () -> social.addPerson("mario", "Other", "Name"));
  }

  @Test
  void blankPersonFieldsAreRejected() {
    assertThrows(IllegalArgumentException.class, () -> social.addPerson(" ", "Mario", "Rossi"));
    assertThrows(IllegalArgumentException.class, () -> social.addPerson("mario", "", "Rossi"));
    assertThrows(IllegalArgumentException.class, () -> social.addPerson("mario", "Mario", null));
  }

  @Test
  void getMissingPersonThrows() {
    assertThrows(NoSuchCodeException.class, () -> social.getPerson("ghost"));
  }

  // ---- friendship ----

  @Test
  void friendshipIsBidirectional() throws Exception {
    social.addPerson("a", "A", "A");
    social.addPerson("b", "B", "B");
    social.addFriendship("a", "b");

    assertTrue(social.listOfFriends("a").contains("b"));
    assertTrue(social.listOfFriends("b").contains("a"));
  }

  @Test
  void friendListIsSortedByCode() throws Exception {
    social.addPerson("a", "A", "A");
    social.addPerson("c", "C", "C");
    social.addPerson("b", "B", "B");
    social.addFriendship("a", "c");
    social.addFriendship("a", "b");

    Collection<String> friends = social.listOfFriends("a");
    assertEquals(new ArrayList<>(List.of("b", "c")), new ArrayList<>(friends));
  }

  @Test
  void selfFriendshipIsRejected() throws Exception {
    social.addPerson("a", "A", "A");
    assertThrows(IllegalArgumentException.class, () -> social.addFriendship("a", "a"));
  }

  @Test
  void duplicateFriendshipIsIdempotent() throws Exception {
    social.addPerson("a", "A", "A");
    social.addPerson("b", "B", "B");
    social.addFriendship("a", "b");
    social.addFriendship("a", "b"); // repeated -> no error, still a single friend

    assertEquals(1, social.listOfFriends("a").size());
  }

  @Test
  void friendshipWithMissingPersonThrows() throws Exception {
    social.addPerson("a", "A", "A");
    assertThrows(NoSuchCodeException.class, () -> social.addFriendship("a", "ghost"));
  }
}
