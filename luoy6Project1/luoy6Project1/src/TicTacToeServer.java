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
 * This is the TicTacToe server. The server will play Tic Tac Toe with client,
 * and it will also calculate and send status code back to the client
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
	ServerSocket serverSocket = null;

	Socket clientSocket = null;

	DataOutputStream dos = null;
	DataInputStream dis = null;
	private int clientInputRow;
	private int clientInputCol;
	private int statuscode;
	private int serverInputRow;
	private int serverInputCol;
	public int[][] board = new int[3][3];

	/**
	 * Constructs and initializes a TicTacToeServer
	 */
	public TicTacToeServer() {
		this.createServerSocket();
		this.displayContactInfo();
		this.printBoard();
		this.playGames();
		this.closeServer();
	}

	protected void playGames() {
		do {
			try {
				handleOneClient();
			} catch (Exception e) {
				System.err.println("Terminating the game with client.");
			}
		} while (true);
	}

	/**
	 * Check to see if a game is over
	 * 
	 * @return true if the game is over, false otherwise.
	 */
	public boolean isGameOver() {
		System.out.println("checking if game is over");
		if (this.statuscode != TicTacToeServer.OK_CODE) {
			System.out.println("Returning in game over 1");
			return true;
		} else if (this.clientInputRow < 0 || this.clientInputCol < 0) {
			System.out.println("Returning in game over 2");
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Creates a server socket with a port number 32100
	 */
	public void createServerSocket() {
		try {
			this.serverSocket = new ServerSocket(SERVER_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Display information of the server
	 */
	public void displayContactInfo() {
		try {
			System.out.println("Number Server standing by to accept "
					+ "Clients:" + "\nIP : " + InetAddress.getLocalHost()
					+ "\nPort: " + serverSocket.getLocalPort() + "\n\n");
		} catch (UnknownHostException e) {
			System.err.println("Unable to display client contact information");
		}
	}

	/**
	 * Assigns status code to each possible scenario.
	 */
	public void assignStatusCode() {
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
	 * Assigns status code to each possible scenario.
	 */
	public void handleOneClient() throws Exception {

		this.clientSocket = new Socket();
		this.clientSocket = serverSocket.accept();
		this.createClientStreams();
		do {
			this.receiveClientMove();
			this.validateAndUpdateClientMove();
			this.sendAndUpdateServerMove();
			this.validateServerMove();
		} while (!isGameOver());
		this.closeClientConnection();
		this.clientSocket.close();
	}

	/**
	 * Creates client streams
	 * 
	 * @throws IOException
	 */
	public void createClientStreams() throws IOException {
		try {
			dos = new DataOutputStream(this.clientSocket.getOutputStream());
			dis = new DataInputStream(this.clientSocket.getInputStream());
		} catch (IOException e) {
			System.err.println("Couldn't make stream connection with client");
			throw e;
		}
	}

	/**
	 * Receives client moves
	 */
	public void receiveClientMove() {
		try {
			this.clientInputRow = dis.readInt();
			this.clientInputCol = dis.readInt();
			if (board[clientInputRow][clientInputCol] == 1
					|| board[clientInputRow][clientInputCol] == -1) {
				this.statuscode = TicTacToeServer.ILLEGAL_MOVE_CODE;
			}
			System.out.println("Received Client row " + this.clientInputRow
					+ " column " + this.clientInputCol);
		} catch (IOException e) {
			System.out.println("Unable to receive client move");
		}

	}

	/**
	 * Validates client's move. If client's move passes the validation, then
	 * updates the board in server
	 * 
	 * @throws IOException
	 */
	public void validateAndUpdateClientMove() throws IOException {
		this.assignStatusCode();
		try {
			dos.writeInt(statuscode);

		} catch (IOException e1) {
			System.err.println("Unable to write status code to client");
			throw e1;
		}

		System.out.println("Status code = " + this.statuscode);

		if (this.statuscode == TicTacToeServer.OK_CODE) {
			board[this.clientInputRow][this.clientInputCol] = -1;
			System.out.println();
			this.printBoard();
		} else {
			this.closeClientConnection();
		}
	}

	/**
	 * Sends server moves to the client
	 */
	public void sendAndUpdateServerMove() {
		Random r = new Random();
		if (this.statuscode == TicTacToeServer.OK_CODE) {
			do {
				this.serverInputRow = r.nextInt(3);
				this.serverInputCol = r.nextInt(3);

			} while (board[this.serverInputRow][this.serverInputCol] != 0);

			System.out.println("Server move row: " + this.serverInputRow
					+ " col = " + this.serverInputCol);
			board[this.serverInputRow][this.serverInputCol] = 1;
			System.out.println();
			this.printBoard();
			try {
				dos.writeInt(this.serverInputRow);
				dos.writeInt(this.serverInputCol);
			} catch (IOException e) {
				System.out.println("Unable to send server moves");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Validates server moves and sends status code about server's move back to
	 * the client
	 */
	public void validateServerMove() {
		this.assignStatusCode();

		try {
			dos.writeInt(this.statuscode);
			System.out.println("status code = " + this.statuscode);
		} catch (IOException e) {
			System.out.println("Unable to send status code to client");
		}

		if (this.statuscode != TicTacToeServer.OK_CODE) {
			this.closeClientConnection();
		}
	}

	/**
	 * Determines if the client wins the game
	 * 
	 * @return true if the client wins, false otherwise.
	 */
	public boolean isClientWin() {
		boolean isClientWin = false;
		if (board[0][0] == -1 && board[0][1] == -1 && board[0][2] == -1
				|| board[1][0] == -1 && board[1][1] == -1 && board[1][2] == -1
				|| board[2][0] == -1 && board[2][1] == -1 && board[2][2] == -1
				|| board[0][0] == -1 && board[1][0] == -1 && board[2][0] == -1
				|| board[0][1] == -1 && board[1][1] == -1 && board[2][1] == -1
				|| board[0][2] == -1 && board[1][2] == -1 && board[2][2] == -1
				|| board[0][0] == -1 && board[1][1] == -1 && board[2][2] == -1
				|| board[0][2] == -1 && board[1][1] == -1 && board[2][0] == -1) {
			isClientWin = true;

		}
		return isClientWin;
	}

	/**
	 * Determines if the server wins the game
	 * 
	 * @return true if the server wins, false otherwise.
	 */
	public boolean isServerWin() {
		boolean isServerWin = false;
		if (board[0][0] == 1 && board[0][1] == 1 && board[0][2] == 1
				|| board[1][0] == 1 && board[1][1] == 1 && board[1][2] == 1
				|| board[2][0] == 1 && board[2][1] == 1 && board[2][2] == 1
				|| board[0][0] == 1 && board[1][0] == 1 && board[2][0] == 1
				|| board[0][1] == 1 && board[1][1] == 1 && board[2][1] == 1
				|| board[0][2] == 1 && board[1][2] == 1 && board[2][2] == 1
				|| board[0][0] == 1 && board[1][1] == 1 && board[2][2] == 1
				|| board[0][2] == 1 && board[1][1] == 1 && board[2][0] == 1) {
			isServerWin = true;

		}
		return isServerWin;
	}

	/**
	 * Determines if the board is full
	 * 
	 * @return true if the board is full, false otherwise.
	 */
	public boolean isFull() {
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
	 * Closes client streams and client sockets.
	 */
	public void closeClientConnection() {
		try {
			this.clientSocket.close();
			dis.close();
			dos.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	/**
	 * Closes the server
	 */
	public void closeServer() {
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints the current board on the screen
	 */
	public void printBoard() {
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

	public static void main(String[] args) {
		new TicTacToeServer();
	}

}
