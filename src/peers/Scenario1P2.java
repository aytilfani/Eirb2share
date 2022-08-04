import java.net.*;
import java.util.*;
import java.io.*;
class Scenario1P2 {
    public static void main(String[] args) throws Exception {
        System.out.print("Téléchargement de l'image test_1.png\n");
        System.out.print("####################################\n");

        FileInfo file2 = new FileInfo("test2.jpeg", 15798, 5, "a2b301e52c435e39ffd78c94c3c639c2");
        FileInfo file3 = new FileInfo("test3.txt", 53791, 3, "76c79dd1bcedb0a55709c3d8053f7502");
        FileInfo file4 = new FileInfo("test3.jpg", 76951, 5, "671bc06a35ee10685ab98fc6e49294e7");

    
        FileInfo[] FilesPeer2 = { file2, file3, file4 };
        
        String[] buffermapPeer2 = { "11111", "111", "11111" };

        PeerClient p2 = new  PeerClient(FilesPeer2, buffermapPeer2, 2021,"128.68.22.16", "./Peer2");

        //p1.peerConnect(p2);
        

        Worker w1 = new Worker(p2, p2.getSocket(-1).getReader(), p2.getSocket(-1).getWriter(), p2.getSocket(-1).getSocket());
        w1.set_send(p2.announce());
        Thread thread1 = new Thread(w1);
        thread1.run();
        
        p2.getSocket(-1).close();

        Thread.sleep(60000);
        p2.peerConnect("127.0.0.1", 2020);
        w1 = new Worker(p2, p2.getSocket(0).getReader(), p2.getSocket(0).getWriter(), p2.getSocket(0).getSocket());
        w1.set_send("Wait");
        thread1 = new Thread(w1);
        thread1.run();
        Worker w2 = new Worker(p2, p2.getServerSocketInfo().getReader(), p2.getServerSocketInfo().getWriter(), p2.getServerSocketInfo().getServerSocket());
        w2.set_previous(w1);
        thread1 = new Thread(w2);
        thread1.run();

    }
}