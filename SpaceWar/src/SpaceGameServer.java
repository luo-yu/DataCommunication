import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import spaceWar.*;

/**
 * Helper class to keep track of client TCP Socket connections and
 * an associate output stream. Maintaining a collection of these
 * objects removes the requirement to have a continuous running
 * thread for each client. After a client has registered only
 * remove information will be set using this Socket by the server.
 * The server will not receive anything.
 * 
 * 
 */
class ClientSocketConnection {
	/**
	 * Connection to a client that is listing for remove
	 * information
	 */
	public Socket clientTCPConnection;

	/**
	 * Output stream for a connected socket;
	 */
	public DataOutputStream dos;

	public ClientSocketConnection(Socket socket,
			DataOutputStream out) {
		// Save the connection and the output stream
		this.clientTCPConnection = socket;
		this.dos = out;
	}

} // end ClientSocketConnection

/**
 * 
 *         SpaceGameServer. Implements a server to support a
 *         multiuser game in which clients maneuver spacecraft
 *         through a shared virtual world and attempt to destroy
 *         each other by either firing torpedoes or ramming. The
 *         servers is the arbiter of all events resulting in the
 *         destruction of a torpedo or a spacecraft and generates
 *         messages for removal accordingly. Removal information
 *         is sent via TCP. UDP update messages that are received
 *         from clients are forwarded to all other clients.
 * 
 */
public class SpaceGameServer {
	// Random number generator for obstacle positions
	Random rand = new Random();

	// Contains all IP addresses and port numbers of the
	// DatagramSockets
	// used by the clients. UDP segments are forwarded using the
	// information
	// in this data member.
	protected ArrayList<InetSocketAddress> 
	clientDatagramSocketAddresses = new ArrayList<InetSocketAddress>();

	// Contains objects which holds a Socket connection and an
	// associated
	// DataOutputStream for each client. Data pertaining to the
	// removal
	// of torpedoes and ships are sent reliably to clients using
	// this
	// ArrayList.
	protected ArrayList<ClientSocketConnection> playerTCPConncetions = new ArrayList<ClientSocketConnection>();

	// Simple gui to display what the server is tracking
	protected ServerGUI display;

	// Sector containing all information about the game state
	protected Sector sector;

	// True till GUI is closed. Setting to false cases all message
	// forwarding
	// and game state updating to end.
	protected boolean playing = true;

	// Reference for timer object used to automatically update
	// torpedoes
	protected Timer torpedoTimer;

	// Class which contains the torpedo update task
	protected TorpdedoUpdater torpUpdater;

	/**
	 * Server constructor. Create data members to use for tracking
	 * and updating game information. Create obstacles. Create and
	 * start GUI. Start threads and tasks.
	 */
	public SpaceGameServer() {
		// Create sector to hold all game information
		sector = new Sector();

		// Create and position the obstacles
		createObstacles();

		// Create the GUI that will display the sector
		display = new ServerGUI(sector);

		// Start the TCP and UDP servers
		new ReliableServer().start();
		new BestEffortServer().start();

		// Start the task to update the torpedoes
		torpedoTimer = new Timer();
		torpUpdater = new TorpdedoUpdater();
		torpedoTimer.scheduleAtFixedRate(torpUpdater, 0, 50);

	} // end SpaceGameServer constructor

	/**
	 * Create a number of obstacles as determined by a value held
	 * in Constants.NUMBER_OF_OBSTACLES. Obstacles are in random
	 * positions and are shared by all clients.
	 */
	protected void createObstacles() {
		for (int i = 0; i < Constants.NUMBER_OF_OBSTACLES; i++) {

			sector.addObstacle(new Obstacle(rand
					.nextInt(Constants.MAX_SECTOR_X), rand
					.nextInt(Constants.MAX_SECTOR_Y)));

		}

	} // end createObstacles

	/**
	 * Causes all threads and timer tasks to cease execution and
	 * closes all sockets.
	 */
	public void close() {
		playing = false;

	} // end close

