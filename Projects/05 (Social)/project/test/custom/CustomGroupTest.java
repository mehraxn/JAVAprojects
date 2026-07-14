package custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import social.GroupExistsException;
import social.NoSuchCodeException;

class CustomGroupTest extends CustomTestBase {

  @Test
  void addAndListGroupsSorted() throws Exception {
    social.addGroup("torino");
    social.addGroup("acolyte");
    assertEquals(new ArrayList<>(List.of("acolyte", "torino")), new ArrayList<>(social.listOfGroups()));
  }

  @Test
  void duplicateGroupIsRejected() throws Exception {
    social.addGroup("poli");
    assertThrows(GroupExistsException.class, () -> social.addGroup("poli"));
  }

  @Test
  void blankGroupNameRejected() {
    assertThrows(IllegalArgumentException.class, () -> social.addGroup("  "));
  }

  @Test
  void addPersonToGroupAndListMembersSorted() throws Exception {
    social.addPerson("b", "B", "B");
    social.addPerson("a", "A", "A");
    social.addGroup("g");
    social.addPersonToGroup("b", "g");
    social.addPersonToGroup("a", "g");

    Collection<String> members = social.listOfPeopleInGroup("g");
    assertEquals(new ArrayList<>(List.of("a", "b")), new ArrayList<>(members));
  }

  @Test
  void duplicateMembershipIsIdempotent() throws Exception {
    social.addPerson("a", "A", "A");
    social.addGroup("g");
    social.addPersonToGroup("a", "g");
    social.addPersonToGroup("a", "g");
    assertEquals(1, social.listOfPeopleInGroup("g").size());
  }

  @Test
  void addToMissingGroupOrMissingPersonThrows() throws Exception {
    social.addPerson("a", "A", "A");
    social.addGroup("g");
    assertThrows(NoSuchCodeException.class, () -> social.addPersonToGroup("a", "missing"));
    assertThrows(NoSuchCodeException.class, () -> social.addPersonToGroup("ghost", "g"));
  }

  @Test
  void listPeopleInMissingGroupIsEmpty() {
    assertTrue(social.listOfPeopleInGroup("missing").isEmpty());
  }

  @Test
  void deleteGroupRemovesItAndDetachesMembers() throws Exception {
    social.addPerson("a", "A", "A");
    social.addGroup("g");
    social.addPersonToGroup("a", "g");

    social.deleteGroup("g");

    assertFalse(social.listOfGroups().contains("g"));
    assertTrue(social.listOfPeopleInGroup("g").isEmpty());
    // person still exists after the group was removed
    assertEquals("a A A", social.getPerson("a"));
  }

  @Test
  void deleteMissingGroupThrows() {
    assertThrows(NoSuchCodeException.class, () -> social.deleteGroup("missing"));
  }

  @Test
  void updateGroupNamePreservesMembers() throws Exception {
    social.addPerson("a", "A", "A");
    social.addGroup("old");
    social.addPersonToGroup("a", "old");

    social.updateGroupName("old", "new");

    assertFalse(social.listOfGroups().contains("old"));
    assertTrue(social.listOfGroups().contains("new"));
    assertTrue(social.listOfPeopleInGroup("new").contains("a"));
  }

  @Test
  void updateGroupNameRejectsDuplicateTarget() throws Exception {
    social.addGroup("a");
    social.addGroup("b");
    assertThrows(GroupExistsException.class, () -> social.updateGroupName("a", "b"));
  }

  @Test
  void updateMissingGroupThrows() {
    assertThrows(NoSuchCodeException.class, () -> social.updateGroupName("missing", "x"));
  }
}
