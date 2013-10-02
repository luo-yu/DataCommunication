import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;


public class Server {
	
	public Server(){
		
		try {
			DatagramSocket dgSocket = new DatagramSocket(32100);
			
			
			DatagramPacket dgPacket = new DatagramPacket(new byte[4], 4);
			
			dgSocket.receive(dgPacket);
			
			ByteArrayInputStream bis = new ByteArrayInputStream(dgPacket.getData());
			
			DataInputStream dis = new DataInputStream(bis);
			
			int x = dis.readInt();
			
			System.out.println(dgPacket.getAddress());
			System.out.println(dgPacket.getPort());
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			DataOutputStream dos = new DataOutputStream(bos);
			
			dos.writeInt(x*x);
			
			
			
			DatagramPacket dgPacket2 = new DatagramPacket(bos.toByteArray(), bos.size() );
			
			dgPacket2.setAddress( dgPacket.getAddress());
			dgPacket2.setPort(dgPacket.getPort());
			
			dgSocket.send(dgPacket2);
			
			
			
			dgSocket.close();
		} catch (SocketException e) {
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
		new Server();
		
		
		
		
		

	}

}
