package blogapi;
import java.time.LocalDateTime;
import java.util.Arrays;
final class BlogJsonTest {
    private BlogJsonTest() { }
    static void run() {
        User u = new User("U-1", "A\"lice\\Admin\nTab\tReturn\rCtrl\u0001Z");
        String user = BlogJson.user(u);
        Assertions.assertContains(user, "\\\"", "quote escaped"); Assertions.assertContains(user, "\\\\", "backslash escaped");
        Assertions.assertContains(user, "\\n", "newline escaped"); Assertions.assertContains(user, "\\t", "tab escaped");
        Assertions.assertContains(user, "\\r", "return escaped"); Assertions.assertContains(user, "\\u0001", "control escaped");
        Assertions.assertFalse(user.contains("\n"), "no raw newline"); Assertions.assertFalse(user.contains("\t"), "no raw tab");
        LocalDateTime now = LocalDateTime.of(2026, 1, 2, 3, 4); Post p = new Post("P-1", u, "Title", "Body", now, now);
        Comment c = new Comment("C-1", "P-1", u, "Nice", now);
        Assertions.assertContains(BlogJson.post(p), "\"title\":\"Title\"", "post JSON");
        Assertions.assertContains(BlogJson.comment(c), "\"postId\":\"P-1\"", "comment JSON");
        Assertions.assertTrue(BlogJson.users(Arrays.asList(u)).startsWith("[{"), "user array");
        Assertions.assertTrue(BlogJson.posts(Arrays.asList(p)).endsWith("]"), "post array");
        Assertions.assertEquals("[]", BlogJson.comments(Arrays.asList()), "empty array");
        Assertions.assertEquals("{\"error\":\"Bad \\\"input\\\".\"}", BlogJson.error("Bad \"input\"."), "error JSON");
    }
}
