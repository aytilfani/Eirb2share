import java.io.*;
import java.net.*;

public class Socket_t extends Thread {

  //Les infos utiles pour la connexion du client au tracker
  private Socket socket;
  private BufferedReader reader;
  private PrintWriter writer;

  public Socket_t(Socket s, BufferedReader r,PrintWriter w ){
    socket = s;
    writer = w;
    reader = r;
  }

  public Socket_t(int port, String ip_address) {
    try {
      socket = new Socket(ip_address, port);
      writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true) ;
      reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      System.out.println(reader);
      System.out.println(socket);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public BufferedReader getReader() {
    System.out.println("je renvoie le reader");
    return reader;
  }
  
  public PrintWriter getWriter() {
    System.out.println("je renvoie le writer");
    return writer;
  }

  public Socket getSocket() {
    System.out.println(socket);
    return socket;
  }

  public void write(String message) throws IOException{
    this.getWriter().println(message);
    this.getWriter().flush();

    return;
  }

  public String read() {
    try {
      System.out.println("Je suis dans le read");
      String res = "";
      System.out.println(this.reader);
      while ((res = this.reader.readLine()) != null) {
        if (res.trim().equals("")) {
          System.out.println("done");
          break;
        }
        else {
          System.out.println(res);
          return(res);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  public void close() throws IOException{
    reader.close();
    writer.close();
    socket.close();    

  }
}
