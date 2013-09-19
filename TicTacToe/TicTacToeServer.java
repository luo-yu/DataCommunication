import java.io.*;
import java.net.*;
import java.util.Random;

public class TicTacToeServer {

	public static int SERVER_PORT = 32100;
	public static int NOT_FULL_CODE = 10;
	public static int FULL_CODE = 20;
	public static int SERVER_WIN_CODE = 30;
	public static int CLIENT_WIN_CODE = 40;
	public static int ILLEGAL_MOVE_CODE = 50;

	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	private int row = 0; // used to receive client moves
	private int col = 0; // used to receive client moves
	private int rr = 0; // used to send server moves
	private int cc = 0; // used to send server moves
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private int serverStatusCode = 0;

	public int[][] board = new int[3][3];

	public TicTacToeServer() {

		this.initializeBoard();
		this.printBoard();
		this.createServerSocket();
		this.displayContactInfor();
		this.createClientSocket();
		this.createClientStreams();

		this.receiveClientAndUpdateMoves();
		while (this.serverStatusCode == NOT_FULL_CODE) {
			this.sendAndUpdateServerMove();
			if (this.serverStatusCode == NOT_FULL_CODE) {
				this.receiveClientAndUpdateMoves();
			}
		}

		System.out.println("HAHAHHAH Server");
		this.closeServer();
	}

	public void sendAndUpdateServerMove() {

		Random r = new Random();
		rr = r.nextInt(3);
		cc = r.nextInt(3);

		while (board[rr][cc] != 0) {
			rr = r.nextInt(3);
			cc = r.nextInt(3);
		}

		try {
			dos.writeInt(rr);
			dos.writeInt(cc);
		} catch (IOException e) {
			e.printStackTrace();
		}

		checkServerMove();

	}

	public void receiveClientAndUpdateMoves() {
		try {
			row = dis.readInt();
			col = dis.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.checkClientAndRespond();

	}

	public void checkClientAndRespond() {
		if (isLegalMove(row, col)) {
			board[row][col] = -1;
			this.printBoard();

			if (isClientWin() || isServerWin() || isFull()) {
				this.sendStatusCode();
				this.printBoard();
				this.closeClientConnection();
			} else {
				this.serverStatusCode = NOT_FULL_CODE;
				this.sendStatusCode();
			}
		} else {
			this.sendStatusCode();
			this.closeClientConnection();
		}
	}

	public void checkServerMove() {
		if (isLegalMove(rr, cc)) {
			board[rr][cc] = 1;
			this.printBoard();

			if (isClientWin() || isServerWin() || isFull()) {
				this.sendStatusCode();
				this.printBoard();
				this.closeClientConnection();
			} else {
				this.serverStatusCode = NOT_FULL_CODE;
				this.sendStatusCode();
			}
		} else {
			this.sendStatusCode();
			this.closeClientConnection();
		}
	}

	/**
	 * Print the board
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

	public void createClientSocket() {

		clientSocket = new Socket();
		try {
			this.clientSocket = this.serverSocket.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendStatusCode() {
		System.out
				.println("I am sending status code: " + this.serverStatusCode);
		try {
			dos.writeInt(serverStatusCode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isFull() {
		boolean isFull = true;

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j]==0) {
					isFull = false;
					j = board[0].length - 1;
					i = board.length - 1;
				}
			}
		}

		if (isFull) {
			this.serverStatusCode = FULL_CODE;
		}
		return isFull;
	}

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

		if (isServerWin)
			this.serverStatusCode = SERVER_WIN_CODE;
		return isServerWin;
	}

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

		if (isClientWin) {
			this.serverStatusCode = CLIENT_WIN_CODE;
		}
		return isClientWin;
	}

	public boolean isLegalMove(int row, int col) {
		boolean isLegal = true;
		if (board[row][col] != 0)
			isLegal = false;

		if (!isLegal)
			this.serverStatusCode = ILLEGAL_MOVE_CODE;

		return isLegal;
	}

	// OK*******************************************

	public void closeClientConnection() {
		try {
			this.clientSocket.close();
			this.dis.close();
			this.dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void closeServer() {

		try {
			this.serverSocket.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	// Okay Code
	public void initializeBoard() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				board[i][j] = 0;
			}
		}
	}

	public void createClientStreams() {
		try {
			dis = new DataInputStream(clientSocket.getInputStream());
			dos = new DataOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void createServerSocket() {
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
		} catch (IOException e) {
			System.out
					.println("The ServerSocket is not successfully created. Please check if you have the correct port number");
			e.printStackTrace();
		}
	}

	public void displayContactInfor() {
		try {
			// Display contact information.
			System.out.println("Number Server standing by to accept Clients:"
					+ "\nIP : " + InetAddress.getLocalHost() + "\nPort: "
					+ serverSocket.getLocalPort() + "\n\n");
		} catch (UnknownHostException e) {
			// NS lookup for host IP failed?
			// This should only happen if the host machine does
			// not have an IP address.
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new TicTacToeServer();
	}

}
