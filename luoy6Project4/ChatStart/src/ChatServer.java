import java.io.*;
import java.net.*;

public class ChatServer{
	private Socket socket = null;
	private ServerSocket serverSocket = null;
	private DataInputStream dis = null;
	private boolean quit = false;
	public static final int port = 6789;
	protected ChatServer(int port){
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Unable to create server socket");
		}
		try {
			socket = serverSocket.accept();
			System.out.println("Client accepted: " + socket );
		} catch (IOException e) {
			System.err.println("Unable to accept socket");
		}
		this.makeStreams();
		while(!quit){
			try {
				String line = dis.readUTF();
				System.out.println(line);
			} catch (IOException e) {
				System.err.println("Unable to read message. Quiting...");
				quit=true;
			}
		}
		this.closeConnections();
		
	}
	
	protected void makeStreams(){
		try {
			dis = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.err.println("Unable to make stream");
		}
	}
	protected void closeConnections(){
		try {
			dis.close();
			socket.close();
		} catch (IOException e) {
			System.err.println("Unable to close connections");
		}
	}
	
	public static void main(String args[]){
		new ChatServer(port);
	}
}
