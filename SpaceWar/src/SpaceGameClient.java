import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.dgc.DGC;
import java.util.ArrayList;

import javax.sound.sampled.ReverbType;

import spaceWar.*;

/**
 * Yu Luo
 * Instructor: Dr. Bachmann
 * CSE 283, Section B
 *
 * This class provide a game to game. It handles one player within
 * the space game
 */

/**
 * 
 *         Driver class for a simple networked space game.
 *         Opponents try to destroy each other by ramming. Head on
 *         collisions destroy both ships. Ships move and turn
 *         through GUI mouse clicks. All friendly and alien ships
 *         are displayed on a 2D interface.
 */
public class SpaceGameClient implements SpaceGUIInterface {
	// Keeps track of the game state
	Sector sector;

	// User interface
	SpaceGameGUI gui;

	// IP address and port to identify ownship and the
	// DatagramSocket being used for game play messages.
	InetSocketAddress ownShipID;

	// Socket for sending and receiving
	// game play messages.
	DatagramSocket gamePlaySocket;

	// Socket used to register and to receive remove information
	// for ships
	Socket reliableSocket;

	// Set to false to stops all receiving loops
	boolean playing = true;

	// DataInputStream and DataOutputstream for TCP
	DataInputStream dis = null;
	DataOutputStream dos = null;

	static final boolean DEBUG = false;

	/**
	 * Creates all components needed to start a space game.
	 * Creates Sector canvas, GUI interface, a Sender object for
	 * sending update messages, a Receiver object for receiving
	 * messages.
	 * 
	 * @throws UnknownHostException
	 */
	public SpaceGameClient() {
		// Create UDP Datagram Socket for sending and receiving
		// game play messages.
		try {
			gamePlaySocket = new DatagramSocket();

		} catch (SocketException e) {
			System.err
					.println("Error creating game play datagram socket.");
			System.exit(0);
		}

		// Instantiate ownShipID using the DatagramSocket port
		// and the local IP address.
		try {

			ownShipID = new InetSocketAddress(
					InetAddress.getLocalHost(),
					gamePlaySocket.getLocalPort());
		} catch (UnknownHostException e) {
			System.err
					.println("Error creating ownship ID. Exiting.");
			System.exit(0);
		}

		// Create display, ownPort is used to uniquely identify
		// the
		// controlled entity.
		sector = new Sector(ownShipID);

		// gui will call SpaceGame methods to handle user events
		gui = new SpaceGameGUI(this, sector);

		// creating TCP socket for reliable connection with the
		// server
		try {
			reliableSocket = new Socket(Constants.SERVER_IP,
					Constants.SERVER_PORT);
		} catch (IOException e1) {
			System.err.println("Error create reliable socket");
		}

		// Call a method that uses TCP/IP to register with the
		// server
		// and receive obstacles from the server.
		// TODO

		registerToServer();

		// Infinite loop or separate thread to receive update and
		// join
		// messages from the server and use the messages to
		// update the sector display

		// TODO

		new UDPListener().start();

		// Infinite loop or separate thread to receive remove
		// messages from the server and use the messages to
		// update the sector display
		// TODO
		new removeListener().start();
		


	} // end SpaceGame constructor

	/**
	 * This class listen for remove messages using the TCP socket.
	 */
	protected class removeListener extends Thread {
		DatagramPacket recPack = new DatagramPacket(
				new byte[24], 24);

		// Data members for holding values contained in the fields
		// of
		// received messages
		protected byte ipBytes[] = new byte[4];
		protected int port, code, x, y, heading;
		protected InetSocketAddress id;

		/**
		 * This method listen for remove messages with on one player
		 */
		public void run() {
			while (playing) {
				reiceveRemoveMessage();
				updateDisplay();
				System.out.println("After update display");
			}
		}

		/**
		 * Update the sector's current display
		 */
		private void updateDisplay() {
			if (code == Constants.REMOVE_TORPEDO) {

				Torpedo torp = new Torpedo(id, x, y, heading);
				sector.removeTorpedo(torp);
			}

			else if (code == Constants.REMOVE_SHIP) {

				SpaceCraft sc = new SpaceCraft(id);
				sector.removeSpaceCraft(sc);
			}
		}

