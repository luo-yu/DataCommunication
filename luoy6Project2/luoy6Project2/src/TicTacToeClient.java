/**
 * Yu Luo
 * Instructor: Dr. Bachmann
 * CSE 283, Section B
 *
 * This is the client program for this Client/Server version of
 * TicTacToe game. 
 *
 * All aspects of this program were developed by Yu Luo
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * This is the client program for this Tic Tac Toe Game. The client
 * will always have the first move. The client will be able to quit
 * the program if he or she enters negative value
 * 
 * @author Yu Luo
 * 
 */
public class TicTacToeClient {
	private DataOutputStream dos = null;
	private DataInputStream dis = null;
	private int clientInputRow;
	private int clientInputCol;
	private int serverInputRow;
	private int serverInputCol;
	private int statusCode;
	private Scanner input = new Scanner(System.in);
	public int[][] board = new int[3][3];
	Socket s = null;

	private InetAddress serverIP = null;


	/**
	 * Constructs and initializes TicTacToeClient
	 */
	public TicTacToeClient() {
		this.readConsole();
		this.connectToServer();
		this.associateStream();
		this.playGame();
		this.gameMessage();
		this.closeSocket();

	}


	protected void playGame() {
		this.printBoard();
		// Send initial client move
		sendMove();
		// receive initial status code about the initial client move
		receiveClientMoveCode();
		this.printBoard();

		// as long as the game is not over, keep sending and
		// receiving moves and update board
		while (!isGameOver()) {
			updateClientMove();
			this.printBoard();
			System.out.println();
			this.receiveServerMove();
			this.updateServerMove();
			this.printBoard();
			this.receiveServerMoveCode();
			if (this.statusCode == TicTacToeServer.OK_CODE) {
				sendMove();
				receiveClientMoveCode();
			}
		}
	}


	/**
	 * Check to see if a game is over
	 * 
	 * @return true if the game is over, false otherwise.
	 */
	protected boolean isGameOver() {
		if (this.statusCode != TicTacToeServer.OK_CODE) {
			return true;
		} else {
			return false;
		}
	}


	/**
	 * Puts the client move on the client board
	 */
	protected void updateClientMove() {
		if (this.statusCode == TicTacToeServer.OK_CODE) {
			board[this.clientInputRow][this.clientInputCol] = -1;
		} else {
			this.gameMessage();
		}
	}


	/**
	 * puts the server move on the client board
	 */
	protected void updateServerMove() {
		if (this.statusCode == TicTacToeServer.OK_CODE) {
			board[this.serverInputRow][this.serverInputCol] = 1;
		} else {
			this.gameMessage();
		}
	}


	/**
	 * Asks user inputs for ip address, enters 1 if the client wants
	 * to use default ip address 127.0.0.1
	 */
	public void readConsole() {
		System.out.print("Enter the IP address of the server,"
				+ " or enter 1 to use the default ip address: ");
		String ip = input.next();
		if (ip.equalsIgnoreCase("1"))
			ip = "127.0.0.1";
		try {
			serverIP = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("No Such IP Address");
		}
	}


	/**
	 * Connects the client to the server
	 */
	protected void connectToServer() {

		try {
			s = new Socket(serverIP, TicTacToeServer.SERVER_PORT);
		} catch (IOException e) {
			System.out
					.println("Please check if you have the correct "
							+ "server IP or Port number");
			e.printStackTrace();
		}
	}


	/**
	 * Sends client moves to the server
	 */
	protected void sendMove() {
		System.out
				.println("Your Move (Enter negative number to quit)");

		do {
			System.out.print("Enter row: ");
			this.clientInputRow = input.nextInt();
		} while (this.clientInputRow > 2);

		do {
			System.out.print("Enter Column: ");
			this.clientInputCol = input.nextInt();
		} while (this.clientInputCol > 2);
		try {
			dos.writeInt(this.clientInputRow);
			dos.writeInt(this.clientInputCol);
			System.out.println("Wroted row and col");
		} catch (IOException e) {
			System.out.println("Unable to send client inputs");
		}
	}


	/**
	 * Receives status code from the server. This is the status code
	 * for client move
	 */
	protected void receiveClientMoveCode() {
		try {
			this.statusCode = dis.readInt();
		} catch (IOException e) {
			System.out.println("Unable to read the status code for"
					+ " client's move");
		}
	}


	/**
	 * Receives server move
	 */
	protected void receiveServerMove() {

		try {
			this.serverInputRow = dis.readInt();
			this.serverInputCol = dis.readInt();
			System.out.println("Computer chose row "
					+ this.serverInputRow + " column "
					+ this.serverInputCol);
		} catch (IOException e) {
			System.out.println("Unable to receive server move");
		}
	}


	/**
	 * Receives status code from the server. This is the status code
	 * for server move
	 */
	protected void receiveServerMoveCode() {
		try {
			this.statusCode = dis.readInt();
		} catch (IOException e) {
			System.out.println("Unable to read the status code for "
					+ "server's move");
		}
	}


	/**
	 * Connects client stream to the server
	 */
	protected void associateStream() {
		try {
			dos = new DataOutputStream(s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());
		} catch (IOException e) {

			e.printStackTrace();
		}

	}


	/**
	 * Closes the socket
	 */
	protected void closeSocket() {
		try {
			s.close();
			dis.close();
			dos.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}


	/**
	 * Prints client board on the screen
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


	/**
	 * Prints messages when a game is over
	 */
	protected void gameMessage() {
		if (this.statusCode == TicTacToeServer.FULL_CODE) {
			System.out
					.println("No empty squares on the board. Draw. "
							+ "Game over");
		} else if (this.statusCode == TicTacToeServer.SERVER_WIN_CODE) {
			System.out.println("The server win!" + " Game over");
		} else if (this.statusCode == TicTacToeServer.CLIENT_WIN_CODE) {
			System.out.println("You win! Game over");
		} else {
			System.out.println("Illegal move or quit, Game is over");
		}

	}


	public static void main(String[] args) {
		new TicTacToeClient();

	}

}
