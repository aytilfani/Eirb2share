import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;

public class Worker implements Runnable {

  //Les infos utiles pour la connexion du client au tracker
  private Socket socket;
  private BufferedReader reader;
  private PrintWriter writer;
  private String next_send;
  private int to_connect;
  private String send;
  private String receive;
  private PeerClient parent;

  //   Peers parent;
  //   String host;

  Worker(PeerClient p, BufferedReader r, PrintWriter w, Socket s) {
    socket = s;
    reader = r;
    writer = w;
    send = null;
    receive = null;
    to_connect = -1;
    next_send = null;
    parent = p;
  }

  void save_message(String message, String file) {
    // try {
    //   PrintWriter writer_f = new PrintWriter(file, "UTF-8");
    //   writer_f.println(message);
    //   writer_f.close();
    // } catch (IOException e) {
    //   e.printStackTrace();
    // }
        BufferedWriter bufWriter = null;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, true);
            bufWriter = new BufferedWriter(fileWriter);
            //InsÃ©rer un saut de ligne
            bufWriter.newLine();
            bufWriter.write(message);
            bufWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                bufWriter.close();
                fileWriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
  }

  private void read() {
    try {
      String res = "";
      System.out.println(socket);
      System.out.println(this.reader.readLine());
      while ((res = this.reader.readLine()) != null) {
        if (res.trim().equals("")) {
          System.out.println(res + "hello");
          System.out.println("done");
          break;
        } else {
          set_receive(res.trim());
          System.out.println(res + "hello");
          //if(res.equals("OK"))
          this.receive += "\n";
          this.traitement(Parsing.parse(this.receive));
          System.out.println("READ:" + this.receive);
          break;
        }
      }
      this.print_read();
      return;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void write(String message) throws IOException {
   
    System.out.print(socket);
    this.writer.println(message);
    this.writer.flush();

    return;
  }

  // private void write(String message) throws IOException{
  //  socket.write(message);
  //   return;
  // }

  void createLeechFile(String name) {
    try {
      // Recevoir le fichier
      File f = new File(this.parent.getDir() + "/leech/" + name + "data.txt");
      //Log
      /*if (f.createNewFile())
                System.out.println("File created");
            else
                System.out.println("File already exists");
        */
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  public void set_send(String str) {
    this.send = str;
  }

  public void set_receive(String str) {
    this.receive = str;
  }

  public String read_line(String file, int l) {
    int i;
    String line = new String();
    //System.out.println(file);
    
    try {
      //lire le fichier file.txt
      FileReader file_ = new FileReader(file);
      BufferedReader buffer = new BufferedReader(file_);
    
      // parcourir le fichier
      for (i = 1; i < 10; i++) {
        // Si le numéro de la ligne = 5 récupérer la ligne
        if (i == l)
          line = buffer.readLine();
        else
          buffer.readLine();
      }
      } catch (IOException e) {
      e.printStackTrace();
    }
    return line;
  
    // try {
    //   System.out.println(l);
    //   // Le fichier d'entrée
    //   File file_ = new File(file);
    //   // Créer l'objet File Reader
    //   FileReader fr = new FileReader(file_);
    //   // Créer l'objet BufferedReader
    //   BufferedReader br = new BufferedReader(fr);
    //   //StringBuffer sb = new StringBuffer();
    //   String line;
    //   int n = 0;
    //   while (n != l) {
    //     if ((line = br.readLine()) == null) {
    //       System.out.println("Problème lors de la lecture du fichier");
    //       return null;
    //     }
    //     n++;
    //   }
    //   line = br.readLine();
    //   fr.close();
    //   return (line);
    // } catch (IOException e) {
    //   e.printStackTrace();
    //   return null;
    // }
  }

  void init_tab(int[] tab) {
    for (int i = 0; i < tab.length; i++) {
      tab[i] = 0;
    }
  }

  String zeros(int nb) {
    String res = "";
    for (int i = 0; i < nb; i++) {
      res += "0";
    }
    return res;
  }

  public String[] get_best(String[] buffers) {
    int nb_parties = buffers[0].length();
    int nb_peers = buffers.length;
    int[] exist = new int[nb_parties];
    int[] res = new int[nb_peers];
    init_tab(exist);
    init_tab(res);
    for (int i = 0; i < nb_parties; i++) {
      for (int j = 0; j < nb_peers; i++) {
        if (buffers[j].charAt(i) == 1) {
          exist[i] += Math.pow(10, j);
        }
      }
    }
    for (int i = 0; i < nb_parties; i++) {
      for (int j = 0; j < nb_peers; i++) {
        if (exist[i] == Math.pow(10, j)) {
          res[j] += Math.pow(10, i);
        } else {
          int index = Integer.toString(exist[i]).length() - 1;
          res[index] += Math.pow(10, i);
        }
      }
    }
    String[] finale = new String[nb_peers];
    for (int i = 0; i < nb_peers; i++) {
      finale[i] = Integer.toString(res[i]);
      if (finale[i].length() != nb_parties) {
        finale[i] = zeros(nb_parties - finale[i].length()) + finale[i];
      }
    }
    return finale;
  }

  public void connect(String[] tab) throws IOException {
    System.out.println("in connect\n");
        for (int i = 0; i < tab.length; i++) {
        System.out.println(tab[i].trim().split(":")[0]+" and "+ Integer.parseInt(tab[i].trim().split(":")[1]));
      this.parent.peerConnect(
          tab[i].trim().split(":")[0],
          Integer.parseInt(tab[i].trim().split(":")[1])
        );
    }
  }

  public void set_previous(Worker previous) throws IOException {
    //this.receive = previous.next_receive;
    String str = previous.next_send;
    System.out.println(previous.next_send);
    System.out.println(str);
    String[] tab = str.trim().split(" ");
    if (str.equals("done\n")) {
      //il faut modifier l'emplacement du fichier pour le mettre dans seed au lieu de leach et supprimer le leach
      System.out.println("recieve\n");
      return;
    } else if (str.equals("not found\n")) {
      System.out.println("Le fichier n'existe pas \n");
      return;
    }else if (tab[0].equals("Wait")){
      this.parent.getServerSocketInfo().getServerSocket();
      send = "";
    } else if (tab[0].equals("connect")) {
      String tmp = "";
      int i = 0;
      while (  
        !((tmp = read_line(this.parent.getDir() + "/leech/" + tab[1] + "data.txt", i)).equals("contact"))) {
        if(tmp == null){
          System.out.println("null\n");
          break;
        }
        //System.out.println(tmp);
        System.out.println(tmp);
        i++;
      }
      i++;
      int nb = 0;
      while (
        !((tmp = read_line(this.parent.getDir() + "/leech/" + tab[1] + "data.txt", i)).equals("end_contact"))) {
         if(tmp == null){
          System.out.println("null\n");
          break;
        }
        nb++;
        i++;
      }
      System.out.println("n="+nb);
      String[] access = new String[nb-1];
      i = i - nb;
      for (int j =1; j<nb; j++){
        access[j-1] =read_line(this.parent.getDir() + "/leech/" + tab[1] + "data.txt",i + j);
        System.out.println(access[j-1]);
      }
      
      save_message(
        "total_connect" + Integer.toString(nb),
        this.parent.getDir() + "/leech/" + tab[1] + "data.txt"
      );
      this.connect(access);
      this.send = "interested";
      //this.kvjflk
    } else if (tab[0].equals("waiting")) {
      int i = 0;
      int nb_total = 0;
      int nb = 0;
      while (
      read_line(this.parent.getDir() + "/leech/" + tab[1] + "data.txt", i) !=
        "total_connect"
      ) {
        i++;
      }
      nb_total =
        Integer.parseInt(
          read_line(
            this.parent.getDir() + "/leech/" + tab[1] + "data.txt",
            i + 1
          )
        );
      String[] buffer = new String[nb_total];
      String[] peers = new String[nb_total];
      i = 0;
      while (
        read_line(this.parent.getDir() + "/leech/" + tab[1] + "data.txt", i) !=
        "buffer maps"
      ) {
        i++;
      }
      while (
        read_line(this.parent.getDir() + "/leech/" + tab[1] + "data.txt", i) ==
        "buffer maps"
      ) {
        i++;
        peers[nb] =
          read_line(this.parent.getDir() + "/leech/" + tab[1] + "data.txt", i);
        i++;
        buffer[nb] =
          read_line(this.parent.getDir() + "/leech/" + tab[1] + "data.txt", i);
        nb++;
        i++;
      }
      if (nb != nb_total) {
        this.next_send = "waiting";
        this.send = null;
        return;
      } else {
        String[] get = get_best(buffer);
        //this.send = this.parent.getPieces(peers,get);
      }
    }
    this.send = previous.next_send;
  }

  public void print_read() {
    System.out.println("here:" + this.receive);
  }

  String traitement(String[] tokens) throws IOException {
    switch (tokens[0]) {
      case "OK":
        this.next_send = this.parent.look("test3.txt", null);
        System.out.println(this.next_send);
        this.to_connect = 0;
        break;
      case "list":
        if (tokens.length > 1) {
          this.createLeechFile(tokens[4]);
          this.next_send = this.parent.getfile(tokens[4]);
          this.to_connect = 0;
        } else {
          this.next_send = "not found\n";
        }
        break;
      case "peers":
        System.out.println("we have Peers\n");
        //peers $Key [$IP1:$Port1 $IP2:$Port2 …]
        this.next_send = "connect " + tokens[1];
        this.to_connect = 1;
        save_message(
          "contact",
          this.parent.getDir() + "/leech/" + tokens[1] + "data.txt"
        );
        save_message(
          Integer.toString(tokens.length - 2),
          this.parent.getDir() + "/leech/" + tokens[1] + "data.txt"
        );
        for (int i = 2; i < tokens.length; i++) save_message(
          tokens[i],
          this.parent.getDir() + "/leech/" + tokens[1] + "data.txt"
        );
        save_message(
          "end_contact",
          this.parent.getDir() + "/leech/" + tokens[1] + "data.txt"
        );
        break;
      case "interested":
        this.next_send = new String(this.parent.have(tokens[1]));
        this.to_connect = 1;
        break;
      case "have":
        //have $Key $BufferMap
        save_message(
          "buffer maps",
          this.parent.getDir() + "/leech/" + tokens[1] + "data.txt"
        );
        save_message(
          socket.getInetAddress().getHostAddress() +
          ":" +
          Integer.toString(socket.getPort()),
          this.parent.getDir() + "/leech/" + tokens[1] + "data.txt"
        );
        save_message(
          tokens[2],
          this.parent.getDir() + "/leech/" + tokens[1] + "data.txt"
        );
        //save_message("end_buffer",this.parent.getDir()+"/leech/"+tokens[1]+"data.txt")
        this.next_send = "waiting";
        this.to_connect = 1;
        break;
      case "getpieces":
        int[] tab = {};
        for (int i = 0; i < tokens.length - 2; i++) {
          tab[i] = Integer.parseInt(tokens[i + 2]);
        }
        this.next_send = new String(this.parent.data(tokens[1], tab));
        this.to_connect = 1;
        break;
      case "data":
        this.next_send = "done";
        for (int i = 2; i < tokens.length; i += 2) System.out.println(
          tokens[i]
        );
        this.to_connect = 1;
        break;
    }
    return "";
  }

  public void run() {
    String[] tab = send.trim().split(" ");
    if(tab[0].equals("connect")){
     return;
    }
    if (send != "" ) {
      System.out.println("SEND: " + send);
      try {
        System.out.println("dans le try write\n");
        this.write(send);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try {
       System.out.println("dans le try read\n");
      String res = "";
      System.out.println(socket);
      //System.out.println(this.reader.readLine());
      while ((res = this.reader.readLine()) != null) {
        if (res.trim().equals("")) {
          System.out.println("done");
          break;
        } else {
          set_receive(res.trim());
          //if(res.equals("OK"))
          this.receive += "\n";
          this.traitement(Parsing.parse(this.receive));
          System.out.println("READ:" + this.receive);
          break;
        }
      }
      this.print_read();
      return;
    } catch (IOException e) {
      e.printStackTrace();
    }

    return;
  }

  public static void main(String[] args) throws Exception {
    FileInfo file1 = new FileInfo(
      "test1.txt",
      3,
      4,
      "8905e92afeb80fc7722ec89eb0bf0966"
    );
    FileInfo file2 = new FileInfo(
      "test2.txt",
      4,
      5,
      "8605m92aghb82fc8122el89eb08f0966"
    );
    FileInfo file3 = new FileInfo(
      "test3.png",
      2,
      3,
      "7904e91afdb89fc7622dc88eb1be0865"
    );

    FileInfo[] Files = { file1, file2, file3 };
    String[] buffermap = { "1111", "11111", "111" };
    PeerClient p = new PeerClient(
      Files,
      buffermap,
      2020,
      "128.68.22.15",
      "./Peer1"
    );

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
      r = new BufferedReader(new InputStreamReader(s.getInputStream()));
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println(s);
    Worker w1 = new Worker(p, r, w, s);
    Worker w2 = new Worker(p, r, w, s);
    Worker w3 = new Worker(p, r, w, s);
    w1.set_send(p.announce());
    Thread thread1 = new Thread(w1);
    thread1.run();
    //thread1.sleep(100000);
    w2.set_previous(w1);
    thread1 = new Thread(w2);
    thread1.run();
    w3.set_previous(w2);
    thread1 = new Thread(w3);
    thread1.run();

    //w.print_read();
    // lancement de ce thread par appel à sa méthode start()
    //w.start();

    // cette méthode rend immédiatement la main...
    System.out.println("Thread lancé");
  }
}