	/**
	 * Sends remove information for a particular SpaceCraft of
	 * Torpedo to all clients. Each client maintains a Socket with
	 * which it only listens for this remove information.
	 * 
	 * @param sc
	 *            ship to be removed
	 */
	synchronized protected void sendRemove(SpaceCraft sc) {

		// Go through all the players in the game
		for (int i = 0; i < playerTCPConncetions.size(); i++) {

			// Get the Socket and DataOutputStream for a
			// particular
			// player
			ClientSocketConnection isa = playerTCPConncetions
					.get(i);
			DataOutputStream dos = isa.dos;

			try {
				// Write out identifying information for the
				// entity to be removed.
				dos.write(sc.ID.getAddress().getAddress());
				dos.writeInt(sc.ID.getPort());

				// Indicate whether the entity to be removed
				// is a torpedo or a spacecraft
				if (sc instanceof Torpedo) {

					dos.writeInt(Constants.REMOVE_TORPEDO);
				} else {

					dos.writeInt(Constants.REMOVE_SHIP);
				}

			} catch (IOException e) {

				// The Socket connection has been lost. Most
				// likely the
				// client has left the game. Remove the socket
				// from the
				// list of connections. Yes, this is sloppy. :(
				playerTCPConncetions.remove(isa);
			}
		}

	} // end sendRemove

	/**
	 * Creates a update message for a torpedo and sends it to all
	 * clients.
	 * 
	 * @param sc
	 *            torpedo being updated
	 * @param dgSock
	 *            socket to use to send the message
	 */
	protected void sendTorpedoUpdate(Torpedo sc,
			DatagramSocket dgSock) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		try {
			// Write fields of the message
			dos.write(sc.ID.getAddress().getAddress());
			dos.writeInt(sc.ID.getPort());
			dos.writeInt(Constants.UPDATE_TORPEDO);
			dos.writeInt(sc.getXPosition());
			dos.writeInt(sc.getYPosition());
			dos.writeInt(sc.getHeading());

		} catch (IOException e) {
			System.err.println("Error sending torpedo update.");
		}

		// Create the packet
		DatagramPacket dpack = new DatagramPacket(
				baos.toByteArray(), baos.size());

		// Send the packet to every client
		allForward(dpack, dgSock);

