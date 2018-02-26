
/******************************************************************************
 *  @author Rafael Zuniga
 *  
 *  
 *  Compilation:  javac Board.java
 *  Execution:    java Board
 *
 *  This program takes create a board that contains an N by N 2d array
 *
 ******************************************************************************/
import java.util.*;

public class Board {

	private int[][] tiles;
	private int N;
	private int manhattanDis;

	// helpers for isSolveable(), neighbors()
	private int numOfInversions;
	private int rowOfBlank;
	private int columnOfBlank;

	// for neighbors
	private Stack<Board> neighborStack;

	/******************************************************************************
	 * construct a board from an N-by-N array of tiles (where tiles[i][j] = tile
	 * at row i, column j)
	 *
	 ******************************************************************************/
	public Board(int[][] tiles) {
		N = tiles.length;
		this.tiles = new int[N][N];

		// defensive
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++) {
				this.tiles[i][j] = tiles[i][j];
			}

		// manhattan distance
		manhattanDis = 0;

		// where tiles[i][j] should be (for computing manhattan distance)
		int correctI = -1;
		int correctJ = -1;

		// helpers to get the values needed from mod and dividing
		double a;
		double b;

		// compute manhattan distance
		for (int i = 1; i < N + 1; i++) {
			for (int j = 1; j < N + 1; j++) {
				if (tiles[i - 1][j - 1] == 0)
					continue; // skip blank tile

				a = tiles[i - 1][j - 1];
				b = N;
				correctI = (int) Math.ceil(a / b);
				correctJ = tiles[i - 1][j - 1] % N; // if last column, this
													// returns 0
				if (correctJ == 0) // if 0, it should be N (last column)
					correctJ = N;

				// calculate manhattan distance
				int dI = i - correctI;
				int dJ = j - correctJ;
				manhattanDis += Math.abs(dI) + Math.abs(dJ);
			}
		}

		// variables needed for other methods
		int[] tiles1D = new int[N * N];
		numOfInversions = 0; // used in isSolvable()
		rowOfBlank = 0; // used for when N is even in isSolvable()
		columnOfBlank = 0; // used in neighbors() along with rowOfBlank()
		int count = 0;

