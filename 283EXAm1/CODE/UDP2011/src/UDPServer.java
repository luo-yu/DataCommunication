import java.io.*;
import java.net.*;
import java.math.*;

public class UDPServer {

	public static void main(String[] args) throws IOException {
		new UDPServer();
	}

	public UDPServer() throws IOException {
		
		DatagramSocket dgs = new DatagramSocket(9999);
		DatagramPacket dgp =  new DatagramPacket(new byte[8], 8);
		dgs.receive(dgp);
		
		System.out.println(dgp.getAddress());
		System.out.println(dgp.getPort());
	
		ByteArrayInputStream bais = new ByteArrayInputStream(dgp.getData());
		DataInputStream dis = new DataInputStream(bais);
	
		float f1 = dis.readFloat();
		float f2 = dis.readFloat();
		System.out.println(f1 + " " + f2);
		
		double a =Math.pow(f1, f2)*1.0;
		
		System.out.println(a);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		dos.writeDouble(a);
		DatagramPacket send = new DatagramPacket(baos.toByteArray(), baos.size());
		send.setAddress(dgp.getAddress());
		send.setPort(dgp.getPort());
		dgs.send(send);
		
		
	}
}