import java.io.*;

public class ReaderCommands {
    public static void main(String[] args) throws IOException {
        String text = "ABCDE";
        Reader reader = new StringReader(text);

        // 1. ready(): Check if we can read
        if (reader.ready()) {
            System.out.println("Stream is ready.");
        }

        // 2. skip(n): Jump over the first 2 characters ('A' and 'B')
        reader.skip(2);
        System.out.println("Read: " + (char)reader.read()); // Prints 'C'

        // 3. reset(): Go back to the beginning
        reader.reset();
        System.out.println("Read: " + (char)reader.read()); // Prints 'A'
    }
}