package prometheusgrafanamonitoring;

public class Main {
    public static void main(String[] args) {
        MetricsRegistry metrics = new MetricsRegistry();
        metrics.increment("starter_requests_total");
        // TODO: Expose the rendered text from a built-in HttpServer /metrics handler.
        System.out.println("Monitoring starter ready; no server was started.");
        System.out.println(metrics.renderPrometheusText());
    }
}
