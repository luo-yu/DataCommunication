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
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MessageBuilder implements MailInterfaceListener
{

	// Server, sender, recipient, subject and body for the message. 
	private String server;
	private String from;
	private String to;
	private String subject;
	private String body;


	// TCP connection to the server.
	SMTPConnection smtpConnection = null;
	
	// GUI interface for the mail application
	MailInterface mailFace;
	
	/**
	 * Instantiates an interface and stores a reference to it in a data 
	 * member.
	 *
	 */
	public MessageBuilder( )
	{
		mailFace = new MailInterface( this );
		
	} // end constructor
	
	
	
	/**
	 * Called by the interface when the send message is pressed. It opens
	 * an SMTPConnection with the appropriate server. Puts together mail 
	 * message commands, and message data and uses the SMTP connection to 
	 * send them.
	 * 
	 * @return true if the mail was sent successfully.
	 */
	public boolean sendMail()
	{		
		// Retrieve the data contained in the text fields and areas of the GUI 
		server = mailFace.getServer();
		from = mailFace.getFrom();
		to = mailFace.getTo();
		subject = mailFace.getSubject();
		body = mailFace.getEscapedBody();
		
		// Instantiate an SMTPConnection object if one has not already be created
		// TODO
		
		// Send SMTP commands and check the response to each in order to 
		// send and email message.
		// TODO
		
		// Return true if the message sent successfully
		// TODO
		return false;
		
	} // end sendMail
	
	/**
	 * Performs any required closing operations and releases resources
	 * when the GUI is closed down.
	 */
	public void close()
	{
		// If the smtpConnection is not null, close it down.
		// TODO
		
	} // end close
	

	/**
	 * Checks whether the email address is valid. Checks that
	 * the address has only one @-sign and is not the empty string. 
	 * 
	 * @return true if the address is valid
	 */
	@SuppressWarnings("unused")
	private boolean isValidAddress(  final String address ) 
	{

		int atPlace = address.indexOf('@');
		
		if( address.length() == 0 || atPlace < 1 
			|| ( address.length() - atPlace ) <= 1 
			|| atPlace != address.lastIndexOf('@') ) {
			
			return false;
		}
		else {

			return true;
		}
		
	} // end isValid
	
	
	public static void main(String[] args) 
	{
		new MessageBuilder();
			
	} // end main
	
	
} // end MailSender
