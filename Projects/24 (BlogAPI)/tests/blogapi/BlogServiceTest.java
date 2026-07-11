package blogapi;
import java.util.List;
final class BlogServiceTest {
    private BlogServiceTest() { }
    static void run() {
        BlogService s = new BlogService(); User a = s.createUser("Alice"); User b = s.createUser("Bob");
        Assertions.assertEquals("U-1", a.getId(), "create user"); Assertions.assertEquals(2, s.listUsers().size(), "list users");
        Assertions.assertEquals("Alice", s.findUser(a.getId()).getName(), "find user"); Assertions.assertNull(s.findUser("missing"), "missing user lookup");
        Assertions.assertThrows(IllegalArgumentException.class, () -> s.createUser("alice"), "duplicate user");
        Assertions.assertThrows(IllegalArgumentException.class, () -> s.createUser(" "), "blank user");
        Post p = s.createPost(a.getId(), "Java Collections", "Maps are useful backend tools.");
        Assertions.assertEquals("P-1", p.getId(), "create post"); Assertions.assertEquals(1, s.listPosts().size(), "list posts");
        Assertions.assertThrows(IllegalArgumentException.class, () -> s.createPost("missing", "T", "B"), "missing post author");
        Assertions.assertThrows(IllegalArgumentException.class, () -> s.createPost(a.getId(), "", "B"), "blank post title");
        Post updated = s.updatePost(p.getId(), "Updated", "Fresh content"); Assertions.assertEquals("Updated", updated.getTitle(), "update post");
        Assertions.assertNull(s.updatePost("missing", "T", "B"), "update missing post");
        Comment c = s.addComment(p.getId(), b.getId(), "Nice post"); Assertions.assertEquals("C-1", c.getId(), "create comment");
        Assertions.assertEquals(1, s.listComments(p.getId()).size(), "list comments");
        Assertions.assertThrows(IllegalArgumentException.class, () -> s.addComment("missing", b.getId(), "x"), "comment missing post");
        Assertions.assertThrows(IllegalArgumentException.class, () -> s.addComment(p.getId(), "missing", "x"), "comment missing author");
        Assertions.assertThrows(IllegalArgumentException.class, () -> s.addComment(p.getId(), b.getId(), " "), "blank comment");
        Assertions.assertEquals(1, s.searchPosts("UPDATED").size(), "case-insensitive title search");
        Assertions.assertEquals(1, s.searchPosts("fresh CONTENT").size(), "content search");
        Assertions.assertEquals(1, s.searchPosts("alice").size(), "author search");
        Assertions.assertThrows(IllegalArgumentException.class, () -> s.searchPosts(" "), "empty search documented as invalid");
        List<Post> posts = s.listPosts(); Assertions.assertThrows(UnsupportedOperationException.class, posts::clear, "post list unmodifiable");
        List<Comment> comments = s.listComments(p.getId()); Assertions.assertThrows(UnsupportedOperationException.class, comments::clear, "comment list unmodifiable");
        List<User> users = s.listUsers(); Assertions.assertThrows(UnsupportedOperationException.class, users::clear, "user list unmodifiable");
        Post external = s.findPost(p.getId()); external.update("External", "Mutation");
        Assertions.assertEquals("Updated", s.findPost(p.getId()).getTitle(), "post defensive copy");
        Assertions.assertTrue(s.deletePost(p.getId()), "delete post"); Assertions.assertNull(s.findPost(p.getId()), "deleted post absent");
        Assertions.assertThrows(IllegalArgumentException.class, () -> s.listComments(p.getId()), "comments hidden after delete");
        Assertions.assertFalse(s.deletePost(p.getId()), "delete missing post");
    }
}
