import java.io.*;
import java.net.*;

public class TCPNumberServer {

	public static final int SERVER_PORT = 32150;

	ServerSocket serverSocket = null;

	Socket clientSocket = null;

	DataOutputStream dos = null;
	DataInputStream dis = null;

	protected void createServerSocket() {
		try {
			this.serverSocket = new ServerSocket(SERVER_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected void displayContactInfo() {
		try {
			System.out.println("Number Server standing by to accept Clients:"
					+ "\nIP : " + InetAddress.getLocalHost() + "\nPort: "
					+ serverSocket.getLocalPort() + "\n\n");
		} catch (UnknownHostException e) {

			e.printStackTrace();
		}
	}

	protected void listenForClients() {
		try {
			while(dis.readFloat()>=0){
				handleOneClient();
			}
		} catch (IOException e) {
			
			e.printStackTrace();
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

	protected void createClientStreams() {
		
			try {
				dos = new DataOutputStream(this.clientSocket.getOutputStream());
				dis = new DataInputStream(this.clientSocket.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

	}

	// 11.
	protected void sendAndReceiveNumbers() {
		try {
			float f1 = dis.readFloat();
			float f2 = dis.readFloat();
			float product = f1 * f2;
			dos.writeFloat(product);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 13.
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
		this.handleOneClient();
		this.listenForClients();
		this.closeServer();
	}

	public static void main(String[] args) {

		new TCPNumberServer();
	}
}
