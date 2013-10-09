import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author bachmann
 *
 * The MailClient class provides the user interface and calls the other 
 * classes as needed. When you press Send, the MailClient class constructs 
 * a Message class object to hold the mail message.
 */
public class MailInterface extends JFrame 
{

	// The components of the the GUI. 
	private JButton btSend = new JButton("Send");
	private JButton btClear = new JButton("Clear");
	private JButton btQuit = new JButton("Quit");
	
	// Label and text field for entering the server that is being contacted to send the mail
	private JLabel outgoingServerLabel = new JLabel("SMTP Mail Server ");
	private TextField outgoingServerField = new TextField("mailfwd.muohio.edu", 40);
	
	// Label and text field for entering the sender of the email
	private JLabel fromLabel = new JLabel("From: ");
	private TextField fromField = new TextField("", 40);
	
	// Label and text field for the recipient of the email
	private JLabel toLabel = new JLabel("To: "); 
	private TextField toField = new TextField("", 40);
	
	// Label and text field for entering the subject line of the email
	private JLabel subjectLabel = new JLabel("Subject: ");
	private TextField subjectField = new TextField("", 40);
	
	// Label and text area in which the body of the message is entered
	private JLabel messageLabel = new JLabel("Message: ");
	private TextArea messageText = new TextArea(10, 40);
	
