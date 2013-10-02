import java.io.*;
import java.net.*;
import java.util.Scanner;

public class UDPClient {
	
	Scanner scan = new Scanner(System.in);

	public UDPClient() throws Exception
	{
	   DatagramSocket clientSock = new DatagramSocket();
	   
	   ByteArrayOutputStream baos = new ByteArrayOutputStream();
	   DataOutputStream dos = new DataOutputStream(baos);
	   
		
	   System.out.print("Enter the number of floats to be multiplied together: ");
	   int numberOfFloats = scan.nextInt();
		
	   dos.writeInt(numberOfFloats);
	   for (int i = 0; i < numberOfFloats; i++ ) {
	      System.out.print("Enter a float: ");
	      dos.writeFloat(scan.nextFloat());
	   }
	   
	   //pack everything in one datagramPacket. The first integer and several float. send to server
	   DatagramPacket dgp = new DatagramPacket(baos.toByteArray(), baos.size());
	   dgp.setAddress(InetAddress.getLocalHost());
	   dgp.setPort(6789);
	   clientSock.send(dgp);
	
	   
	   
	   DatagramPacket dgPacket = new DatagramPacket(new byte[8], 8);
	   clientSock.receive(dgPacket);
		
	   ByteArrayInputStream bais = new ByteArrayInputStream(dgPacket.getData());
	   DataInputStream dis = new DataInputStream(bais);
	   System.out.print("Product of the floats is : " + dis.readDouble() );
	   clientSock.close();
	
	} // end TCPClient
	
	public static void main(String[] args)  throws Exception{
		
		new UDPClient();
	} // end main
	
}// end TCPClient class	
 



