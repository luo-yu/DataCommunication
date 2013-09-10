import java.io.*;
import java.net.*;

public class TCPNumberServer {

	public static final int SERVER_PORT = 32150;

	ServerSocket serverSocket = null;

	Socket clientSocket = null;

	DataOutputStream dos = null;
	DataInputStream dis = null;
	float f1;
	float f2;
	float product;

	/*
	 * Create a server socket with a port number 32150
	 */
	protected void createServerSocket() {
		try {
			this.serverSocket = new ServerSocket(SERVER_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * Display information of the server
	 */
	protected void displayContactInfo() {
		try {
			System.out.println("Number Server standing by to accept Clients:"
					+ "\nIP : " + InetAddress.getLocalHost() + "\nPort: "
					+ serverSocket.getLocalPort() + "\n\n");
		} catch (UnknownHostException e) {

			e.printStackTrace();
		}
	}

	/*
	 * if the server receives a negative number, the server will return the
	 * calculated answer and close the socket connection, and close the server
	 * 
	 * if the server receives positive numbers, the server will keep open
	 */
	protected void listenForClients() {
		if (f1 < 0 || f2 < 0) {
			handleOneClient();
			this.closeServer();
		}

		while (f1 >= 0 && f2 >= 0) {
			handleOneClient();
		}

	}

	protected void handleOneClient() {
		try {
			this.clientSocket = new Socket();
			this.clientSocket = serverSocket.accept();
		} catch (IOException e) {

			e.printStackTrace();
		}
		this.createClientStreams();
		this.sendAndReceiveNumbers();
		this.closeClientConnection();
	}

	/*
	 * dos will be used to write number back to the client dis will be used to
	 * receive number from the client
	 */
	protected void createClientStreams() {

		try {
			dos = new DataOutputStream(this.clientSocket.getOutputStream());
			dis = new DataInputStream(this.clientSocket.getInputStream());
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	protected void sendAndReceiveNumbers() {
		try {
			f1 = dis.readFloat();
			f2 = dis.readFloat();
			product = f1 * f2;
			dos.writeFloat(product);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected void closeClientConnection() {
		try {
			this.clientSocket.close();
			dis.close();
			dos.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	protected void closeServer() {
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public TCPNumberServer() {
		this.createServerSocket();
		this.displayContactInfo();
		// this.handleOneClient();
		this.listenForClients();
		this.closeServer();
	}

	public static void main(String[] args) {

		new TCPNumberServer();
	}
}
