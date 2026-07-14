package custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class CustomStatisticsAndEndToEndTest extends CustomTestBase {

  @Test
  void emptyDatabaseStatisticsAreNull() {
    assertNull(social.personWithLargestNumberOfFriends());
    assertNull(social.largestGroup());
    assertNull(social.personInLargestNumberOfGroups());
  }

  @Test
  void personWithMostFriends() throws Exception {
    social.addPerson("a", "A", "A");
    social.addPerson("b", "B", "B");
    social.addPerson("c", "C", "C");
    social.addFriendship("a", "b");
    social.addFriendship("a", "c"); // a has 2 friends, b and c have 1 each
    assertEquals("a", social.personWithLargestNumberOfFriends());
  }

  @Test
  void largestGroupIsReturned() throws Exception {
    social.addPerson("a", "A", "A");
    social.addPerson("b", "B", "B");
    social.addGroup("big");
    social.addGroup("small");
    social.addPersonToGroup("a", "big");
    social.addPersonToGroup("b", "big");
    social.addPersonToGroup("a", "small");
    assertEquals("big", social.largestGroup());
  }

  @Test
  void personInMostGroups() throws Exception {
    social.addPerson("a", "A", "A");
    social.addPerson("b", "B", "B");
    social.addGroup("g1");
    social.addGroup("g2");
    social.addPersonToGroup("a", "g1");
    social.addPersonToGroup("a", "g2");
    social.addPersonToGroup("b", "g1");
    assertEquals("a", social.personInLargestNumberOfGroups());
  }

  @Test
  void tieIsBrokenByLexicographicallySmallestCode() throws Exception {
    social.addPerson("zoe", "Z", "Z");
    social.addPerson("amy", "A", "A");
    social.addPerson("x", "X", "X");
    social.addPerson("y", "Y", "Y");
    // amy-x and zoe-y : both amy and zoe have exactly 1 friend -> tie -> smallest code "amy"
    social.addFriendship("amy", "x");
    social.addFriendship("zoe", "y");
    assertEquals("amy", social.personWithLargestNumberOfFriends());
  }

  @Test
  void endToEndWorkflow() throws Exception {
    // people
    social.addPerson("mario", "Mario", "Rossi");
    social.addPerson("elena", "Elena", "Aresti");
    social.addPerson("lupo", "Lupo", "Bianchi");
    // friendships
    social.addFriendship("mario", "elena");
    social.addFriendship("mario", "lupo");
    // groups + membership (mario is in two groups, elena in one)
    social.addGroup("poli");
    social.addGroup("torino");
    social.addPersonToGroup("mario", "poli");
    social.addPersonToGroup("elena", "poli");
    social.addPersonToGroup("mario", "torino");
    // posts by a friend
    String p1 = social.post("elena", "hi from elena");
    Thread.sleep(3);
    String p2 = social.post("lupo", "hi from lupo");

    // queries
    assertEquals("mario Mario Rossi", social.getPerson("mario"));
    assertEquals(2, social.listOfFriends("mario").size());
    assertTrue(social.listOfPeopleInGroup("poli").contains("mario"));
    assertEquals("poli", social.largestGroup());
    assertEquals("mario", social.personInLargestNumberOfGroups());

    List<String> friendFeed = social.getPaginatedFriendPosts("mario", 1, 10);
    assertNotNull(friendFeed);
    assertEquals(2, friendFeed.size());
    assertEquals("lupo:" + p2, friendFeed.get(0)); // most recent first
    assertTrue(friendFeed.contains("elena:" + p1));
  }
}