		/**
		 * Receive remove message from the server
		 */
		private void reiceveRemoveMessage() {
			try {

				dis.read(ipBytes);
				port = dis.readInt();
				code = dis.readInt();
				System.out.println("port = " + port + " code = "
						+ code);
				id = new InetSocketAddress(
						InetAddress.getByAddress(ipBytes), port);
			} catch (IOException e) {
				System.err
						.println("Error receiving remove message");
			}
			System.out.println("Within remove message");
		}
	}

	/**
	 * 
	 * This class receive UDP update and join messages and use
	 * information to update the sector display
	 * 
	 */
	protected class UDPListener extends Thread {

		// DatagramPacket for receiving updates. All updates are
		// 24 bytes.
		DatagramPacket recPack = new DatagramPacket(
				new byte[24], 24);

		// Data members for holding values contained in the fields
		// of
		// received messages
		protected byte ipBytes[] = new byte[4];
		protected int port, code, x, y, heading;
		protected InetSocketAddress id;

		/**
		 * This method listen for update messages and update 
		 * displays
		 */
		public void run() {
			do {
				receiveUpdateMessage();
				updateDisplay();
				

			} while (playing);
		}

		/**
		 * Updates the sector display and check for collisions. If
		 * it determines a collision has occurred. it sends remove
		 * messages to all clients.
		 */
		protected void updateDisplay() {
			if (code == Constants.UPDATE_TORPEDO) {

				Torpedo torpedo = new Torpedo(id, x, y, heading);
				sector.updateOrAddTorpedo(torpedo);

			} else {

				SpaceCraft ship = new AlienSpaceCraft(id, x, y,
						heading);

				// Update the sector display
				sector.updateOrAddSpaceCraft(ship);

			}

		} // end updateTorpedo

		/**
		 * Receive torpedo update messages and update displays
		 */
		public void receiveUpdateMessage() {

			try {
				gamePlaySocket.receive(recPack);
				// Create streams
				ByteArrayInputStream bais = new ByteArrayInputStream(
						recPack.getData());
				DataInputStream diis = new DataInputStream(bais);

				// Read message fields
				diis.read(ipBytes);
				port = diis.readInt();
				code = diis.readInt();
				x = diis.readInt();
				y = diis.readInt();
				heading = diis.readInt();
			} catch (IOException e) {
				System.err
						.println("Error receiving torpedo updates");
			}

			// Get id for the client that sent the message
			try {
				id = new InetSocketAddress(
						InetAddress.getByAddress(ipBytes), port);
			} catch (UnknownHostException e) {
				System.err
						.println("Can not get ip address in "
								+ "receiveTopedoUpdate");
			}
		}

	}

	/**
	 * This method registers a player to the server
	 */
	private void registerToServer() {

		// Streaming with the server
		try {
			dos = new DataOutputStream(
					reliableSocket.getOutputStream());
			// sending register code
			dos.writeInt(Constants.REGISTER);
			// sending local datagramSocket as int
			dos.writeInt(gamePlaySocket.getLocalPort());

			dis = new DataInputStream(
					reliableSocket.getInputStream());

			// receiving obstacles from server
			int flag = 0;
			do {
				flag = dis.readInt();
				if (flag != -1) {
					int y = dis.readInt();
					sector.addObstacle(flag, y);
				}
			} while (flag != -1);

		} catch (IOException e) {
			System.err.println("Error streaming with server");
		}

	}

	/**
	 * Causes sector.ownShip to turn and sends an update message
	 * for the heading change.
	 */
	public void turnRight() {
		if (sector.ownShip != null) {

			if (DEBUG)
				System.out.println(" Right Turn ");

			// Update the display
			sector.ownShip.rightTurn();

			// Send update message to server with new heading.
			// TODO
			doUpdateShip(Constants.UPDATE_SHIP);
		}

	} // end turnRight

	/**
	 * Causes sector.ownShip to turn and sends an update message
	 * for the heading change.
	 */
	public void turnLeft() {
		// See if the player has a ship in play
		if (sector.ownShip != null) {

			if (DEBUG)
				System.out.println(" Left Turn ");

			// Update the display
			sector.ownShip.leftTurn();

			// Send update message to other server with new
			// heading.
			// TODO
			doUpdateShip(Constants.UPDATE_SHIP);
		}

	} // end turnLeft

