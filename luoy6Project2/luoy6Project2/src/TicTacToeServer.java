/**
 * Yu Luo
 * Instructor: Dr. Bachmann
 * CSE 283, Section B
 *
 * This is the server program for this Client/Server version of
 * TicTacToe game. 
 *
 * All aspects of this program were developed by Yu Luo
 */
import java.io.*;
import java.net.*;
import java.util.Random;

/**
 * This is the TicTacToe server. The server will play Tic Tac Toe with
 * client, and it will also calculate and send status code back to the
 * client
 * 
 * @author Yu Luo
 * 
 */
public class TicTacToeServer {

	public static final int SERVER_PORT = 32100;
	public static int OK_CODE = 10;
	public static int FULL_CODE = 20;
	public static int SERVER_WIN_CODE = 30;
	public static int CLIENT_WIN_CODE = 40;
	public static int ILLEGAL_MOVE_CODE = 50;
	protected ServerSocket serverSocket = null;
	protected boolean listen = true;


	/**
	 * Constructs and initializes a TicTacToeServer. It creates a
	 * ServerSocket to use for listening for clients. Displays the
	 * port number and IP address for the ServerSocket to the console.
	 */
	public TicTacToeServer() {
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			displayContactInfo();
		} catch (IOException e) {
			// Port not available
			System.err.println("Could not listen on port: "
					+ SERVER_PORT);
			System.exit(-1);
		}

		listining();

		try {
			serverSocket.close();
		} catch (IOException e) {
			System.err.println("Unable to close serverSoccket.");
		}

	}


	/**
	 * Constructor for the TicTacToeServer class. It enters an
	 * infinite loop to listen for clients. Spawns a new
	 * TicTacToeThread object to handle each client.
	 */
	protected void listining() {
		do {
			// Create new thread to hand each client.
			// Pass the Socket object returned by the accept
			// method to the thread.
			try {

				new TicTacToeServerThread(serverSocket.accept())
						.start();
			} catch (IOException e) {
				System.err.println("Terminating with client");
			}
		} while (listen);
	}


	/**
	 * Creates a server socket with a port number 32100
	 * 
	 * @throws IOException
	 */
	protected void createServerSocket() throws IOException {
		try {
			this.serverSocket = new ServerSocket(SERVER_PORT);
		} catch (IOException e) {
			System.err.println("Unable to create server socket");
			throw e;
		}
	}


	/**
	 * Display information of the server
	 * 
	 * @throws UnknownHostException
	 */
	protected void displayContactInfo() throws UnknownHostException {
		try {
			System.out.println("Number Server standing by to accept "
					+ "Clients:" + "\nIP : "
					+ InetAddress.getLocalHost() + "\nPort: "
					+ serverSocket.getLocalPort() + "\n\n");
		} catch (UnknownHostException e) {
			System.err.println("Unable to display client contact"
					+ " information");
			throw e;
		}
	}


	/**
	 * Closes the server
	 * 
	 * @throws IOException
	 */
	protected void closeServer() throws IOException {
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			System.err.println("Unable to close server");
			throw e;
		}
	}


	public static void main(String[] args) {
		new TicTacToeServer();
	}

}

/**
 * Class that defines a runnable object (extends Thread) that can
 * handle a single client.
 * 
 */
class TicTacToeServerThread extends Thread {
	protected Socket clientSocket = null;
	protected DataOutputStream dos = null;
	protected DataInputStream dis = null;
	private int clientInputRow;
	private int clientInputCol;
	private int statuscode;
	private int serverInputRow;
	private int serverInputCol;
	public int[][] board = new int[3][3];


	/**
	 * Sets up class data members. Displays communication info.
	 */
	public TicTacToeServerThread(Socket socket) {

		// Call the super class (Thread) constructor.
		super();

		// Save a reference to the Socket connection
		// to the client.
		this.clientSocket = socket;

	}


