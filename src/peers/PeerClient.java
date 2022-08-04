import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class PeerClient extends PeerServer{

  private FileInfo[] files;
  private String[] buffermap;
  private Socket_t tracker_socket;
  private List<Socket_t> peer_sockets;
  private String dir;

  // PeerClient(FileInfo[] files, String[] buffermap, int server_port, String server_ip, String dir) throws IOException {
  //   super(server_port, server_ip);
  //   this.files = files;
  //   this.buffermap = buffermap;
  //   //this.tracker_socket = new Socket_t(s,w,r); //tracker ip and port to add, config.ini
  //   this.peer_sockets = new ArrayList<>();
  //   this.dir = dir;
  // }

  PeerClient(FileInfo[] files, String[] buffermap, int server_port, String server_ip, String dir) throws IOException {
    super(server_port, server_ip);
    this.files = files;
    this.buffermap = buffermap;
    this.tracker_socket = new Socket_t(8080, "127.0.0.1"); //tracker ip and port to add, config.ini
    this.peer_sockets = new ArrayList<>();
    this.dir = dir;
  }

  Socket_t getSocket( int i) {
    if(i==-1)
      return this.tracker_socket;
    else 
      return this.peer_sockets.get(i);
  }

//   BufferedReader getReader() throws IOException{
//     return(new BufferedReader(new InputStreamReader(this.tracker_socket.getInputStream())));
//   }
// PrintWriter  getWriter() throws IOException{
//     return(new PrintWriter(
//           new BufferedWriter(new OutputStreamWriter(tracker_socket.getOutputStream())),
//           true
//         ));
//   }

  String getDir() {
    return this.dir;
  }

  Socket_t peerConnect(String ip_address, int port) throws IOException{
    //Socket_t socket = new Socket_t(null,null,null);
    Socket_t socket = new Socket_t(port, ip_address);
    this.peer_sockets.add(socket);
    return this.peer_sockets.get(this.peer_sockets.size() - 1);
  }

  Socket_t peerConnect(PeerClient p) throws IOException{
    //Socket_t socket = new Socket_t(null,null,null);
    Socket_t socket = new Socket_t( p.getPort(), p.getIpAddress());
    this.peer_sockets.add(socket);
    return this.peer_sockets.get(this.peer_sockets.size() - 1);
  }


  // Socket_t getSocketInfo(int index) {
  //   return this.peer_sockets.get(index);
  // }
  
  public Set<String> listFiles(String dir) throws IOException {
    Set<String> fileList = new HashSet<>();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
        for (Path path : stream) {
            if (!Files.isDirectory(path)) {
                fileList.add(path.getFileName()
                    .toString());
            }
        }
    }
    return fileList;
}
String announce() throws IOException {
  Set<String> seedFileNames = listFiles(dir+"/seed");
  Set<String> leechFileNames = listFiles(dir + "/leech");
  List<FileInfo> seed_f = new ArrayList<>();
  List<FileInfo> leech_f = new ArrayList<>();
  String seed = "";
  String leech = "";
  for (String seedFile : seedFileNames ) {
    for(int i=0; i<this.files.length ; i++){
      if (this.files[i].getName().equals(seedFile)) {
        seed_f.add(this.files[i]);
        //seed += this.files[i].getName() + " "+  this.files[i].getLength()+" "+  this.files[i].getPieceSize()+" "+ this.files[i].getKey() +" ";
      } 
    }
  }
  for (String leechFile : leechFileNames ) {
    for(int i=0; i<this.files.length ; i++){
      if (this.files[i].getName().equals(leechFile)) {
        leech_f.add(this.files[i]);
        //leech += this.files[i].getKey()+" ";
      }
    }
  }

  for (int i = 0; i < seed_f.size() - 1; i++) {
    seed +=
      seed_f.get(i).getName() +
      " " +
      seed_f.get(i).getLength() +
      " " +
      seed_f.get(i).getPieceSize() +
      " " +
      seed_f.get(i).getKey() +
      " ";
  }
  seed +=
    seed_f.get(seed_f.size() - 1).getName() +
    " " +
    seed_f.get(seed_f.size() - 1).getLength() +
    " " +
    seed_f.get(seed_f.size() - 1).getPieceSize() +
    " " +
    seed_f.get(seed_f.size() - 1).getKey();

  if(leech_f.size()==0){
    return (
    "announce listen " + getPort() + " seed [" + seed + "]\n"
    );
  }
  for (int i = 0; i < leech_f.size() - 1; i++) {
    leech += leech_f.get(i).getKey() + " ";
  }

  
  leech += leech_f.get(leech_f.size() - 1).getKey();

  return (
    "announce listen " + getPort() + " seed [" + seed + "] leech [" + leech + "]\n"
  );
}

  String look(String filename, String filesize) {
    if (filename == null) {
      if (filesize == null) {
        System.exit(-1);
      } else {
        return "look [filesize>”" + filesize + "”]\n";
      }
    } else {
      if (filesize == null) {
        return "look [filename=”" + filename + "”]\n";
      } else {
        return (
          "look [filename=”" + filename + "”, filesize>”" + filesize + "”]\n"
        );
      }
    }
    return "";
  }

  String getfile(String hash) {
    String comm = new String();
    comm ="getfile " + hash + "\n";
    System.out.print("< getfile" + hash + "\n");
    return comm;
  }

  

  boolean seedOrNot(FileInfo file, int i) {
    int j = 0;
    while (j < file.getNbrPieces()) {
      if (buffermap[i].charAt(j) == '0') {
        return false;
      }
      j++;
    }
    return true;
  }

  String update() throws IOException {
    Set<String> seedFileNames = listFiles(dir + "/seed");
    Set<String> leechFileNames = listFiles(dir + "/leech");
    List<FileInfo> seed_f = new ArrayList<>();
    List<FileInfo> leech_f = new ArrayList<>();
    String seed = "";
    String leech = "";
    for (String seedFile : seedFileNames ) {
      for(int i=0; i<this.files.length ; i++){
        if (this.files[i].getName().equals(seedFile)) {
          seed_f.add(this.files[i]);
          //seed += this.files[i].getName() + " "+  this.files[i].getLength()+" "+  this.files[i].getPieceSize()+" "+ this.files[i].getKey() +" ";
        } 
       }
    }
    for (String leechFile : leechFileNames ) {
      for(int i=0; i<this.files.length ; i++){
        if (this.files[i].getName().equals(leechFile)) {
          leech_f.add(this.files[i]);
          //leech += this.files[i].getKey()+" ";
        }
      }
    }

    for (int i = 0; i < seed_f.size() - 1; i++) {
      seed +=
        seed_f.get(i).getName() +
        " " +
        seed_f.get(i).getLength() +
        " " +
        seed_f.get(i).getPieceSize() +
        " " +
        seed_f.get(i).getKey() +
        " ";
    }
    for (int i = 0; i < leech_f.size() - 1; i++) {
      leech += leech_f.get(i).getKey() + " ";
    }

    seed +=
      seed_f.get(seed_f.size() - 1).getName() +
      " " +
      seed_f.get(seed_f.size() - 1).getLength() +
      " " +
      seed_f.get(seed_f.size() - 1).getPieceSize() +
      " " +
      seed_f.get(seed_f.size() - 1).getKey();

    leech += leech_f.get(leech_f.size() - 1).getKey();

    return (
      "update seed [" + seed + "] leech [" + leech + "]\n"
    );
  }

  // Peer-Peer
  byte[] interesed(String key) {
    String str = "interested " + key+"\n";

    return str.getBytes();
  }

  byte[] getpieces(String key, int[] piecesIndex) {
    String comm = "getpieces " + key + " [";
    int i = 0;
    for (i = 0; i < piecesIndex.length-1; i++) {
      comm = comm + piecesIndex[i] + " ";
    }
    comm = comm + piecesIndex[piecesIndex.length-1]+"]\n";
    //System.out.print("<" + comm);
    return comm.getBytes();
  }
    byte[] have(String hash) {
    String comm = "have " + hash + " ";
    for (int i = 0; i < this.files.length; i++) {
      if (hash == this.files[i].getKey()) {
        comm = comm + this.buffermap[i] + "\n";
        return comm.getBytes();
      }
    }
    return "".getBytes();
    
  }

  byte[] data(String key, int[] pieces) throws IOException {
    int i;
    String indexAndPieces = "";
    FileInfo file=null;
    int index=0;
    String filename="";
 
    for (i = 0; i < this.files.length; i++) {
      if (this.files[i].getKey().equals(key)) {
        file=this.files[i];
        index=i;
        break;
      }
    }

    if(seedOrNot(file, index)){
      filename=dir+"/seed/"+file.getName();
    }else{
      filename = dir+"/leech/"+file.getName();
    }

   for (int j = 0; j < pieces.length; j++) {
      if(j==pieces.length-1){
        indexAndPieces =
        indexAndPieces + pieces[j] +":"+ getLines(getPraties(filename,pieces[j]));
      }
      else{
        indexAndPieces =
        indexAndPieces + pieces[j] +":"+ getLines(getPraties(filename,pieces[j]))+" ";
      }
    }
    String str = "data " + key + " [" + indexAndPieces + "]\n";
    return str.getBytes();
  }

  List<String> getPraties(String filename, int indexPartie) throws IOException{
    String line="";
    List<String> lines= new ArrayList<String>();
    boolean find = false;
    /*for(int i=indexPartie*5; i<indexPartie*5+5; i++){
      line=  Files.readAllLines(Paths.get(filename)).get(i);
      lines.add(line);
    }*/
    int i=0;
    int lineNumber=0;
    while(!find){
      line=  Files.readAllLines(Paths.get(filename)).get(i);
      if(line.equals(Integer.toString(indexPartie))){
        find=true;
        lineNumber=i+1;
        break;
      }
      i++;
    }
    for(int k=lineNumber; k<lineNumber+5; k++){
      line=  Files.readAllLines(Paths.get(filename)).get(k);
      lines.add(line);
    }
    return lines;
  }

  void writePart(String filename, int indexPart, List<String> part){
    try{
      File f=new File(filename);
      FileWriter fw = new FileWriter(f,true);
      BufferedWriter bw = new BufferedWriter(fw);
      LineNumberReader lnr = new LineNumberReader(new FileReader(f));
      lnr.setLineNumber(indexPart*5);
      bw.write(Integer.toString(indexPart));
      bw.newLine();
      for(int i=0; i<part.size(); i++){
        bw.write(part.get(i));
        bw.newLine();
      }
      bw.close();
      lnr.close();
    } 
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  String getLines(List<String> lines){
    String str="";
    for(int i=0; i<lines.size(); i++){
      str+=lines.get(i);
    }
    return str;
  }

  
}
