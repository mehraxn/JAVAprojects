import java.io.*;

public class CopyTextFile {
    public static void main(String[] args) throws IOException {
        
        // This code requires two file paths passed as arguments
        // Example usage: java CopyTextFile input.txt output.txt
        
        Reader src = new FileReader(args[0]);
        Writer dest = new FileWriter(args[1]);

        int in;
        // Read byte-by-byte (or char-by-char) until End of File (-1)
        while ((in = src.read()) != -1) {
            dest.write(in);
        }

        src.close();
        dest.close();
    }
}