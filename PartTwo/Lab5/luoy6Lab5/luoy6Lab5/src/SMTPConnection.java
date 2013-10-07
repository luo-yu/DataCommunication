import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * CSA 283 Project 2: SMTPConnection
 * 
 * Open an SMTP connection to a remote machine that can be used to send email.
 * 
 * @author bachmaer
 * @version Created on Feb 8, 2004
 */
public class SMTPConnection {

	// To make code more readable
	public static final String CRLF = "\r\n"; // carraige return line feed

	private Socket tcpSocket; // Socket connection to the server

	private static final int SMTP_PORT = 25; // smtp protocol port number

	// Streams for reading from and writing to the server.
	private BufferedReader fromServer;
	private DataOutputStream toServer;

	/**
	 * Creates a socket connection to a mail server. Reads the initial reply and
	 * sends HELO message.
	 * 
	 * @param mailServer
	 *            address of the SMTP server to connect with
	 * @throws IOException
	 *             problem with initializing the connection.
	 */
	public SMTPConnection(String mailServer) throws IOException {
		makeConnection(mailServer);

	} // end constructor

	protected void makeConnection(String mailServer) throws IOException {
		if (tcpSocket == null) {

			// Create a connection to the mail server
			tcpSocket = new Socket(mailServer, SMTP_PORT);

			// Instantiate streams for communicating with server
			fromServer = new BufferedReader(new InputStreamReader(
					tcpSocket.getInputStream()));
			toServer = new DataOutputStream(tcpSocket.getOutputStream());

			// Read initial reply for the server
			String reply = fromServer.readLine();

			// Check the reply from the server
			if (parseReply(reply) != 220) {
				System.err.println("Error in connect.");
				System.err.println(reply);
				throw new IOException();
			}

			// Send greeting to the server
			toServer.writeBytes("HELO "
					+ InetAddress.getLocalHost().getHostName() + CRLF);

			// Read reply from the server
			reply = fromServer.readLine();

			// Check the reply from the server
			if (parseReply(reply) != 250) {
				System.err.println("HELO failed");
				System.err.println(reply);
				throw new IOException();
			}
		}
	} // end makeConnection

	/**
	 * Sends a command string to the mail server and reads the reply form the
	 * server. Returns true or false to indicate success or failure.
	 * 
	 * @param command
	 *            string to be sent to the server
	 * @param expected
	 *            return code from the server
	 * @return true if code returned by the server matches the
	 *         expectedReturnCode
	 */
	public boolean send(String command, int expectedReturnCode)
			throws IOException {
		// Send the command
		// TODO
		toServer.writeBytes(command + "\r\n");

		// Receive the reply
		// TODO
		String reply = fromServer.readLine();
		// Parse the reply and return true if it equals the expectedReturncode
		// TODO
		if (parseReply(reply) == expectedReturnCode) {
			return true;
		} else {
			return false;
		}

	} // end send

	/**
	 * Parses the reply line from the server using a StringTokenizer. Returns an
	 * int representing the reply code.
	 * 
	 * @param the
	 *            reply code
	 */
	protected int parseReply(String reply) {

		StringTokenizer parser = new StringTokenizer(reply);
		String replycode = parser.nextToken();
		return (new Integer(replycode)).intValue();

	} // end parseReply

	/**
	 * Sends "QUIT" and closes the connection.
	 */
	public void close() {
		if (tcpSocket != null) {

			try {

				send("QUIT" + CRLF, 221);

				fromServer.close();
				toServer.close();
				tcpSocket.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	} // end close

} // end SMTPConnection