		try {
			dos.close();
		} catch (IOException e) {
			System.err.println("Error closing stream.");
		}

	} // end sendTorpedoUpdate

	/**
	 * Sends a datagram packet to all clients except one as
	 * specified by an input argument.
	 * 
	 * @param fwdPack
	 *            packet to send
	 * @param notSendTo
	 *            address to skip
	 * @param dgSock
	 *            socket to use to send the message
	 */
	protected void selectiveForward(DatagramPacket fwdPack,
			InetSocketAddress notSendTo, DatagramSocket dgSock) {
		for (InetSocketAddress isa : clientDatagramSocketAddresses) {

			if (!isa.equals(notSendTo)) {

				fwdPack.setSocketAddress(isa);
				try {
					dgSock.send(fwdPack);

				} catch (IOException e) {
					System.err
							.println("Error performing selective forward.");
				}
			}
		}
	} // end selectiveForward

	/**
	 * Sends a datagram packet to all clients.
	 * 
	 * @param fwdPack
	 *            packet to send
	 * @param dgSock
	 *            socket to use to send the message
	 */
	protected void allForward(DatagramPacket fwdPack,
			DatagramSocket dgSock) {
		for (InetSocketAddress isa : clientDatagramSocketAddresses) {

			fwdPack.setSocketAddress(isa);

			try {
				dgSock.send(fwdPack);
			} catch (IOException e) {
				System.err
						.println("Error forward message to all clients.");
			}
		}

	} // end allForward

	/**
	 * 
	 *         Inner class to listen for new clients sending
	 *         information reliably using TCP. It is a single
	 *         Threaded TCP server. It takes care of the following
	 *         events: 1. Clients coming into the game 2. Clients
	 *         firing torpedoes 3. Clients leaving the game
	 */
	class ReliableServer extends Thread {

		ServerSocket gameServerSocket = null;

		DataInputStream dis = null;
		DataOutputStream dos = null;
		Socket clientConnection = null;
		InetSocketAddress clientID;

		int port = -1;
		int code = -1;

		public ReliableServer() {
			try {

				gameServerSocket = new ServerSocket(
						Constants.SERVER_PORT);

			} catch (IOException e) {
				System.err
						.println("Error creating server socket used to listing for joining clients.");
				System.exit(0);
			}

		} // end JoinServer

		/**
		 * Listens for join and exiting clients using TCP. Joining
		 * clients are sent the x and y coordinates of all
		 * obstacles followed by a negative number.
		 */
		public void run() {

			while (playing) { // loop till playing is set to false

				try {
					clientConnection = gameServerSocket.accept();

					// Make streams to communicate with the client
					dis = new DataInputStream(
							clientConnection.getInputStream());
					dos = new DataOutputStream(
							clientConnection.getOutputStream());

					// Determine if a client is registering,
					// firing, or exiting
					code = dis.readInt();

					// Read Port number
					port = dis.readInt();

					// Get the clients IP address
					clientID = new InetSocketAddress(
							clientConnection.getInetAddress(),
							port);

					// Handle the client's request
					if (code == Constants.REGISTER) {

						handleNewClient();
					} else if (code == Constants.FIRED_TORPEDO) {

						handleTorpedoLaunch();
					} else if (code == Constants.EXIT) {

						handleExitingClient();
					}

					// Be nice to the other threads
					yield();

				} catch (IOException e) {

					System.err
							.print("Error comminicating with a client.");

				}

			} // end while

			try {
				this.gameServerSocket.close();
			} catch (IOException e) {

			}

		} // end run

		/**
		 * Takes care of clients that are first coming into the
		 * game. It sends all the obstacles to the client. Saves
		 * the UDP socket address for the clients and save a TCP
		 * socket connection to the client.
		 * 
		 * @throws IOException
		 */
		protected void handleNewClient() throws IOException {
			// Add the player to the database
			System.out.println("New Client; " + clientID);
			clientDatagramSocketAddresses.add(clientID);

			// Retrieve a list of the obstacles in the sector
			ArrayList<Obstacle> obstacles = sector
					.getObstacles();

			// Send to coordinates of the obstacles
			for (Obstacle obs : obstacles) {

				dos.writeInt(obs.getXPosition());
				dos.writeInt(obs.getYPosition());
			}

			// Signal that their are no more obstacle coordinates
			// to send
			dos.writeInt(-1);

			// Leave the connection open and save it for sending
			// removal messages
			playerTCPConncetions.add(new ClientSocketConnection(
					clientConnection, dos));

		} // end handleNewClient

		/**
		 * Receives information from a client about a torpedo that
		 * has been launched and set up the torpedo for automatic
		 * updating.
		 * 
		 * @throws IOException
		 */
		protected void handleTorpedoLaunch() throws IOException {
			System.out.println("client is firing torpedo");
			// Get the torpedo position and heading
			int x = dis.readInt();
			int y = dis.readInt();
			int heading = dis.readInt();

			// The torpedo to the sector so that it can be
			// automatically
			// updated by the timer task.
			Torpedo torpedo = new Torpedo(clientID, x, y,
					heading);
			sector.updateOrAddTorpedo(torpedo);

			// Close off the connection. The request has been
			// completed
			dis.close();
			dos.close();
			clientConnection.close();

		} // end handleTorpedoLaunch

		/**
		 * Takes are of a client that is leaving the game. It
		 * removes the UDP socket address from the list of client
		 * socket address. Note: In order to simplify things, it
		 * does not attempt to remove TCP information about the
		 * client from the server database.
		 * 
		 * @throws IOException
		 */
		protected void handleExitingClient() throws IOException {
			System.out.println("Departing Client; " + clientID);

			// Remove the player from the database
			clientDatagramSocketAddresses.remove(clientID);

			SpaceCraft sc = new SpaceCraft(clientID);

			// Remove the client from the server sector display
			sector.removeSpaceCraft(sc);

			// Tell all the other clients to remove the ship
			sendRemove(sc);

			// Close off the connection. The request has been
			// completed
			dis.close();
			dos.close();
			clientConnection.close();

		} // end handleExitingClient

	} // end ReliableServer class

	/**
	 * Inner class to receive and forward UDP packets containing
	 * updates from clients. In addition, it checks for collisions
	 * caused by client movements and sends appropriate removal
	 * information
	 * 
	 * @author bachmaer
	 */
	class BestEffortServer extends Thread {

		// Socket through which all client UDP messages
		// are received
		protected DatagramSocket gamePlaySocket = null;

		// DatagramPacket for receiving updates. All updates are
		// 24 bytes.
		protected DatagramPacket recPack = new DatagramPacket(
				new byte[24], 24);

		// Data members for holding values contained in the fields
		// of
		// received messages
		protected byte ipBytes[] = new byte[4];
		protected int port, code, x, y, heading;
		protected InetSocketAddress id;

		/**
		 * Creates DatagramSocket through which all client update
		 * messages will be received and forwarded.
		 */
		public BestEffortServer() {

			try {

				gamePlaySocket = new DatagramSocket(
						Constants.SERVER_PORT);

			} catch (IOException e) {

				System.err
						.println("Error creating socket to receive and forward messages.");
				System.exit(0);
			}

		} // end gamePlayServer

		/**
		 * run method that continuously receives update messages,
		 * updates the display, and then forwards update messages.
		 */
		public void run() {

			// Receive and forward messages
			while (playing) {

				try {

					receiveReadAndForwardMessage();

					updateDisplay();

					// Be nice to other threads
					yield();

				} catch (IOException e) {
					System.err
							.println("Error sending remove message.");
				}
			}

			gamePlaySocket.close();

		} // end run

		/**
		 * Receives an update message and extracts the value
		 * contained in each field of the message. The message is
		 * then forwared to all other clients.
		 * 
		 * @throws IOException
		 */
		protected void receiveReadAndForwardMessage()
				throws IOException {
			// Receive packet
			gamePlaySocket.receive(recPack);

			// Create streams
			ByteArrayInputStream bais = new ByteArrayInputStream(
					recPack.getData());
			DataInputStream dis = new DataInputStream(bais);

			// Read message fields
			dis.read(ipBytes);
			port = dis.readInt();
			code = dis.readInt();
			x = dis.readInt();
			y = dis.readInt();
			heading = dis.readInt();

			// Get id for the client that sent the message
			id = new InetSocketAddress(
					InetAddress.getByAddress(ipBytes), port);

			// Forward to all clients except the one that sent it.
			selectiveForward(recPack, id, gamePlaySocket);

		} // end receiveReadAndForwardMessage

		/**
		 * Updates the sector display and check for collisions. If
		 * it determines a collision has occurred. it sends remove
		 * messages to all clients.
		 */
		protected void updateDisplay() {
			if (code == Constants.JOIN
					|| code == Constants.UPDATE_SHIP) {

				// Create a temp spacecraft for adding to the
				// sector display
				// or for updating.
				SpaceCraft ship = new SpaceCraft(id, x, y,
						heading);

				// Update the sector display
				sector.updateOrAddSpaceCraft(ship);

				// Check to see if any collisions have occurred
				ArrayList<SpaceCraft> destroyed = sector
						.collisionCheck(ship);

				// Send remove information if something was
				// destroyed in a
				// collision.
				if (destroyed != null) {

					for (SpaceCraft sc : destroyed) {
						sendRemove(sc);
					}
				}
			} else {
				System.out
						.println("Unknown UDP message received. Code: "
								+ code);
			}

		} // end updateDisplay

	} // end gamePlayServer class

	/**
	 * Task which periodically updates the torpedoes that are in
	 * the sector and determines if they have hit anything.
	 * 
	 * @author bachmaer
	 */
	class TorpdedoUpdater extends TimerTask {
		/**
		 * Socket through which all torpedo update messages will
		 * be sent
		 */
		DatagramSocket dgsock;

		/**
		 * Creates a DatagramSocket that is used to send update
		 * mesages.
		 */
		public TorpdedoUpdater() {

			try {
				dgsock = new DatagramSocket();
			} catch (SocketException e) {
				System.err
						.println("Could not create Datagram Socket for torpedo updater.");
				System.exit(0);
			}
		} // end TorpdedoUpdater constructor

		/**
		 * run method that will be called periodically by a Timer
		 * (sub-class of thread). It updates all torpedoes in the
		 * sector and then sends updates message to all clients to
		 * provide them with the new positions of the torpedoes.
		 * Additionally it sends remove messages for torpedoes and
		 * ships. Torpedoes are removed when they reach the end of
		 * their life or hit a ship. Ships are removed if they are
		 * hit by torpedoes.
		 */
		public void run() {

			// Move all torpedoes and determine if they hit
			// anything
			ArrayList<SpaceCraft> destroyed = sector
					.updateTorpedoes();

			// Send remove messages for any ships of torpedoes
			// that are no longer in the game.
			if (destroyed != null) {

				for (SpaceCraft sc : destroyed) {

					sendRemove(sc);
				}
			}

			// Access the torpedoes that are still in the sector
			Vector<Torpedo> remainingTorpedoes = sector
					.getTorpedoes();

			// Send update messages for torpedoes that are still
			// in the game
			for (Torpedo t : remainingTorpedoes) {

				sendTorpedoUpdate(t, dgsock);
			}

			// Check to see if the game has ended
			if (playing == false) {
				this.cancel();
				dgsock.close();
			}

		} // end run

	} // end TorpdedoUpdater class

	/**
	 * Driver for starting the server.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new SpaceGameServer();

	} // end main

} // end SpaceGameServer class

