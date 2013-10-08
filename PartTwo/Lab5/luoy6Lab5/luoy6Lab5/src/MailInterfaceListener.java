/**
 * CSA 283 Project 1: Interface to capture events from
 * MailInterface GUI.  
 * 
 * @author Eric Bachmann 
 * @version 1.0 September 10, 2007
 */
public interface MailInterfaceListener {
	
	/**
	 * Uses contents of GUI Text fields to construct an
	 * email message and send it.
	 
	 * 
	 * @returns True if the message was successfully sent. False otherwise.
	 */
	public boolean sendMail();
	
	/**
	 * Performs any required closing operations and releases resources
	 * when the GUI is closed down.
	 */
	public void close();
	
} // end MailInterfaceListener interface
