package blogapi;

public class Main {
    public static void main(String[] args) {
        BlogService service = new BlogService();
        if (args.length > 0 && "server".equalsIgnoreCase(args[0])) {
            startServer(service, args);
            return;
        }

        User author = service.createUser("Ada Author");
        User reader = service.createUser("Rita Reader");
        Post post = service.createPost(author.getId(), "Learning standard Java",
                "A small blog post about collections and services.");
        service.addComment(post.getId(), reader.getId(), "Clear and useful example.");

        System.out.println("Posts:");
        for (Post item : service.listPosts()) {
            System.out.println(item);
        }
        System.out.println("Comments: " + service.listComments(post.getId()).size());
        System.out.println("Search results for 'Ada': " + service.searchPosts("Ada").size());
        System.out.println("Run with 'server [port]' to start the optional HTTP API.");
    }

    private static void startServer(BlogService service, String[] args) {
        int port = 8082;
        try {
            if (args.length > 1) {
                port = Integer.parseInt(args[1]);
            }
            BlogHttpServer server = new BlogHttpServer(service);
            server.start(port);
            System.out.println("Blog API listening on http://localhost:" + port);
            System.out.println("Stop the process with Ctrl+C.");
        } catch (NumberFormatException exception) {
            System.err.println("Port must be a number.");
        } catch (Exception exception) {
            System.err.println("Could not start server: " + exception.getMessage());
        }
    }
}
