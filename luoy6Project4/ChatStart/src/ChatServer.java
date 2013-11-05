import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
	private Socket socket = null;
	private ServerSocket serverSocket = null;
	private DataInputStream dis = null;
	private boolean quit = false;
	public static final int port = 6789;
	ConcurrentHashMap<String, InetSocketAddress> clientMap = null;
	private String operation = "";

	protected ChatServer(int port) {
		clientMap = new ConcurrentHashMap<String, InetSocketAddress>();

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Unable to create server socket");
		}
		try {
			socket = serverSocket.accept();
			System.out.println("Client accepted: " + socket);
		} catch (IOException e) {
			System.err.println("Unable to accept socket");
		}
		this.makeStreams();
		try {
			operation = dis.readUTF();
			System.out.println("Operation string: " + operation);
		} catch (IOException e) {
			System.err.println("Unable to read operation");
		}

		handleClientOperation(operation);

	}

	protected void handleClientOperation(String temp) {
		int length = temp.length();

		if (temp.substring(0, 3).equalsIgnoreCase("add")) {
			System.out.println("Inside of add");
			addUsertoMap();
		} else if (temp.substring(0, 4).equalsIgnoreCase("quit")) {
			System.out.println("Inside of quit");
			quit();
		} else if (temp.substring(0, 6).equalsIgnoreCase("remove")) {
			System.out.println("Inside of remove");
			removeUser();
		} else {
			System.out.println("Inside of request");
			handleUserRequest();
		}

	}

	private void quit() {
		// TODO Auto-generated method stub

	}

	private void handleUserRequest() {
		// TODO Auto-generated method stub

	}

	private void removeUser() {
		// TODO Auto-generated method stub

	}

	private void addUsertoMap() {
		// TODO Auto-generated method stub

	}

	protected void makeStreams() {
		try {
			dis = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.err.println("Unable to make stream");
		}
	}

	protected void closeConnections() {
		try {
			dis.close();
			socket.close();
		} catch (IOException e) {
			System.err.println("Unable to close connections");
		}
	}

	public static void main(String args[]) {
		new ChatServer(port);
	}

	protected class serviceClient extends Thread {

	}

}
