import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;

public class Peer1 {
     public static void main(String[] args) throws Exception {
    // String[] P1 = { "aaa", "bbb", "ccc", "ddd" };
    // String[] P2 = { "eeee", "ffff", "gggg", "hhhh", "pppp" };
    // String[] P3 = { "ss", "tt", "vv" };

    FileInfo file1 = new FileInfo("test3.txt", 53791, 4, "8905e92afeb80fc7722ec89eb0bf0966");
    FileInfo file2 = new FileInfo("test2.txt", 4, 5, "8605m92aghb82fc8122el89eb08f0966");
    FileInfo file3 = new FileInfo("test4.txt", 2, 3, "7904e91afdb89fc7622dc88eb1be0865");

    FileInfo[] Files = { file1, file2, file3 };
    String[] buffermap = { "1111", "11111", "111" };
    PeerClient p = new  PeerClient(Files, buffermap, 2020,"128.68.22.16", "./Peer2");
    

    Socket s = null;
    PrintWriter w = null;
    BufferedReader r = null;

    try {
      s = new Socket(args[1], Integer.parseInt(args[0]));
      w =
        new PrintWriter(
          new BufferedWriter(new OutputStreamWriter(s.getOutputStream())),
          true
        );
      r =
        new BufferedReader(new InputStreamReader(s.getInputStream()));
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println(s);
    Worker w1 = new Worker(p,r,w,s);
    Worker w2 = new Worker(p,r,w,s);
    Worker w3 = new Worker(p,r,w,s);
    w1.set_send(p.announce());
    Thread thread1 = new Thread(w1) ;
    thread1.run();
    //thread1.sleep(100000);
    // w2.set_previous(w1);
    // thread1 = new Thread(w2) ;
    // thread1.run();
    // w3.set_previous(w2);
    // thread1 = new Thread(w3) ;
    // thread1.run();
   
    
    //w.print_read();
    // lancement de ce thread par appel à sa méthode start()
    //w.start();

    // cette méthode rend immédiatement la main...
    System.out.println("Thread lancé");
  }
}
