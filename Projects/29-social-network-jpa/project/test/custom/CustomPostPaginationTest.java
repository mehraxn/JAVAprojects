package custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class CustomPostPaginationTest extends CustomTestBase {

  /** Posts a message and sleeps briefly so post timestamps are strictly increasing. */
  private String timedPost(String author, String text) throws InterruptedException {
    String id = social.post(author, text);
    Thread.sleep(3);
    return id;
  }

  @Test
  void postAndReadBackContentAndTimestamp() throws Exception {
    social.addPerson("a", "A", "A");
    long before = System.currentTimeMillis();
    String pid = social.post("a", "hello");

    assertNotNull(pid);
    assertEquals("hello", social.getPostContent(pid));
    assertTrue(social.getTimestamp(pid) >= before);
  }

  @Test
  void postByMissingAuthorReturnsNull() {
    assertEquals(null, social.post("ghost", "hi"));
  }

  @Test
  void blankPostFieldsRejected() throws Exception {
    social.addPerson("a", "A", "A");
    assertThrows(IllegalArgumentException.class, () -> social.post("a", "  "));
    assertThrows(IllegalArgumentException.class, () -> social.post(" ", "text"));
  }

  @Test
  void missingPostContentAndTimestamp() {
    assertEquals(null, social.getPostContent("nope"));
    assertEquals(-1L, social.getTimestamp("nope"));
  }

  @Test
  void userPostsPaginatedMostRecentFirst() throws Exception {
    social.addPerson("a", "A", "A");
    timedPost("a", "1");
    timedPost("a", "2");
    timedPost("a", "3");
    timedPost("a", "4");
    String last = timedPost("a", "5");

    List<String> page1 = social.getPaginatedUserPosts("a", 1, 3);
    assertEquals(3, page1.size());
    assertEquals(last, page1.get(0)); // most recent first
  }

  @Test
  void userPostsPaginationSplitsWithoutOverlap() throws Exception {
    social.addPerson("a", "A", "A");
    for (int i = 0; i < 5; i++) {
      timedPost("a", "p" + i);
    }
    List<String> page1 = social.getPaginatedUserPosts("a", 1, 2);
    List<String> page2 = social.getPaginatedUserPosts("a", 2, 2);
    List<String> page3 = social.getPaginatedUserPosts("a", 3, 2);

    assertEquals(2, page1.size());
    assertEquals(2, page2.size());
    assertEquals(1, page3.size());

    Set<String> all = new HashSet<>();
    all.addAll(page1);
    all.addAll(page2);
    all.addAll(page3);
    assertEquals(5, all.size()); // no duplicates across pages
  }

  @Test
  void invalidPaginationRejected() throws Exception {
    social.addPerson("a", "A", "A");
    assertThrows(IllegalArgumentException.class, () -> social.getPaginatedUserPosts("a", 0, 3));
    assertThrows(IllegalArgumentException.class, () -> social.getPaginatedUserPosts("a", 1, 0));
  }

  @Test
  void friendPostsPaginatedWithAuthorPrefix() throws Exception {
    social.addPerson("a", "A", "A");
    social.addPerson("b", "B", "B");
    social.addFriendship("a", "b");
    String b1 = timedPost("b", "fromB1");
    String b2 = timedPost("b", "fromB2");

    List<String> feed = social.getPaginatedFriendPosts("a", 1, 10);
    assertEquals(new ArrayList<>(List.of("b:" + b2, "b:" + b1)), feed); // most recent first
  }

  @Test
  void friendPostsInvalidPaginationRejected() throws Exception {
    social.addPerson("a", "A", "A");
    assertThrows(IllegalArgumentException.class, () -> social.getPaginatedFriendPosts("a", 0, 3));
    assertThrows(IllegalArgumentException.class, () -> social.getPaginatedFriendPosts("a", 1, -1));
  }
}
