package blogapi;

public class Main {
    public static void main(String[] args) {
        BlogService service = new BlogService();
        BlogHttpServer httpServer = new BlogHttpServer(service);
        // TODO: Demonstrate post/comment operations and optional server mode.
        System.out.println("Blog API skeleton ready.");
        System.out.println("HTTP server was not started.");
    }
}
