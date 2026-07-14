package blogapi;
import java.time.LocalDateTime;
final class DomainModelTest {
    private DomainModelTest() { }
    static void run() {
        User u = new User(" U-1 ", " Alice ");
        Assertions.assertEquals("U-1", u.getId(), "user ID"); Assertions.assertEquals("Alice", u.getName(), "user name");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new User("U", null), "null user name");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new User("U", " "), "blank user name");
        LocalDateTime now = LocalDateTime.now(); Post p = new Post("P-1", u, " Title ", " Body ", now, now);
        Assertions.assertEquals("P-1", p.getId(), "post ID"); Assertions.assertEquals("Title", p.getTitle(), "post title");
        Assertions.assertEquals("Body", p.getContent(), "post content"); Assertions.assertEquals("U-1", p.getAuthor().getId(), "post author");
        p.update("Changed", "New body"); Assertions.assertEquals("Changed", p.getTitle(), "updated title"); Assertions.assertEquals("New body", p.getContent(), "updated content");
        Assertions.assertThrows(IllegalArgumentException.class, () -> p.update("", "body"), "invalid post update");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Post("P", u, null, "body", now, now), "null title");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Post("P", u, "title", " ", now, now), "blank content");
        Comment c = new Comment("C-1", "P-1", u, " Nice ", now);
        Assertions.assertEquals("C-1", c.getId(), "comment ID"); Assertions.assertEquals("P-1", c.getPostId(), "comment post");
        Assertions.assertEquals("U-1", c.getAuthor().getId(), "comment author"); Assertions.assertEquals("Nice", c.getBody(), "comment body");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Comment("C", "P", u, null, now), "null comment");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Comment("C", "P", u, " ", now), "blank comment");
    }
}
