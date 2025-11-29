import java.io.*;

public class CustomLineReader {
    public static void main(String[] args) throws IOException {
        // Simulating a file with 3 lines of text
        String data = "First Line\nSecond Line\r\nThird Line";
        Reader reader = new StringReader(data);

        // Printing the lines using the custom method
        System.out.println(readLine(reader));
        System.out.println(readLine(reader));
        System.out.println(readLine(reader));
    }

    public static String readLine(Reader r) throws IOException {
        StringBuffer res = new StringBuffer(); // Used to build the string 
        int ch = r.read(); // Read first char 
        
        if (ch == -1) return null; // Return null if End of File 
        
        while (ch != -1) {
            char unicode = (char) ch;
            if (unicode == '\n') break; // Stop at newline 
            if (unicode != '\r') res.append(unicode); // Ignore carriage return 
            ch = r.read(); // Read next char
        }
        return res.toString(); // Return the complete line [cite: 108]
    }
}