import java.io.*;
import java.net.*;

public class ChatServer{
	private Socket socket = null;
	private ServerSocket serverSocket = null;
	private DataInputStream dos = null;
	
	protected ChatServer(int port){
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Unable to create server socket");
		}
	}
}
