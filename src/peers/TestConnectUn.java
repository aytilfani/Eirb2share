import java.net.*;
import java.util.*;
import java.io.*;
// import org.ini4j.InvalidFileFormatException;
// import org.ini4j.Wini;


class TestConnectUn {
    public static void main(String[] args)  throws Exception {
        // String[] P1={"aaa","bbb","ccc","ddd"};
        // String[] P2={"eeee","ffff","gggg","hhhh","pppp"};
        // String[] P3={"ss","tt","vv"};


        FileInfo file1 = new FileInfo("test1.txt", 3, 4, "4566" );
        FileInfo file2 = new FileInfo("test2.txt", 4, 5, "4577" );
        FileInfo file3 = new FileInfo("test3.txt", 2, 3, "4588" );

        FileInfo[] Files={file1,file2,file3};
        String[] buffermap={"1111","11111","100"};
        PeerClient client1 = new PeerClient(Files, buffermap, 8080, "127.0.0.1","./Peer1");
        Socket socket_ = null;
        PrintWriter writer_ = null;
        BufferedReader reader_ = null;
        try {
            socket_ = client1.getServerSocketInfo().getServerSocket();
            writer_ = 
            new PrintWriter(
            new BufferedWriter(new OutputStreamWriter(socket_.getOutputStream())),
            true
            );
            reader_ =
            new BufferedReader(new InputStreamReader(socket_.getInputStream()));
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        String res = "";
        System.out.println(reader_.readLine());
        while((res = reader_.readLine()) != null) {
            System.out.println(res +"\n");
            //break;
            }
        //}
        /*catch (IOException e) {
            e.printStackTrace();
        }*/

        // Wini ini = new Wini(new File("./config.ini"));
        // System.out.println(ini.get("Tracker","tracker-ip"));

    }
}