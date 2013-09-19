
import java.io.*;
import java.net.*;
import java.util.*;

public class TicTacToeClient {

	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private Socket s = null;
	private Scanner input = new Scanner(System.in);
	public int[][] board = new int[3][3];

	private int row = 0;
	private int col = 0;
	private int rr = 0;
	private int cc = 0;
	private int statusCode = 0;
	private InetAddress serverIP = null;

	public TicTacToeClient() {
		this.initializeBoard();
		this.printBoard();
		this.readConsole();
		this.connectToServer();
		this.associateStream();

		this.sendClientMove();
		this.receiveStatusCode();
		if(this.statusCode==TicTacToeServer.NOT_FULL_CODE){
			board[row][col]=-1;
			this.printBoard();
		}
		gameMessage();
		System.out.println("HAHAHAHHAHA");

		this.closeSocket();

	}

	public void readConsole() {
		System.out
				.print("Enter the IP address of the server, or enter 1 to use the default ip address: ");
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
	 * Client Move
	 */
	public void receiveServerAndUpdateMove() {
		try {
			System.out.println("Reading stuff from server");
			this.gameMessage();
			rr = dis.readInt();
			cc = dis.readInt();
			this.receiveStatusCode();
			board[rr][cc] = 1;
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Computer choose row " + rr + " column " + cc);
		this.printBoard();
		
	}

	/**
	 * Server Move
	 */
	public void sendClientMove() {

		System.out.println("Your Move (Enter negative number to quit)");

		do {
			System.out.print("Enter row: ");
			this.row = input.nextInt();
		} while (row > 2);

		do {
			System.out.print("Enter Column: ");
			this.col = input.nextInt();
		} while (col > 2);


		if (this.statusCode == TicTacToeServer.NOT_FULL_CODE) {

			try {
				dos.writeInt(row);
				dos.writeInt(col);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void receiveStatusCode() {
		try {
			statusCode = dis.readInt();
			System.out.println("Statuc Code: " + statusCode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void connectToServer() {
		try {
			s = new Socket(serverIP, TicTacToeServer.SERVER_PORT);
		} catch (UnknownHostException e) {
			System.out.println("The local host is unknow");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initializeBoard() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				board[i][j] = 0;
			}
		}
	}

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

	public void associateStream() {
		try {
			dis = new DataInputStream(s.getInputStream());
			dos = new DataOutputStream(s.getOutputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void gameMessage() {
		if (statusCode == TicTacToeServer.FULL_CODE) {
			System.out
					.println("No empty squares on the board. Draw. Game is over");
		}
		if (statusCode == TicTacToeServer.SERVER_WIN_CODE) {
			System.out.println("The server has won the game. Game is over");
		}
		if (statusCode == TicTacToeServer.CLIENT_WIN_CODE) {
			System.out.println("You has won the game. Game is over");
		}
		if (statusCode == TicTacToeServer.ILLEGAL_MOVE_CODE) {
			System.out.println("Illegal move, Game is over");
		}
		System.out.println("Still playing");
	}

	public void closeSocket() {
		try {
			this.s.close();
		} catch (IOException e) {
			System.out.println("Can't close socket");
			e.printStackTrace();
		}
		try {
			this.dis.close();
		} catch (IOException e) {
			System.out.println("Can't close DataInputStream");
			e.printStackTrace();
		}
		try {
			this.dos.close();
		} catch (IOException e) {
			System.out.println("Can't close DataOutputStream");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		new TicTacToeClient();
	}

}