	/**
	 * Causes sector.ownShip to turn and sends an update message
	 * for the heading change.
	 */
	public void fireTorpedo() {
		// See if the player has a ship in play
		if (sector.ownShip != null) {

			if (DEBUG)
				System.out
						.println("Informing server of new torpedo");

			// Make a TCP connection to the server and send
			// torpedo information
			// TODO
			try {
				Socket s = new Socket(Constants.SERVER_IP,
						Constants.SERVER_PORT);
				DataOutputStream doos = new DataOutputStream(
						s.getOutputStream());

				doos.writeInt(Constants.FIRED_TORPEDO);
				int port = gamePlaySocket.getLocalPort();
				doos.writeInt(port);
				System.out.println("Local port = " + port);

				doos.writeInt(sector.ownShip.getXPosition());
				doos.writeInt(sector.ownShip.getYPosition());
				doos.writeInt(sector.ownShip.getHeading());

				doos.close();
				s.close();

			} catch (IOException e) {
				System.err
						.println("Error writing x and y "
								+ "positions when firing Torpedos");
			}

		}

	} // end turnLeft

	/**
	 * Causes sector.ownShip to move forward and sends an update
	 * message for the position change. If there is an obstacle in
	 * front of the ship it will not move forward and a message is
	 * not sent.
	 */
	public void moveFoward() {
		// Check if the player has and unblocked ship in the game
		if (sector.ownShip != null && sector.clearInfront()) {

			if (DEBUG)
				System.out.println(" Move Forward");

			// Update the displayed position of the ship
			sector.ownShip.moveForward();

			// Send a message with the updated position to server
			// TODO
			doUpdateShip(Constants.UPDATE_SHIP);
		}

	} // end moveFoward

	/**
	 * Causes sector.ownShip to move forward and sends an update
	 * message for the position change. If there is an obstacle in
	 * front of the ship it will not move forward and a message is
	 * not sent.
	 */
	public void moveBackward() {
		// Check if the player has and unblocked ship in the game
		if (sector.ownShip != null && sector.clearBehind()) {

			if (DEBUG)
				System.out.println(" Move Backward");

			// Update the displayed position of the ship
			sector.ownShip.moveBackward();

			// Send a message with the updated position to server
			// TODO
			doUpdateShip(Constants.UPDATE_SHIP);
		}

	} // end moveFoward

	/**
	 * This is a helper method that do update ship message This
	 * will be used each time the user makes a move by pressing
	 * applicable buttons or keys
	 */
	public void doUpdateShip(int code) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {

			dos.write(ownShipID.getAddress().getAddress());
			dos.writeInt(gamePlaySocket.getLocalPort());
			dos.writeInt(code);
			dos.writeInt(sector.ownShip.getXPosition());
			dos.writeInt(sector.ownShip.getYPosition());
			dos.writeInt(sector.ownShip.getHeading());
		} catch (IOException e) {
			System.err.println("Error joining the game");
		}
		DatagramPacket dgp = new DatagramPacket(
				baos.toByteArray(), baos.size());
		dgp.setAddress(Constants.SERVER_IP);
		dgp.setPort(Constants.SERVER_PORT);
		try {
			gamePlaySocket.send(dgp);
		} catch (IOException e) {
			System.err.println("Error sending join message");
		}
	}

	/**
	 * Creates a new sector.ownShip if one does not exist. Sends a
	 * join message for the new ship.
	 * 
	 */
	public void join() {
		if (sector.ownShip == null) {

			if (DEBUG)
				System.out.println(" Join ");

			// Add a new ownShip to the sector display
			sector.createOwnSpaceCraft();

			// Send message to server let them know you have
			// joined the game
			// using the
			// send object
			// TODO
			doUpdateShip(Constants.JOIN);
		}

	} // end join

	/**
	 * Perform clean-up for application shut down
	 */
	public void stop() {
		if (DEBUG)
			System.out.println("stop");

		// Stop all thread and close all streams and sockets
		playing = false;

		// Inform the server that the client is leaving the game
		// TODO

		try {
			Socket s = new Socket(Constants.SERVER_IP,
					Constants.SERVER_PORT);
			DataOutputStream doos = new DataOutputStream(
					s.getOutputStream());
			doos.writeInt(Constants.EXIT);
			doos.writeInt(gamePlaySocket.getLocalPort());
			doos.close();
			s.close();
		} catch (IOException e) {
			System.err.println("Error exiting fromt he server");
		}

	} // end stop

	/*
	 * Starts the space game. Driver for the application.
	 */
	public static void main(String[] args) {
		new SpaceGameClient();

	} // end main

} // end SpaceGame class
