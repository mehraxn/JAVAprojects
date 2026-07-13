import java.io.*;

public class FileWriteExample {
    public static void main(String[] args) {
        // We use a try-with-resources block to automatically close the file
        // This replaces the manual 'writer.close()' call
        try (Writer writer = new FileWriter("output.txt")) {

            // 1. write(int c): Write 'A'
            writer.write(65);
            writer.write('\n');

            // 2. write(char[] cbuf): Write array
            char[] charArray = {'H', 'e', 'l', 'l', 'o'};
            writer.write(charArray);
            writer.write('\n');

            // 3. write(String str): Write string
            writer.write("Writing to a file is just as easy!");

            // No need to manually call flush() or close() here because
            // the try-with-resources block does it automatically.

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}