import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
	private Socket socket = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private Scanner operation = null;
	private InetSocketAddress inetSocketAddress = null;
	private ServerSocket listenSocket = null;
	private int listenPort = 0;

	/**
	 * 
	 * @param address
	 * @param serverPort
	 */
	protected ChatClient(InetAddress address, int serverPort) {
		try {
			listenSocket = new ServerSocket(0);
		} catch (IOException e1) {
			System.err.println("Unable to listen to peer");
		}
		System.out.println("Establishing connection...");
		try {
			socket = new Socket(address, serverPort);
		} catch (IOException e) {
			System.err.println("Unable to create socket");
		}
		listening();
		this.makeStreams();
		sendOperation();

	}

	private void listening() {
		do {
			try {
				new ChatClientThread(listenSocket.accept()).start();
			} catch (IOException e) {
				System.err.println("Terminating with client");
			}
		} while (true);

	}

	private void sendOperation() {
		System.out
				.print("If you want to add yourself to the server, enter 10'; \n"
						+ "if you want to remove yourself from the server, enter 20' ;\n"
						+ "if you want to request an address of a user, enter 30 "
						+ "if you want to quit, enter 40");
		operation = new Scanner(System.in);
		int operationCode = operation.nextInt();

		try {
			dos.writeInt(operationCode);
		} catch (IOException e) {
			System.err.println("Unable to write operation");
		}

		if (operationCode == ChatServer.ADD_NAME_IP) {
			System.out.print("Please enter your screen name: ");
			String name = operation.nextLine();
			System.out.print("Please enter your ip address: ");
			String ipaddress = operation.next();
			try {
				dos.writeUTF(name);
			} catch (IOException e) {
				System.err.println("Unable to write name");
			}
			listenPort = listenSocket.getLocalPort();
			try {
				inetSocketAddress = new InetSocketAddress(
						InetAddress.getByName(ipaddress), listenPort);
			} catch (UnknownHostException e) {
				System.err.println("Unable to create inet socket address");
			}
			byte[] ip = inetSocketAddress.getAddress().getAddress();
			try {
				dos.write(ip);
			} catch (IOException e) {
				System.err.println("Unable to write IP to server");
			}
			try {
				dos.writeInt(listenPort);
			} catch (IOException e) {
				System.err.println("Unable to write port number");
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

	public static void main(String args[]) throws NumberFormatException,
			UnknownHostException {
		new ChatClient(InetAddress.getByName("127.0.0.1"),
				ChatServer.SERVER_PORT);
	}

	protected class ChatClientThread extends Thread {
		public ChatClientThread(Socket socket) {

		}

		/**
		 * 
		 * Calling this method will causes a separate executing thread. It
		 * contains all the methods needed to interact with the chat server
		 */
		public void run() {

		}// end run

	}

}