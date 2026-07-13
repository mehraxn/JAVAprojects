import java.io.*;

public class WriterCommands {
    public static void main(String[] args) throws IOException {
        // We use OutputStreamWriter to demonstrate writing to the console
        Writer writer = new OutputStreamWriter(System.out);

        // 1. write(int c): Write a single character (65 is 'A')
        writer.write(65);
        writer.write('\n'); // formatting

        // 2. write(char[] cbuf): Write a full array of characters
        char[] charArray = {'H', 'e', 'l', 'l', 'o'};
        writer.write(charArray);
        writer.write('\n');

        // 3. write(char[] cbuf, int off, int len): Write part of an array
        // Writes 4 characters starting from index 0 ("Hell")
        writer.write(charArray, 0, 4);
        writer.write('\n');

        // 4. write(String str): Write a standard String
        writer.write("Writing a string is easy");
        writer.write('\n');

        // 5. flush(): Force any buffered characters to be written out
        writer.flush();

        // 6. close(): Close the stream and release resources
        writer.close();
    }
}