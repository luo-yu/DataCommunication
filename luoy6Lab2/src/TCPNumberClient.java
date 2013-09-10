import java.io.*;
import java.net.*;
import java.util.*;

public class TCPNumberClient {
	private float firstNum;
	private float secondNum;
	Socket s = null;
	private DataOutputStream dos = null;
	private DataInputStream dis = null;
	private InetAddress serverIP = null;
	
	TCPNumberClient() {
		readConsole();
		connectToServer();
		associateStream();
		sendAndDisplay();
		closeSocket();
	}

	public void readConsole() {
		Scanner readFromConsole = new Scanner(System.in);
		System.out.print("Enter first number: ");
		float firstNumber = readFromConsole.nextFloat();
		System.out.print("Enter second number: ");
		float secondNumber = readFromConsole.nextFloat();
		this.firstNum = firstNumber;
		this.secondNum = secondNumber;
		System.out.println("Enter the IP address of the server");
		String ip = readFromConsole.next();
		try {
			serverIP = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
			System.out.println("No Such IP Address");
		}
	}
	
	public void connectToServer(){
		try {
			s = new Socket(serverIP, TCPNumberServer.SERVER_PORT);
		} catch (UnknownHostException e) {
		
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
	}

	
	public void associateStream(){
		try {
			dos = new DataOutputStream(s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
	}
	
	public void sendAndDisplay(){
		try {
			dos.writeFloat(firstNum);
			dos.writeFloat(secondNum);
			
			float f1 = dis.readFloat();
			System.out.println(f1);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public void closeSocket(){
		try {
			s.close();
			dis.close();
			dos.close();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new TCPNumberClient();
	}

}
