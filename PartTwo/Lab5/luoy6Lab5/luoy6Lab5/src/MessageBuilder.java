import java.io.IOException;

/*
 * Created on Feb 8, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

/**
 * @author bachmaer
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class MessageBuilder implements MailInterfaceListener {

	// Server, sender, recipient, subject and body for the message.
	private String server;
	private String from;
	private String to;
	private String subject;
	private String body;
	public static final String CRLF = "\r\n";

	// TCP connection to the server.
	SMTPConnection smtpConnection = null;

	// GUI interface for the mail application
	MailInterface mailFace;

	/**
	 * Instantiates an interface and stores a reference to it in a data member.
	 * 
	 */
	public MessageBuilder() {
		mailFace = new MailInterface(this);

	} // end constructor

	/**
	 * Called by the interface when the send message is pressed. It opens an
	 * SMTPConnection with the appropriate server. Puts together mail message
	 * commands, and message data and uses the SMTP connection to send them.
	 * 
	 * @return true if the mail was sent successfully.
	 */
	@SuppressWarnings("unused")
	public boolean sendMail() {
		// Retrieve the data contained in the text fields and areas of the GUI
		server = mailFace.getServer();
		from = mailFace.getFrom();
		to = mailFace.getTo();
		subject = mailFace.getSubject();
		body = mailFace.getEscapedBody();

		// Instantiate an SMTPConnection object if one has not already be
		// created
		// TODO
		try {
			smtpConnection = new SMTPConnection(server);
		} catch (IOException e) {
			System.err.println("Can not instantiate an SMTPConnection object");
		}

		// Send SMTP commands and check the response to each in order to
		// send and email message.
		// TODO

		if (this.isValidAddress(from) && this.isValidAddress(to)) {
			try {

				if (smtpConnection.send("mail from:<" + from + ">" + "\r\n",
						250) == false) {
					System.err
							.println("something went wrong when send mail from command");
					return false;
				}
				if (smtpConnection.send("rcpt to:<" + to + ">" + "\r\n", 250) == false) {
					System.err
							.println("something went wrong when send mail from command");
					return false;
				}
				if (smtpConnection.send("data" + "\r\n", 354) == false) {
					System.err
							.println("something went wrong when send mail from command");
					return false;

				}
				if (smtpConnection.send("from:" + from + "\r\n" + "to:" + to
						+ "\r\n" + "subject:" + subject + "\r\n" + body
						+ "\r\n" + "." + "\r\n", 250) == false) {
					System.err
							.println("something went wrong when send mail from command");
					return false;
				}

			} catch (IOException e) {
				System.err.println("Something went wrong during the send process");
				return false;
			}
		} else {
			System.err.println("address is not valid");
		}
		// Return true if the message sent successfully
		// TODO

		return true;

	}// end sendMail

	/**
	 * Performs any required closing operations and releases resources when the
	 * GUI is closed down.
	 */
	public void close() {
		// If the smtpConnection is not null, close it down.
		// TODO
		if (smtpConnection != null) {
			smtpConnection.close();
		}

	} // end close

	/**
	 * Checks whether the email address is valid. Checks that the address has
	 * only one @-sign and is not the empty string.
	 * 
	 * @return true if the address is valid
	 */
	@SuppressWarnings("unused")
	private boolean isValidAddress(final String address) {

		int atPlace = address.indexOf('@');

		if (address.length() == 0 || atPlace < 1
				|| (address.length() - atPlace) <= 1
				|| atPlace != address.lastIndexOf('@')) {

			return false;
		} else {

			return true;
		}

	} // end isValid

	public static void main(String[] args) {
		new MessageBuilder();

	} // end main

} // end MailSender
