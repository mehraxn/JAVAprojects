package csvanalyticsengine;

public class Main {
    public static void main(String[] args) {
        CsvReader reader = new CsvReader();
        CsvWriter writer = new CsvWriter();
        AnalyticsService analytics = new AnalyticsService();
        // TODO: Accept an input path and demonstrate validated analytics.
        System.out.println("CSV Analytics Engine skeleton ready.");
        System.out.println("No file was read or written.");
    }
}
