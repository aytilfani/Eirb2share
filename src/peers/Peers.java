//import java.net.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


class Peers {

  private String ip_address; //TEMP
  private int port;
  private FileInfo[] files;
  private String[] buffermap;
  private String dir;

  Peers(String ip_address, int port, FileInfo[] files, String[] buffermap, String Dir) {
    this.ip_address = ip_address;
    this.port = port;
    this.files = files;
    this.buffermap = buffermap;
    this.dir= Dir;
  }

  public Set<String> listFiles(String dir) throws IOException {
    Set<String> fileList = new HashSet<>();
    try (
      DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))
    ) {
      for (Path path : stream) {
        if (!Files.isDirectory(path)) {
          fileList.add(path.getFileName().toString());
        }
      }
    }
    return fileList;
  }

  String getIpAddress() {
    return this.ip_address;
  }

  int getPort() {
    return this.port;
  }

  String getDir() {
    return this.dir;
  }

  String announce() throws IOException {
    Set<String> seedFileNames = listFiles(dir+"/seed");
    Set<String> leechFileNames = listFiles(dir+"/leech");
    List<FileInfo> seed_f = new ArrayList<>();
    List<FileInfo> leech_f = new ArrayList<>();
    String seed = "";
    String leech = "";
    for (String seedFile : seedFileNames) {
      for (int i = 0; i < this.files.length; i++) {
        if (this.files[i].getName().equals(seedFile)) {
          seed_f.add(this.files[i]);
          //seed += this.files[i].getName() + " "+  this.files[i].getLength()+" "+  this.files[i].getPieceSize()+" "+ this.files[i].getKey() +" ";
        }
      }
    }
    for (String leechFile : leechFileNames) {
      for (int i = 0; i < this.files.length; i++) {
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

    if (leech_f.size() == 0) {
      return ("announce listen " + port + " seed [" + seed + "]\n");
    }
    for (int i = 0; i < leech_f.size() - 1; i++) {
      leech += leech_f.get(i).getKey() + " ";
    }

    leech += leech_f.get(leech_f.size() - 1).getKey();

    return (
      "announce listen " + port + " seed [" + seed + "] leech [" + leech + "]\n"
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
    comm = "getfile " + hash + "\n";
    //System.out.print("< getfile" + hash);
    return comm;
  }

  String update() throws IOException {
    Set<String> seedFileNames = listFiles(dir+"/seed");
    Set<String> leechFileNames = listFiles(dir+"/leech");
    List<FileInfo> seed_f = new ArrayList<>();
    List<FileInfo> leech_f = new ArrayList<>();
    String seed = "";
    String leech = "";
    for (String seedFile : seedFileNames) {
      for (int i = 0; i < this.files.length; i++) {
        if (this.files[i].getName().equals(seedFile)) {
          seed_f.add(this.files[i]);
          //seed += this.files[i].getName() + " "+  this.files[i].getLength()+" "+  this.files[i].getPieceSize()+" "+ this.files[i].getKey() +" ";
        }
      }
    }
    for (String leechFile : leechFileNames) {
      for (int i = 0; i < this.files.length; i++) {
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

    return ("update seed [" + seed + "] leech [" + leech + "]\n");
  }

  byte[] have(String hash) {
    String comm = "have " + hash + " ";
    //String buff = "";
    for (int i = 0; i < this.files.length; i++) {
      if (hash == this.files[i].getKey()) {
        comm = comm + this.buffermap[i] + "\n";
        return comm.getBytes();
      }
    }
    return "".getBytes();
  }

  // Peer-Peer
  byte[] interesed(String key) {
    String str = "interested " + key + "\n";

    return str.getBytes();
  }
  
  byte[] getpieces(String key, int[] piecesIndex) {
    String comm = "getpieces " + key + " [";
    int i = 0;
    for (i = 0; i < piecesIndex.length - 1; i++) {
      comm = comm + piecesIndex[i] + " ";
    }
    comm = comm + piecesIndex[piecesIndex.length - 1] + "]\n";
    //System.out.print("<" + comm);
    return comm.getBytes();
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

  byte[] getPraties(String filename, int indexPartie) throws IOException {
    RandomAccessFile randomAccessFile = new RandomAccessFile(filename, "rw");

    byte[] dest = new byte[10298];
    System.out.println(new String(dest));

    randomAccessFile.seek(indexPartie * 10298);
    int t = randomAccessFile.read(dest);

    randomAccessFile.close();
    //System.out.println(new String(dest));
    return dest;
  }

  void writePart(String filename, int indexPart, byte[] part)
    throws IOException {
    RandomAccessFile file = new RandomAccessFile(filename, "rw");
    file.seek(indexPart * 10298);
    file.write(part);
    file.close();
  }

  String getLines(List<String> lines) {
    String str = "";
    for (int i = 0; i < lines.size(); i++) {
      str += lines.get(i);
    }
    return str;
  }

  //https://howtodoinjava.com/java/library/json-simple-read-write-json-examples/
  //https://www.baeldung.com/reading-file-in-java

  //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
  /*void ReadJSON(){
    JSONParser jsonParser = new JSONParser();
         
    try (FileReader reader = new FileReader("employees.json"))
    {
        //Read JSON file
        Object obj = jsonParser.parse(reader);

        JSONArray employeeList = (JSONArray) obj;
        System.out.println(employeeList);
         
        //Iterate over employee array
        employeeList.forEach( emp -> parseEmployeeObject( (JSONObject) emp ) );

    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (ParseException e) {
        e.printStackTrace();
    }
  }*/


  //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

  
  byte[] data(String key, int[] pieces) throws IOException {
    int i;
    String indexAndPieces = "";
    FileInfo file = null;
    int index = 0;
    String filename = "";

    for (i = 0; i < this.files.length; i++) {
      if (this.files[i].getKey().equals(key)) {
        file = this.files[i];
        index = i;
        break;
      }
    }

    if(seedOrNot(file, index)){
      filename=dir+"/seed/"+file.getName();
    }else{
      filename = dir+"/leech/"+file.getName();
    }

    for (int j = 0; j < pieces.length; j++) {
      if (j == pieces.length - 1) {
        indexAndPieces =
          indexAndPieces +
          pieces[j] +
          ":" +
          new String(getPraties(filename, pieces[j]));
      } else {
        indexAndPieces =
          indexAndPieces +
          pieces[j] +
          ":" +
          new String(getPraties(filename, pieces[j])) +
          " ";
      }
    }
    String str = "data " + key + " [" + indexAndPieces + "]\n";
    return str.getBytes();
  }
}
