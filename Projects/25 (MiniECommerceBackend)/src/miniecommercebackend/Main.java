package miniecommercebackend;

public class Main {
    public static void main(String[] args) {
        ShopService service = new ShopService();
        ShopHttpServer httpServer = new ShopHttpServer(service);
        // TODO: Demonstrate catalog/cart logic and optional server mode.
        System.out.println("Mini E-Commerce Backend skeleton ready.");
        System.out.println("HTTP server was not started.");
    }
}
