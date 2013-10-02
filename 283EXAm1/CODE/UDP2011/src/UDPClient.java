import java.io.*;
import java.net.*;
import java.math.*;

public class UDPClient {

	public static void main(String[] args) throws IOException {
		new UDPClient();
	}

	public UDPClient() throws IOException {
		DatagramSocket client = new DatagramSocket();

		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeFloat(2);
		dos.writeFloat(3);
		
		DatagramPacket dgp = new DatagramPacket(baos.toByteArray(), baos.size());
		dgp.setAddress(InetAddress.getLocalHost());
		dgp.setPort(9999);
		client.send(dgp);
		
		
		DatagramPacket receive = new DatagramPacket(new byte[8], 8);
		client.receive(receive);
		ByteArrayInputStream bais = new ByteArrayInputStream(receive.getData());
		DataInputStream dis = new DataInputStream(bais);
		System.out.println(dis.readDouble());
		
	}
}