import java.net.*;
import java.util.*;
import java.io.*;
class Scenario1P1 {
    public static void main(String[] args) throws Exception {
        System.out.print("Téléchargement de l'image test_1.png\n");
        System.out.print("####################################\n");

        FileInfo file1 = new FileInfo("test1.pdf", 412607, 10, "9942a0cd9518fbd2d311fbdf3e3a2ff6");
        
        FileInfo[] FilesPeer1 = { file1 };

        String[] buffermapPeer1 = { "1111111111" };

        PeerClient p1 = new  PeerClient(FilesPeer1, buffermapPeer1, 2020,"128.68.22.15", "./Peer1");

        Worker w1 =new Worker(p1, p1.getSocket(-1).getReader(), p1.getSocket(-1).getWriter(), p1.getSocket(-1).getSocket());
        Worker w2= new Worker(p1, p1.getSocket(-1).getReader(), p1.getSocket(-1).getWriter(), p1.getSocket(-1).getSocket());
        w1.set_send(p1.announce());
        Thread thread1 = new Thread(w1);
        thread1.run();
     
        w2.set_previous(w1);
        thread1 = new Thread(w2);
        thread1.run();
       
        w1.set_previous(w2);
        thread1 = new Thread(w1);
        thread1.run();

        w2.set_previous(w1);
        thread1 = new Thread(w2);
        thread1.run();
        
        w1 = new Worker(p1, p1.getSocket(0).getReader(), p1.getSocket(0).getWriter(), p1.getSocket(0).getSocket());    
        w1.set_previous(w2);
        thread1 = new Thread(w1);
        thread1.run();
        

    }
}