package csvanalyticsengine;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java csvanalyticsengine.Main <csv-file> [numeric-column] [group-column]");
            return;
        }

        Path inputPath = Paths.get(args[0]);
        try {
            DataSet dataSet = new CsvReader().read(inputPath);
            System.out.println("Columns: " + dataSet.getColumns());
            System.out.println("Row count: " + dataSet.getRowCount());

            AnalyticsService analytics = new AnalyticsService();
            if (args.length > 1 && !dataSet.getColumns().isEmpty()) {
                System.out.println(analytics.calculateStatistics(dataSet, args[1]));
            }
            if (args.length > 2 && !dataSet.getColumns().isEmpty()) {
                Map<String, List<DataRow>> groups = analytics.groupBy(dataSet, args[2]);
                System.out.println("Groups for " + args[2] + ":");
                for (Map.Entry<String, List<DataRow>> group : groups.entrySet()) {
                    System.out.println("  " + group.getKey() + ": " + group.getValue().size());
                }
            }
        } catch (IOException exception) {
            System.err.println("Could not read CSV data: " + exception.getMessage());
        } catch (IllegalArgumentException exception) {
            System.err.println("Invalid request: " + exception.getMessage());
        }
    }
}
