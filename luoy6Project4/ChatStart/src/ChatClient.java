import java.io.*;
import java.net.*;

public class ChatClient {
	private Socket socket = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private boolean quit = false;

	protected ChatClient(InetAddress address, int serverPort) {
		System.out.println("Establishing connection...");
		try {
			socket = new Socket(address, serverPort);
		} catch (IOException e) {
			System.err.println("Unable to create socket");
		}
		this.makeStreams();
		String line = "";
		while (!line.equals("quit")) {
			try {
				line = dis.readLine();
			} catch (IOException e) {
				System.err.println("Unable to read line from console");
			}
			if (line.equals("quit"))
				quit = true;
			else {
				try {
					dos.writeUTF(line);
					dos.flush();
				} catch (IOException e) {
					System.err.println("Unable to write line");
				}
			}
		}
	}

	protected void makeStreams() {

		dis = new DataInputStream(System.in);
		try {
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.err.println("Unable to create data ouput stream");
		}

	}

	protected void closeConnections() {
		try {
			dis.close();
			dos.close();
			socket.close();
		} catch (IOException e) {
			System.err.println("Unable to close connections");
		}
	}
	
	public static void main(String args[]) throws NumberFormatException, UnknownHostException{
		new ChatClient(InetAddress.getByName("127.0.0.1"), ChatServer.port);
	}

}