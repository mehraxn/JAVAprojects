package blogapi;

import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        System.exit(run(args, System.out, System.err));
    }

    public static int run(String[] args, PrintStream out, PrintStream err) {
        if (out == null || err == null) throw new IllegalArgumentException("Output streams cannot be null.");
        if (args == null || args.length == 0) {
            err.println("Missing command. Run 'help' for usage.");
            return 2;
        }
        if ("help".equals(args[0]) && args.length == 1) {
            help(out); return 0;
        }
        if (("demo".equals(args[0]) || "service-demo".equals(args[0])) && args.length == 1) {
            demo(out); return 0;
        }
        if ("server".equals(args[0])) return server(args, out, err);
        err.println("Unknown or invalid command. Run 'help' for usage.");
        return 2;
    }

    private static void help(PrintStream out) {
        out.println("Usage: java -cp out blogapi.Main <command>");
        out.println("Commands: help, demo, service-demo, server <port>");
    }

    private static void demo(PrintStream out) {
        BlogService service = new BlogService();
        User author = service.createUser("Ada Author");
        User reader = service.createUser("Rita Reader");
        Post post = service.createPost(author.getId(), "Learning standard Java",
                "A small blog post about collections and services.");
        service.addComment(post.getId(), reader.getId(), "Clear and useful example.");
        out.println("Created post: " + post);
        out.println("Comments before delete: " + service.listComments(post.getId()).size());
        out.println("Search results: " + service.searchPosts("COLLECTIONS").size());
        Post updated = service.updatePost(post.getId(), "Updated Java post", "Updated content.");
        out.println("Updated post: " + updated);
        service.deletePost(post.getId());
        out.println("Post deleted: " + (service.findPost(post.getId()) == null));
        try {
            service.listComments(post.getId());
        } catch (IllegalArgumentException expected) {
            out.println("Comment cleanup confirmed: " + expected.getMessage());
        }
    }

    private static int server(String[] args, PrintStream out, PrintStream err) {
        if (args.length != 2) {
            err.println("Server command requires one port argument."); return 2;
        }
        final int port;
        try {
            port = Integer.parseInt(args[1]);
            if (port < 1 || port > 65_535) throw new NumberFormatException();
        } catch (NumberFormatException exception) {
            err.println("Port must be a number between 1 and 65535."); return 2;
        }
        BlogHttpServer server = new BlogHttpServer(new BlogService());
        try {
            server.start(port);
            Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
            out.println("Blog API listening on http://localhost:" + port);
            out.println("Stop the process with Ctrl+C.");
            new CountDownLatch(1).await();
            return 0;
        } catch (IOException exception) {
            err.println("Could not start server: " + exception.getMessage()); return 1;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            server.stop(); return 0;
        } catch (IllegalArgumentException | IllegalStateException exception) {
            err.println("Could not start server: " + exception.getMessage()); return 1;
        }
    }
}
