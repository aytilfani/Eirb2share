import java.io.*;
import java.net.*;

public class ServerSocket_t extends Thread {

  //Les infos utiles pour la connexion du client au tracker
  private ServerSocket serverSocket;
  private BufferedReader reader;
  private PrintWriter writer;
  private Socket socket;

  public ServerSocket_t(int port) {
    try {
      serverSocket = new ServerSocket(port);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public BufferedReader getReader() {
    return reader;
  }
  
  public PrintWriter getWriter() {
    return writer;
  }

  public Socket getServerSocket() throws IOException {
    socket = serverSocket.accept();
    writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true) ;
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    return socket;
  }
}
