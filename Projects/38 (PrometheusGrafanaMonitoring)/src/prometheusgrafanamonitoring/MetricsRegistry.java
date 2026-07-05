package prometheusgrafanamonitoring;

import java.util.LinkedHashMap;
import java.util.Map;

public class MetricsRegistry {
    private final Map<String, Long> counters = new LinkedHashMap<String, Long>();

    public synchronized void increment(String metricName) {
        String name = validateName(metricName);
        long current = counters.containsKey(name) ? counters.get(name) : 0L;
        if (current == Long.MAX_VALUE) {
            throw new IllegalStateException("Metric counter overflow: " + name);
        }
        counters.put(name, current + 1);
    }

    public synchronized String renderPrometheusText() {
        StringBuilder output = new StringBuilder();
        for (Map.Entry<String, Long> metric : counters.entrySet()) {
            output.append("# TYPE ").append(metric.getKey()).append(" counter\n");
            output.append(metric.getKey()).append(' ').append(metric.getValue()).append('\n');
        }
        // TODO: Add help text and carefully controlled labels.
        return output.toString();
    }

    private String validateName(String metricName) {
        if (metricName == null || !metricName.matches("[a-zA-Z_:][a-zA-Z0-9_:]*")) {
            throw new IllegalArgumentException("Invalid Prometheus metric name.");
        }
        return metricName;
    }
}
