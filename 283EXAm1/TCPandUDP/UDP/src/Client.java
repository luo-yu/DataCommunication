import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class Client {
	public Client(){
		
		try {
			DatagramSocket dgSocket = new DatagramSocket();
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			DataOutputStream dos = new DataOutputStream(bos);
			
			dos.writeInt(7);
			
			DatagramPacket dgPacket = new DatagramPacket(bos.toByteArray(), bos.size());
			
			dgPacket.setAddress( InetAddress.getLocalHost());
			dgPacket.setPort(32100);
			
			dgSocket.send( dgPacket );
			
			
			
			
			
			dgSocket.close();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		new Client();
	}

}
