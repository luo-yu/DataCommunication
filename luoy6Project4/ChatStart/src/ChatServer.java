import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private Socket socket = null;
	private ServerSocket serverSocket = null;
	private boolean quit = false;
	public static final int SERVER_PORT = 6789;
	protected ConcurrentHashMap<String, InetSocketAddress> clientMap = null;
	private String operation = "";
	protected boolean listen = true;
	public static final int ADD_NAME_IP = 10;
	public static final int REMOVE_NAME_IP = 20;
	public static final int REQUEST = 30;
	public static final int QUIT = 40;
	private InetSocketAddress inetSocketAddress = null;

	/**
	 * Constructs and initializes chat server. It initializes client map to
	 * store client information. It creates a ServerSocket to use for listening
	 * for clients.
	 */
	protected ChatServer() {
		clientMap = new ConcurrentHashMap<String, InetSocketAddress>();

		try {
			serverSocket = new ServerSocket(SERVER_PORT);
		} catch (IOException e) {

			System.err.println("Could not listen on port: " + SERVER_PORT);
			System.exit(-1);
		}
		listening();

		try {
			serverSocket.close();
		} catch (IOException e) {
			System.err.println("Unable to close serverSoccket.");
		}

	}

	protected void listening() {
		do {
			// Create new thread to hand each client.
			// Pass the Socket object returned by the accept
			// method to the thread.
			try {

				new ChatServerThread(serverSocket.accept()).start();
			} catch (IOException e) {
				System.err.println("Terminating with client");
			}
		} while (listen);

	}

	/**
	 * Starts a chat server
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		new ChatServer();
	}

	protected class ChatServerThread extends Thread {

		/**
		 * Sets up class data members. Displays communication info.
		 */
		public ChatServerThread(Socket socket) {
			super();

		}

		/**
		 * 
		 * Calling this method will causes a separate executing thread. It
		 * contains all the methods needed to interact with the chat server
		 */
		public void run() {

		}// end run

		/**
		 * Create streams to a client
		 */
		protected void makeStreams() {
			try {
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				System.err.println("Unable to make stream");
			}
		}

		/**
		 * Close the connection to a client
		 */
		protected void closeConnections() {
			try {
				dis.close();
				socket.close();
			} catch (IOException e) {
				System.err.println("Unable to close connections");
			}
		}

		/**
		 * Handles client operations
		 */
		protected void handleClientOperation() {
			int operation = 0;
			try {
				operation = dis.readInt();
			} catch (IOException e) {
				System.err.println("Can't read operation code");
			}

			if (operation == ADD_NAME_IP) {
				System.out.println("Inside of add");
				addUsertoMap();
			} else if (operation == QUIT) {
				System.out.println("Inside of quit");
				quit();
			} else if (operation == REMOVE_NAME_IP) {
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
			String name="";
			int port=0;
			try {
				name = dis.readUTF();
			} catch (IOException e) {
				System.err.println("Unable to read name");
			}
			
			byte[] ip = new byte[4]; 
			 try {
				dis.read(ip);
			} catch (IOException e) {
				System.err.println("Unable to read ip");
			} 
			 
			 try {
				port = dis.readInt();
			} catch (IOException e1) {
				System.err.println("Unable to read port number");
			}

			 try {
				inetSocketAddress = new InetSocketAddress(InetAddress.getByAddress(ip),port);
			} catch (UnknownHostException e) {
				System.err.println("Unable to get inet socket address");
			}
			 clientMap.put(name,inetSocketAddress);
		}

	}

}
