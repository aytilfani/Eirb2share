import java.net.*;
import java.util.*;
import java.io.*;

class TestConnect {
    public static void main(String[] args)  throws Exception {
        // String[] P1={"aaa","bbb","ccc","ddd"};
        // String[] P2={"eeee","ffff","gggg","hhhh","pppp"};
        // String[] P3={"ss","tt","vv"};


        FileInfo file1 = new FileInfo("test1.txt", 3, 4, "4566" );
        FileInfo file2 = new FileInfo("test2.txt", 4, 5, "4577");
        FileInfo file3 = new FileInfo("test3.txt", 2, 3, "4588" );

        FileInfo[] Files={file1,file2,file3};
        String[] buffermap={"1111","11111","100"};
        PeerClient client2 = new PeerClient(Files, buffermap, 8081, "127.0.0.2","./Peer1");
        Socket socket_ = null;
        PrintWriter writer_ = null;
        BufferedReader reader_ = null;
        try {
            socket_ = client2.peerConnect("127.0.0.1", 8080).getSocket();
            writer_ = client2.getSocketInfo(0).getWriter();
            reader_ = client2.getSocketInfo(0).getReader();
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        writer_.println("message\n");
        writer_.flush();

    }
}