import java.io.*;
import java.net.*;

public class Client{
    static final int port = 8080;
    public static void main( String[] args) throws Exception{
        Socket socket = new Socket(args[0], port);
        System.out.println("SOCKET = "+ socket);

        BufferedReader plec = new BufferedReader(
                                new InputStreamReader(socket.getInputStream())
                            );

        PrintWriter pred = new PrintWriter(
                            new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream())),
                            true);
        
        String str = "announce listen 2222 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3145728 1536 330a57722ec8b0bf09669a2b35f88e9e]\n";
        for(int i =0; i<10; i++){
            pred.println(str);
            str = plec.readLine();
        }
        System.out.println("END");
        pred.println("END");
        plec.close();
        pred.close();
        socket.close();    

      
    }
}