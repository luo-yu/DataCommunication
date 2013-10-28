import java.io.*;
import java.net.*;


public class ChatServer {
	private Socket socket = null;
	private ServerSocket serverSocket = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	public final int  port = 6789;
	
	public ChatServer(){
		try {
			serverSocket = new ServerSocket(port);
			socket = serverSocket.accept();
		} catch (IOException e) {
			System.err.println("Unable to create server socket");
		}
		
	}
	
	
	public void makeStreams(){
		try {
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.err.println("Unable to make streams");
		}
	}
}
