import java.io.*;
import java.net.*;



public class Client {

	public Client() {
		

			try {
				
				Socket s = new Socket(InetAddress.getByName("127.0.0.1"), 31200);
				
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				
				dos.writeInt(1);
				
				s.close();
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		DataOutputStream dos;
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Client();
	}

}
