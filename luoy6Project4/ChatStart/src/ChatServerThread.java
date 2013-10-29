import java.io.*;
import java.net.*;


public class ChatServerThread extends Thread {
	
	
	private Socket socket = null;
	private int iD = -1;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	
	
	protected ChatServerThread(Socket socket){
		this.socket = socket;
		this.iD = socket.getPort();
	}
	
	public void run(){
		
	}
	
	/**
	 * Creates BufferReader, DataInputStream, and DataoutputStream
	 */
	protected void makeStream() {
		try {
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.err.println("Unable to make streams");
		}
	}
	
	/**
	 * Closes all the streams, socket, and BufferReader
	 */
	protected void closeConnection() {
		try {
			this.dis.close();
			this.dos.close();
			this.socket.close();
		} catch (IOException e) {
			System.err.println("Unable to close connections");
		}
	}


}
