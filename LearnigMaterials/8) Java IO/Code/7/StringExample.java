import java.io.StringReader;
import java.io.IOException;

public class StringExample {
    public static void main(String[] args) throws IOException {
        String text = "Java";
        StringReader reader = new StringReader(text);

        // Read the first character ('J')
        int data = reader.read();
        System.out.println("String character: " + (char)data);

        // Always close the reader
        reader.close();
    }
}