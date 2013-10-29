import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
	private ServerSocket serverSocket = null;
	private Thread thread = null;
	public final int port = 6789;

	private ConcurrentHashMap<String, InetSocketAddress> clientMap = null;

	public ChatServer(int port) {
		try {
			serverSocket = new ServerSocket(this.port);
			clientMap = new ConcurrentHashMap<String, InetSocketAddress>();
		} catch (IOException e) {
			System.err.print("Unable to create server socket");
		}
	}

	

}

