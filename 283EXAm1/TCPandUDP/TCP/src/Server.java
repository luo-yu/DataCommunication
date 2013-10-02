import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class Server {
	
	public Server() {
		
		InetAddress ip = null;
		
		try {
			ip = InetAddress.getByName("cnn.com");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(ip.getHostAddress());
		
		try {
			ServerSocket ss = new ServerSocket(31200);
			
			Socket s = ss.accept();
			
			DataInputStream dis = new DataInputStream(s.getInputStream());
			
			int x = dis.readInt();
			
			System.out.println(x);
			
			
			s.close();
			ss.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Server();

	}

}
