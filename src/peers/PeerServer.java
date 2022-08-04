import java.io.*;
import java.net.*;

public class PeerServer {

  private int server_port;
  private String server_ip_address; //TEMP 
  private ServerSocket_t server_socket; //TEMP
  //private Socket socket;


  PeerServer(int server_port, String server_ip_address) throws IOException {
    this.server_ip_address = server_ip_address;
    this.server_port = server_port;
    this.server_socket = new ServerSocket_t(this.server_port);
    //this.socket = this.server_socket.getServerSocket();
  }

  int getPort() {
    return this.server_port;
  }

  String getIpAddress() {
    return this.server_ip_address;
  }

  ServerSocket_t getServerSocketInfo() {
    return this.server_socket;
  }
}