	/**
	 * 
	 * Calling this method will causes a separate executing thread. It
	 * contains all the methods needed to play a game.
	 */
	public void run() {
		try {

			createClientStreams();
			
			
			do {
				this.receiveClientMove();
				this.validateAndUpdateClientMove();
				this.sendAndUpdateServerMove();
				this.validateServerMove();
			} while (!isGameOver());
			System.out.println("After game over");
			this.closeClientConnection();
			this.clientSocket.close();

		} catch (IOException e) {
			System.err.println("Something is wrong. Ending the game");
		}

	}// end run


	/**
	 * Reset the board so that every item on the board is 0, meaning
	 * that there are not any moves yet.
	 */
	protected void reset() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				board[i][j] = 0;
			}
		}
	}


	/**
	 * Check to see if a game is over
	 * 
	 * @return true if the game is over, false otherwise.
	 */
	protected boolean isGameOver() {

		System.out.println("checking if game is over");

		if (this.statuscode != TicTacToeServer.OK_CODE) {

			System.out.println("Returning in game over 1");
			return true;

		} else if (this.clientInputRow < 0 || this.clientInputCol < 0) {
			System.out.println("Returning in game over 2");
			return true;
		} else if (isFull()) {
			System.out.println("Returning in game over 3");
			return true;
		} else if (isClientWin()) {
			System.out.println("Returning in game over 4");
			return true;
		} else if (isServerWin()) {
			System.out.println("Returning in game over 4");
			return true;
		} else {
			return false;
		}
	}


	/**
	 * Assigns status code to each possible scenario.
	 */
	protected void assignStatusCode() {
		if (isFull()) {
			this.statuscode = TicTacToeServer.FULL_CODE;
			System.out.println("Checking full");
		} else if (this.clientInputRow > 2 || this.clientInputCol > 2) {
			this.statuscode = TicTacToeServer.ILLEGAL_MOVE_CODE;
		} else if (isClientWin()) {
			this.statuscode = TicTacToeServer.CLIENT_WIN_CODE;
		} else if (isServerWin()) {
			this.statuscode = TicTacToeServer.SERVER_WIN_CODE;
		} else {
			this.statuscode = TicTacToeServer.OK_CODE;
		}
	}


	/**
	 * Receives client moves
	 * 
	 * @throws IOException
	 */
	protected void receiveClientMove() throws IOException {
		try {
			this.clientInputRow = dis.readInt();
			this.clientInputCol = dis.readInt();
			System.out.println("Received Client row "
					+ this.clientInputRow + " column "
					+ this.clientInputCol);
		} catch (IOException e) {
			System.err.println("Unable to receive client move");
			throw e;
		}

	}


	/**
	 * Validates client's move. If client's move passes the
	 * validation, then updates the board in server
	 * 
	 * @throws IOException
	 */
	protected void validateAndUpdateClientMove() throws IOException {
		this.assignStatusCode();
		if (this.clientInputRow < 0 || this.clientInputCol < 0
				|| board[clientInputRow][clientInputCol] != 0) {
			this.statuscode = TicTacToeServer.ILLEGAL_MOVE_CODE;
		}

		try {
			dos.writeInt(statuscode);
			System.out.println("Status code = " + this.statuscode);
		} catch (IOException e1) {
			System.err
					.println("Unable to write status code to client");
			throw e1;
		}

		if (this.statuscode == TicTacToeServer.OK_CODE) {
			board[this.clientInputRow][this.clientInputCol] = -1;
			System.out.println();
			this.printBoard();

		}
	}


	/**
	 * Sends server moves to the client
	 * 
	 * @throws IOException
	 */
	protected void sendAndUpdateServerMove() throws IOException {
		Random r = new Random();
		if (this.statuscode == TicTacToeServer.OK_CODE) {
			do {
				this.serverInputRow = r.nextInt(3);
				this.serverInputCol = r.nextInt(3);

			} while (board[this.serverInputRow][this.serverInputCol] 
					!= 0);

			System.out.println("Server move row: "
					+ this.serverInputRow + " col = "
					+ this.serverInputCol);
			board[this.serverInputRow][this.serverInputCol] = 1;
			System.out.println();
			this.printBoard();
			try {
				dos.writeInt(this.serverInputRow);
				dos.writeInt(this.serverInputCol);
			} catch (IOException e) {
				System.err.println("Unable to send server moves");
				throw e;
			}
		}
	}


	/**
	 * Validates server moves and sends status code about server's
	 * move back to the client
	 * 
	 * @throws IOException
	 */
	protected void validateServerMove() throws IOException {
		this.assignStatusCode();
		try {
			dos.writeInt(this.statuscode);
			System.out.println("status code = " + this.statuscode);
		} catch (IOException e) {
			System.err.println("Unable to send status code of server"
							+ " move to client");
			throw e;
		}
	}


	/**
	 * Determines if the client wins the game
	 * 
	 * @return true if the client wins, false otherwise.
	 */
	protected boolean isClientWin() {
		boolean isClientWin = false;
		if (board[0][0] == -1 && board[0][1] == -1
				&& board[0][2] == -1 || board[1][0] == -1
				&& board[1][1] == -1 && board[1][2] == -1
				|| board[2][0] == -1 && board[2][1] == -1
				&& board[2][2] == -1 || board[0][0] == -1
				&& board[1][0] == -1 && board[2][0] == -1
				|| board[0][1] == -1 && board[1][1] == -1
				&& board[2][1] == -1 || board[0][2] == -1
				&& board[1][2] == -1 && board[2][2] == -1
				|| board[0][0] == -1 && board[1][1] == -1
				&& board[2][2] == -1 || board[0][2] == -1
				&& board[1][1] == -1 && board[2][0] == -1) {
			isClientWin = true;

		}
		return isClientWin;
	}


	/**
	 * Determines if the server wins the game
	 * 
	 * @return true if the server wins, false otherwise.
	 */
	protected boolean isServerWin() {
		boolean isServerWin = false;
		if (board[0][0] == 1 && board[0][1] == 1 && board[0][2] == 1
				|| board[1][0] == 1 && board[1][1] == 1
				&& board[1][2] == 1 || board[2][0] == 1
				&& board[2][1] == 1 && board[2][2] == 1
				|| board[0][0] == 1 && board[1][0] == 1
				&& board[2][0] == 1 || board[0][1] == 1
				&& board[1][1] == 1 && board[2][1] == 1
				|| board[0][2] == 1 && board[1][2] == 1
				&& board[2][2] == 1 || board[0][0] == 1
				&& board[1][1] == 1 && board[2][2] == 1
				|| board[0][2] == 1 && board[1][1] == 1
				&& board[2][0] == 1) {
			isServerWin = true;

		}
		return isServerWin;
	}


	/**
	 * Determines if the board is full
	 * 
	 * @return true if the board is full, false otherwise.
	 */
	protected boolean isFull() {
		boolean isFull = true;

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] == 0) {
					isFull = false;
					j = board[0].length - 1;
					i = board.length - 1;
				}
			}
		}
		return isFull;
	}


	/**
	 * Creates client streams
	 * 
	 * @throws IOException
	 */
	protected void createClientStreams() throws IOException {
		try {
			dos = new DataOutputStream(
					this.clientSocket.getOutputStream());
			dis = new DataInputStream(
					this.clientSocket.getInputStream());
		} catch (IOException e) {
			System.err.println("Couldn't make stream connection"
					+ " with client");
			throw e;
		}
	}


	/**
	 * Closes client streams and client sockets.
	 */
	protected void closeClientConnection() {
		try {
			this.clientSocket.close();
			dis.close();
			dos.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}


	/**
	 * Prints the current board on the screen
	 */
	protected void printBoard() {
		for (int r = 0; r < board.length; r++) {
			System.out.print("|");
			for (int c = 0; c < board[0].length; c++) {
				if (board[r][c] == -1) {
					System.out.print(" X|");
				} else if (board[r][c] == 1) {
					System.out.print(" O|");
				} else {
					System.out.print(" _|");
				}
			}
			System.out.println();
		}
	}// end printBoard

} // end TCPListenThread class
