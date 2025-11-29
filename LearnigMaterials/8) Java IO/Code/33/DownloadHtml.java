import java.net.*;
import java.io.*;

public class DownloadHtml {
    public static void main(String[] args) throws IOException {
        
        URL home = new URL("http://www.google.com");
        URLConnection con = home.openConnection();
        String ctype = con.getContentType();
        
        if (ctype != null && ctype.equals("text/html")) {
            
            Reader r = new InputStreamReader(con.getInputStream());
            Writer w = new OutputStreamWriter(System.out);
            char[] buffer = new char[4096];
            
            while (true) {
                int n = r.read(buffer);
                if (n == -1) break;
                w.write(buffer, 0, n);
            }
            
            r.close(); 
            w.close();
        }
    }
}