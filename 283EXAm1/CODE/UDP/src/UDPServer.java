import java.io.*;
import java.net.*;

public class UDPServer {
	
	public UDPServer() throws Exception{
		
		DatagramSocket serverSock = new DatagramSocket(6789);
		DatagramPacket dgp = new DatagramPacket(new byte[40], 40);
		serverSock.receive(dgp);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(dgp.getData());
		DataInputStream dis = new DataInputStream(bais);
		
		int numberOfFloats = dis.readInt();
		System.out.println(numberOfFloats);
		double answer = 1.0;
		for(int i =0; i<numberOfFloats; i++){
			answer = answer * dis.readFloat();
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeDouble(answer);
		DatagramPacket send = new DatagramPacket(baos.toByteArray(), baos.size());
		send.setAddress(dgp.getAddress());
		send.setPort(dgp.getPort());
		serverSock.send(send);
		
		dis.close();
		dos.close();
		baos.close();
		bais.close();
		serverSock.close();
	}
	
	
public static void main(String[] args)  throws Exception{
		
		new UDPServer();
	} // end main

}