		// construct 1D array representation of tiles, also gets position of
		// blank tile
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++) {
				if (tiles[i][j] == 0) {
					rowOfBlank = i;
					columnOfBlank = j;
				}
				tiles1D[count++] = tiles[i][j];
			}

		// compute number of inversions
		for (int i = 0; i < N * N; i++) {
			for (int j = i + 1; j < N * N; j++) {
				if (tiles1D[j] == 0)
					continue;
				if (tiles1D[i] > tiles1D[j])
					numOfInversions++;
			}
		}

	}

	/******************************************************************************
	 * return tile at row i, column j (or 0 if blank)
	 *
	 ******************************************************************************/
	public int tileAt(int i, int j) {
		if (i < 0 && i > N - 1 || j < 0 && j > N - 1)
			throw new java.lang.IndexOutOfBoundsException("i or j is out-of-bounds");
		return tiles[i][j];
	}

	/******************************************************************************
	 * return the size of the board (N)
	 ******************************************************************************/
	public int size() {
		return N;
	}

	/******************************************************************************
	 * number of tiles out of place
	 ******************************************************************************/
	public int hamming() {

		int outOfPlace = 0;
		int helper = 0; // run numbers from 1 to N^2

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				helper++; // update first so it equals 1 the first time through
				if (tiles[i][j] == 0)
					continue; // skip blank tile
				if (tiles[i][j] != helper)
					outOfPlace++;

			}
		}

		// still need to add the number of moves made?
		// subtract 1 to make up for blank tile... used continue instead
		return outOfPlace;
	}

	/******************************************************************************
	 * sum of Manhattan distances between tiles and goal computed in the
	 * constructor
	 ******************************************************************************/
	public int manhattan() {
		return manhattanDis;
	}

	/******************************************************************************
	 * is this board the goal board?
	 ******************************************************************************/
	public boolean isGoal() {
		return manhattanDis == 0;
	}

	/******************************************************************************
	 * is this board solvable?
	 ******************************************************************************/
	public boolean isSolvable() {

		// compute number of inversions, here or in constructor

		// if N is odd
		if (N % 2 != 0) {
			// if numOfInversions is odd, this board is not solvable
			if (numOfInversions % 2 != 0)
				return false;
			else
				return true;
		}

		// if N is even
		int sum = numOfInversions + rowOfBlank;
		if (sum % 2 != 0) // if sum is odd
			return true;
		else
			return false;
	}

	/******************************************************************************
	 * does this board equal y?
	 ******************************************************************************/
	public boolean equals(Object y) {
		if (y == this)
			return true;
		if (y == null)
			return false;
		if (y.getClass() != this.getClass())
			return false;

		Board that = (Board) y;

		// check each tile individually?
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++) {
				if (this.tiles[i][j] != that.tiles[i][j])
					return false;
			}

		return (this.N == that.N);
	}

	/******************************************************************************
	 * check if i and j are in bounds of the array (tiles) helper for
	 * neighbors()
	 ******************************************************************************/
	private boolean inBound(int i, int j) {
		if (i >= 0 && i < N && j >= 0 && j < N)
			return true;
		return false;
	}

	/******************************************************************************
	 * all neighboring boards
	 ******************************************************************************/
	public Iterable<Board> neighbors() {

		neighborStack = new Stack<Board>();

		// figure out if we can move left/right/up/down
		boolean left = inBound(rowOfBlank, columnOfBlank - 1);
		boolean right = inBound(rowOfBlank, columnOfBlank + 1);
		boolean top = inBound(rowOfBlank - 1, columnOfBlank);
		boolean bottom = inBound(rowOfBlank + 1, columnOfBlank);

		// declare 4 potential boards
		Board leftBoard = null;
		Board rightBoard = null;
		Board topBoard = null;
		Board bottomBoard = null;

		// create the neighboring boards and put them in a stack
		if (left) {

			// create 2d array first and then create the board
			int[][] tempTiles = new int[N][N];
			for (int i = 0; i < N; i++)
				for (int j = 0; j < N; j++) {
					tempTiles[i][j] = this.tiles[i][j];
				}

			// swap
			int temp = tempTiles[rowOfBlank][columnOfBlank - 1];
			tempTiles[rowOfBlank][columnOfBlank] = temp;
			tempTiles[rowOfBlank][columnOfBlank - 1] = 0;
			leftBoard = new Board(tempTiles);
			neighborStack.push(leftBoard);
		}

		if (right) {

			// create 2d array first and then create the board
			int[][] tempTiles = new int[N][N];
			for (int i = 0; i < N; i++)
				for (int j = 0; j < N; j++) {
					tempTiles[i][j] = this.tiles[i][j];
				}

			// swap
			int temp = tempTiles[rowOfBlank][columnOfBlank + 1];
			tempTiles[rowOfBlank][columnOfBlank] = temp;
			tempTiles[rowOfBlank][columnOfBlank + 1] = 0;
			rightBoard = new Board(tempTiles);
			neighborStack.push(rightBoard);
		}

		if (top) {

			// create 2d array first and then create the board
			int[][] tempTiles = new int[N][N];
			for (int i = 0; i < N; i++)
				for (int j = 0; j < N; j++) {
					tempTiles[i][j] = this.tiles[i][j];
				}

			// swap
			int temp = tempTiles[rowOfBlank - 1][columnOfBlank];
			tempTiles[rowOfBlank][columnOfBlank] = temp;
			tempTiles[rowOfBlank - 1][columnOfBlank] = 0;
			topBoard = new Board(tempTiles);
			neighborStack.push(topBoard);
		}

		if (bottom) {

			// create 2d array first and then create the board
			int[][] tempTiles = new int[N][N];
			for (int i = 0; i < N; i++)
				for (int j = 0; j < N; j++) {
					tempTiles[i][j] = this.tiles[i][j];
				}

			// swap
			int temp = tempTiles[rowOfBlank + 1][columnOfBlank];
			tempTiles[rowOfBlank][columnOfBlank] = temp;
			tempTiles[rowOfBlank + 1][columnOfBlank] = 0;
			bottomBoard = new Board(tempTiles);
			neighborStack.push(bottomBoard);
		}

		return neighborStack;
	}

	/******************************************************************************
	 * string representation of this board
	 ******************************************************************************/
	public String toString() {

		StringBuilder s = new StringBuilder();
		s.append(N + "\n");
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				s.append(String.format("%2d ", tileAt(i, j)));
			}
			s.append("\n");
		}
		return s.toString();
	}

	/******************************************************************************
	 * unit testing. (not tested)
	 ******************************************************************************/
	public static void main(String[] args) {

	}

}
