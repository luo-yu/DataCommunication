import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
	private Socket socket = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private Scanner operation = null;

	/**
	 * 
	 * @param address
	 * @param serverPort
	 */
	protected ChatClient(InetAddress address, int serverPort) {
		System.out.println("Establishing connection...");
		try {
			socket = new Socket(address, serverPort);
		} catch (IOException e) {
			System.err.println("Unable to create socket");
		}
		this.makeStreams();
		sendOperation();

	}

	private void sendOperation() {
		System.out
				.println("If you want to add yourself to the server, enter \'add\' followed by a screen name; \n"
						+ "if you want to remove yourself from the server, enter \'remove\' followed by a screen name;\n"
						+ "if you want to request an address of a user, enter \'request\' followed by a screen name"
						+ "if you want to quit, enter \'quit\'");
		 operation= new Scanner(System.in);
		 String temp = operation.nextLine();
		 
		 try {
			dos.writeUTF(temp);
		} catch (IOException e) {
			System.err.println("Unable to write operation");
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

	public static void main(String args[]) throws NumberFormatException,
			UnknownHostException {
		new ChatClient(InetAddress.getByName("127.0.0.1"), ChatServer.port);
	}

}