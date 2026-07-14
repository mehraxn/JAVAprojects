package prometheusgrafanamonitoring;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class MetricsRegistry {
    private static final double[] DURATION_BUCKETS = {
        0.005, 0.010, 0.025, 0.050, 0.100, 0.250, 0.500, 1.000, 2.500, 5.000
    };

    private final Map<RequestKey, Long> requestCounts = new LinkedHashMap<>();
    private final long[] durationBucketCounts = new long[DURATION_BUCKETS.length];
    private final long startedAtNanos = System.nanoTime();
    private final String version;
    private long durationCount;
    private double durationSum;

    public MetricsRegistry(String version) {
        this.version = version == null || version.isBlank() ? "unknown" : version.trim();
    }

    /**
     * Records one handled request. {@code route} must be a bounded, stable
     * route name ("/", "/health", "/metrics", "/work", or "not_found") —
     * never a raw URL, or every unique path would create a new time series.
     */
    public synchronized void recordRequest(
            String method, String route, int status, double durationSeconds) {
        RequestKey key = new RequestKey(method, route, status);
        requestCounts.put(key, requestCounts.getOrDefault(key, 0L) + 1L);

        durationCount++;
        durationSum += Math.max(0.0, durationSeconds);
        for (int index = 0; index < DURATION_BUCKETS.length; index++) {
            if (durationSeconds <= DURATION_BUCKETS[index]) {
                durationBucketCounts[index]++;
            }
        }
    }

    public synchronized String renderPrometheusText() {
        StringBuilder output = new StringBuilder();
        appendRequestCounter(output);
        appendDurationHistogram(output);
        appendJvmMetrics(output);
        appendUptime(output);
        return output.toString();
    }

    private void appendRequestCounter(StringBuilder output) {
        output.append("# HELP http_requests_total Total HTTP requests handled by the example app.\n");
        output.append("# TYPE http_requests_total counter\n");
        for (Map.Entry<RequestKey, Long> entry : requestCounts.entrySet()) {
            RequestKey key = entry.getKey();
            output.append("http_requests_total{method=\"").append(escapeLabel(key.method))
                    .append("\",route=\"").append(escapeLabel(key.route))
                    .append("\",status=\"").append(key.status).append("\"} ")
                    .append(entry.getValue()).append('\n');
        }
    }

    private void appendDurationHistogram(StringBuilder output) {
        output.append("# HELP http_request_duration_seconds HTTP request duration in seconds.\n");
        output.append("# TYPE http_request_duration_seconds histogram\n");
        for (int index = 0; index < DURATION_BUCKETS.length; index++) {
            output.append("http_request_duration_seconds_bucket{le=\"")
                    .append(DURATION_BUCKETS[index]).append("\"} ")
                    .append(durationBucketCounts[index]).append('\n');
        }
        output.append("http_request_duration_seconds_bucket{le=\"+Inf\"} ")
                .append(durationCount).append('\n');
        output.append("http_request_duration_seconds_sum ").append(durationSum).append('\n');
        output.append("http_request_duration_seconds_count ").append(durationCount).append('\n');
    }

    private void appendJvmMetrics(StringBuilder output) {
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = memory.getHeapMemoryUsage();
        output.append("# HELP jvm_memory_used_bytes Current JVM memory usage.\n");
        output.append("# TYPE jvm_memory_used_bytes gauge\n");
        output.append("jvm_memory_used_bytes{area=\"heap\"} ").append(heap.getUsed()).append('\n');
        output.append("# HELP jvm_memory_max_bytes Maximum JVM memory when defined.\n");
        output.append("# TYPE jvm_memory_max_bytes gauge\n");
        if (heap.getMax() >= 0) {
            output.append("jvm_memory_max_bytes{area=\"heap\"} ").append(heap.getMax()).append('\n');
        }
    }

    private void appendUptime(StringBuilder output) {
        double uptime = (System.nanoTime() - startedAtNanos) / 1_000_000_000.0;
        output.append("# HELP process_uptime_seconds Process uptime in seconds.\n");
        output.append("# TYPE process_uptime_seconds gauge\n");
        output.append("process_uptime_seconds ").append(uptime).append('\n');
        output.append("# HELP app_info Static information about the example application.\n");
        output.append("# TYPE app_info gauge\n");
        output.append("app_info{version=\"").append(escapeLabel(version)).append("\"} 1\n");
    }

    private String escapeLabel(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }

    private static final class RequestKey {
        private final String method;
        private final String route;
        private final int status;

        private RequestKey(String method, String route, int status) {
            this.method = method;
            this.route = route;
            this.status = status;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof RequestKey key)) {
                return false;
            }
            return status == key.status && method.equals(key.method) && route.equals(key.route);
        }

        @Override
        public int hashCode() {
            return Objects.hash(method, route, status);
        }
    }
}