	// Handles sending the message place in the fields
	private MailInterfaceListener mailSender;

	
	/**
	 * Create a new MailClient window with fields for entering all
	 * the relevant information (From, To, Subject, and message).
	 */
	public MailInterface( MailInterfaceListener mailSender ) {
		
		// Create a frame with "Mailclient" in the title bar
		super("Mailclient");
		
		this.mailSender = mailSender;
		
		// Create all of the panels and add them to the window.
		Container contentPane = this.getContentPane();
		contentPane.add( buildFieldPanel(), BorderLayout.NORTH);
		contentPane.add( buildMessagePanel(), BorderLayout.CENTER);
		contentPane.add( buildButtonPanel(), BorderLayout.SOUTH);
		
		// Stop the program when the window is closed.
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Set the window size, pack the components, and display the window.
		this.setSize(400, 600);
		this.pack();
		this.setVisible( true );

	} // end default constructor
	
	
	/**
	 * Instantiates and places components for entering text information
	 *
	 *	@param panel containing server, from, to, and subject panels.
	 */
	private JPanel buildFieldPanel()
	{
		// Create panels for holding the fields. To make it look nice,
		// create an extra panel for holding all the child panels.
		JPanel outgoingServerPanel = new JPanel( new BorderLayout());
		JPanel fromPanel = new JPanel(new BorderLayout());
		JPanel toPanel = new JPanel(new BorderLayout());
		JPanel subjectPanel = new JPanel(new BorderLayout());
		
		// Panel to specify the mail server
		outgoingServerPanel.add(outgoingServerLabel, BorderLayout.WEST);
		outgoingServerPanel.add(outgoingServerField, BorderLayout.CENTER);
		
		// Panel to specify the sender
		fromPanel.add(fromLabel, BorderLayout.WEST);
		fromPanel.add(fromField, BorderLayout.CENTER);
		
		// Panel to specify the receiver
		toPanel.add(toLabel, BorderLayout.WEST);
		toPanel.add(toField, BorderLayout.CENTER);
		
		// Panel for the subject line
		subjectPanel.add(subjectLabel, BorderLayout.WEST);
		subjectPanel.add(subjectField, BorderLayout.CENTER);
		
		JPanel fieldPanel = new JPanel(new GridLayout(0, 1));
		fieldPanel.add(outgoingServerPanel);
		fieldPanel.add(fromPanel);
		fieldPanel.add(toPanel);
		fieldPanel.add(subjectPanel);
		
		return fieldPanel;
		
	} // end buildFieldPanel
	
	
	/**
	 * Creates a panel that will be used for entering the message body.
	 * 
	 * @return panel containing a textarea for the message body
	 */
	private JPanel buildMessagePanel()
	{
		JPanel messagePanel = new JPanel(new BorderLayout());		
		
		// Text area for the message
		messagePanel.add(messageLabel, BorderLayout.NORTH);	
		messagePanel.add(messageText, BorderLayout.CENTER);
		
		return messagePanel;
		
	} // end buildMessagePanel
	
	
	/**
	 * Creates a panel contain three buttons. Buttons allow the user
	 * to clear the text fields, send a message of quit the program.
	 * 
	 * @return panel containing "clear," "send," and "quit" buttons
	 */
	private JPanel buildButtonPanel()
	{
		// Create a panel for the JButtons and add listeners to the JButtons.
		JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
		btSend.addActionListener(new SendListener());
		btClear.addActionListener(new ClearListener());
		btQuit.addActionListener(new QuitListener());
		buttonPanel.add(btSend);
		buttonPanel.add(btClear);
		buttonPanel.add(btQuit);
		
		return buttonPanel;
		
	} // end buildButtonPanel

	
	/**
	 * With the exception of the outgoingServerField, calling the method will 
	 * clear all text areas and text fields.
	 *
	 */
	private void clearFields()
	{
		fromField.setText("");
		toField.setText("");
		subjectField.setText("");
		messageText.setText("");
		
	} // end clearFields
	
	
	/**
	 * Accessor method for the fromField. Returns a trimed version of the 
	 * sender's email address.
	 * 
	 * @return the sender's email address.
	 */
	public String getFrom()
	{
		return fromField.getText().trim();
		
	} // end getFrom
	
	
	/**
	 * Accessor method for the toField. Returns a trimed version of the 
	 * recepient's email address.
	 * 
	 * @return the recepient's email address.
	 */
	public String getTo()
	{
		return toField.getText().trim();
		
	} // end getTo
	
	
	/**
	 * Accessor method for the subjectField. Returns a trimed version of the 
	 * subject string.
	 * 
	 * @return the sender.
	 */
	public String getSubject()
	{
		return subjectField.getText().trim();
		
	} // end getSubject
	
	
	/**
	 * Accessor method for the message body.
	 * 
	 * @return the sender.
	 */
	public String getBody()
	{
		return messageText.getText();
		
	} // end getBody
	
	
	/**
	 * Accessor method for the message body. Provides and "escaped" version in
	 * which all periods at the beginning of the line have been doubled.
	 * 
	 * @return the subject line.
	 */
	public String getEscapedBody()
	{
		String escapedBody = "";
		String token;
		StringTokenizer parser = new StringTokenizer( messageText.getText(), "\n", true);
			
		while(parser.hasMoreTokens()) {
			token = parser.nextToken();
		
			if(token.startsWith(".") ) {
				token = "." + token;
			}
			
			escapedBody += token;
		}
		
		return escapedBody;
		
	} // end getBody
	
	
	/**
	 * Accessor method for the outgoing mail server. This sever should be
	 * contacted to send the mail.
	 * 
	 * @return server's address.
	 */
	public String getServer()
	{
		return outgoingServerField.getText().trim();
		
	} // end getServer
	

	// Inner class Handler for the Send-JButton event
	class SendListener implements ActionListener 
	{	
		
		public void actionPerformed(ActionEvent event) 
		{
			if ( mailSender.sendMail()) {
				clearFields();
				System.out.println("Mail sent succesfully!");
			}
			else {
				
				System.out.println("Error sending mail!");
			}
			
			
		} // end actionPerformed
	
	
	} // end SendListener class


	// Inner class to Clear the fields on the GUI when clear JButton is pressed. 
	class ClearListener implements ActionListener 
	{

		public void actionPerformed(ActionEvent e) 
		{
			clearFields();
		
		} // end actionPerformed
	
		
	} // end ClearListener class
	
	
	// Inner class to handle Quit JButton events
	class QuitListener implements ActionListener 
	{
		
		public void actionPerformed(ActionEvent e) 
		{
			mailSender.close();
			System.exit(0);
		
		} // end actionPerformed
	
		
	} // end QuitListener class

	
} // end MailInterface class
