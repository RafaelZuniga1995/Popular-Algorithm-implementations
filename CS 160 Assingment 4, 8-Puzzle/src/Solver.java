
/******************************************************************************
 *  @author Rafael Zuniga
 *  
 *  
 *  Compilation:  javac Solver.java
 *  Execution:    java Solver "input"
 *
 *  This program used the A* algorithm to find the solution to the given
 *  puzzle
 *
 ******************************************************************************/
import java.util.*;

public class Solver {

	private Board initial;
	private int moves;
	private SearchNode solutionNode;
	private MinPQ<SearchNode> minPq;
	private ManhattanPriority manhPriority;
	private HammingPriority hamPriority;

	/******************************************************************************
	 * Uses the A* algorithm to find the solution to the given board
	 ******************************************************************************/
	public Solver(Board initial) {
		this.initial = initial;

		// isSolveable?
		if (!initial.isSolvable())
			throw new java.lang.IllegalArgumentException("Initial board is not Solvable");
		if (initial == null)
			throw new java.lang.NullPointerException("Initial board is null");

		// priority Queue with manhattan priority
		manhPriority = new ManhattanPriority();
		hamPriority = new HammingPriority();
		minPq = new MinPQ<SearchNode>(manhPriority);
		SearchNode iniNode = new SearchNode(initial, null, 0);

		// A* Algorithm Start
		minPq.insert(iniNode);
		SearchNode deletedNode = minPq.delMin();

		while (!deletedNode.board.isGoal()) {

			// insert all its neighbors into minPq unless it is equal to its
			// granparent
			for (Board board : deletedNode.board.neighbors()) {
				SearchNode currNeighbor = new SearchNode(board, deletedNode, deletedNode.moves + 1);

				if (deletedNode.previous == null) {// first iteration
					minPq.insert(currNeighbor);
				} else {
					if (!deletedNode.previous.board.equals(currNeighbor.board))
						minPq.insert(currNeighbor);
				}
			}

			// get next leaf node with min manhattan Priority
			deletedNode = minPq.delMin();

		}

		// if code is here, currentNode should contain the goal Board
		solutionNode = deletedNode;

	}

	/******************************************************************************
	 * min number of moves to solve initial board
	 ******************************************************************************/
	public int moves() {
		return solutionNode.moves;
	}

	/******************************************************************************
	 * sequence of boards in a shortest solution
	 ******************************************************************************/
	public Iterable<Board> solution() {

		// holder
		Stack<Board> solutionSeq = new Stack<Board>();

		// trace back from solution node to initial node
		SearchNode currentNode = solutionNode;
		while (!currentNode.board.equals(initial)) {
			solutionSeq.push(currentNode.board);
			currentNode = currentNode.previous;
		}

		return solutionSeq;
	}

	/******************************************************************************
	 * solve a slider puzzle with given format
	 ******************************************************************************/
	public static void main(String[] args) {
		// solve a slider puzzle (given below)

		// create initial board from file
		In in = new In(args[0]);
		int N = in.readInt();
		int[][] tiles = new int[N][N];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				tiles[i][j] = in.readInt();
		Board initial = new Board(tiles);

		// check if puzzle is solvable; if so, solve it and output solution
		if (initial.isSolvable()) {
			Solver solver = new Solver(initial);
			System.out.println("Minimum number of moves = " + solver.moves());
			for (Board board : solver.solution())
				System.out.println(board);
		}

		// if not, report unsolvable
		else {
			System.out.println("Unsolvable puzzle");
		}

	}

	/******************************************************************************
	 * A search node knows a board, previous board, # of moves to get to this
	 * board
	 ******************************************************************************/
	private class SearchNode {
		private Board board;
		private SearchNode previous;
		private int moves; // num of moves to get to this node/board
		private int manhPriority; // moves + manhattan
		private int hammingPriority; // moves + hamming

		private SearchNode(Board board, SearchNode previous, int moves) {
			this.board = board;
			this.previous = previous;
			this.moves = moves;
			manhPriority = moves + board.manhattan();
			hammingPriority = moves + board.hamming();
		}

	}

	private class ManhattanPriority implements Comparator<SearchNode> {

		@Override
		public int compare(SearchNode a, SearchNode b) {
			if (a.manhPriority < b.manhPriority)
				return -1;
			if (a.manhPriority > b.manhPriority)
				return 1;
			return 0;
		}

	}

	private class HammingPriority implements Comparator<SearchNode> {

		@Override
		public int compare(SearchNode a, SearchNode b) {
			if (a.hammingPriority < b.hammingPriority)
				return -1;
			if (a.hammingPriority > b.hammingPriority)
				return 1;
			return 0;
		}

	}

}