/******************************************************************************
 * Author: Rafael Zuniga
 * 
 * Compilation: javac ShortestCommonAncestor.java Dependencies: Digraph.java
 *
 ******************************************************************************/

public class ShortestCommonAncestor {

	private Digraph G;
	private int roots;

	/******************************************************************************
	 * constructor takes a rooted DAG as argument
	 ******************************************************************************/
	public ShortestCommonAncestor(Digraph G) {
		this.G = new Digraph(G);
		validateAcyclical(G);
	}

	private void validateAcyclical(Digraph G) {
		roots = 0;
		for (int v = 0; v < G.V(); v++) {
			if (G.outdegree(v) == 0) {
				roots++;
			}
		}

		if (roots != 1)
			throw new java.lang.IllegalArgumentException("not acyclical");
	}

	/******************************************************************************
	 * length of shortest ancestral path between v and w
	 ******************************************************************************/
	public int length(int v, int w) {
		if (v < 0 || v > G.V() || w < 0 || w > G.V())
			throw new java.lang.IndexOutOfBoundsException();

		Stack<Integer> subsetA = new Stack<Integer>();
		Stack<Integer> subsetB = new Stack<Integer>();
		subsetA.push(v);
		subsetB.push(w);
		RedBlueBFS rbBFS = new RedBlueBFS(G, subsetA, subsetB);
		return rbBFS.redBlueDistToAncestor;
	}

	/******************************************************************************
	 * a shortest common ancestor of vertices v and w
	 ******************************************************************************/
	public int ancestor(int v, int w) {
		if (v < 0 || v > G.V() || w < 0 || w > G.V())
			throw new java.lang.IndexOutOfBoundsException();

		Stack<Integer> subsetA = new Stack<Integer>();
		Stack<Integer> subsetB = new Stack<Integer>();
		subsetA.push(v);
		subsetB.push(w);
		RedBlueBFS rbBFS = new RedBlueBFS(G, subsetA, subsetB);
		return rbBFS.ancestor;
	}

	/******************************************************************************
	 * length of shortest ancestral path of vertex subsets A and B
	 ******************************************************************************/
	public int length(Iterable<Integer> subsetA, Iterable<Integer> subsetB) {
		validate(subsetA, subsetB);
		RedBlueBFS rbBFS = new RedBlueBFS(G, subsetA, subsetB);
		return rbBFS.redBlueDistToAncestor;
	}

	/******************************************************************************
	 * a shortest common ancestor of vertex subsets A and B
	 ******************************************************************************/
	public int ancestor(Iterable<Integer> subsetA, Iterable<Integer> subsetB) {
		validate(subsetA, subsetB);
		RedBlueBFS rbBFS = new RedBlueBFS(G, subsetA, subsetB);
		return rbBFS.ancestor;
	}

	/******************************************************************************
	 * Checks for null inputs
	 ******************************************************************************/
	private void validate(Iterable<Integer> subsetA, Iterable<Integer> subsetB) {
		int count = 0;
		for (int i : subsetA) {
			if (i < 0 || i > G.V())
				throw new java.lang.IndexOutOfBoundsException("in subsetA");
			count++;
		}
		if (count == 0)
			throw new java.lang.IllegalArgumentException();
		count = 0;
		for (int i : subsetB) {
			if (i < 0 || i > G.V())
				throw new java.lang.IndexOutOfBoundsException("in subsetB");
			count++;
		}
		if (count == 0)
			throw new java.lang.IllegalArgumentException();

	}

	/******************************************************************************
	 * do unit testing of this class
	 ******************************************************************************/
	public static void main(String[] args) {
		In in = new In(args[0]);
		Digraph G = new Digraph(in);
		ShortestCommonAncestor sca = new ShortestCommonAncestor(G);

		while (!StdIn.isEmpty()) {
			int v = StdIn.readInt();
			int w = StdIn.readInt();
			int length = sca.length(v, w);
			int ancestor = sca.ancestor(v, w);
			StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
		}
	}

	/******************************************************************************
	 * Reimplementation of BFS to be able to do a double multi source BFS
	 ******************************************************************************/
	private class RedBlueBFS {
		private static final int INFINITY = Integer.MAX_VALUE;
		private boolean[] redMarked; // marked[v] = is there an s->v path?
		private boolean[] blueMarked;
		private int[] edgeTo; // edgeTo[v] = last edge on shortest s->v path
		private int[] redDistTo; // distTo[v] = length of shortest s->v path
		private int[] blueDistTo;
		private int ancestor;
		private int redBlueDistToAncestor;

		/******************************************************************************
		 * single source constructor to check if acyclical
		 ******************************************************************************/
		private RedBlueBFS(Digraph G) {
			redMarked = new boolean[G.V()];
			redDistTo = new int[G.V()];
			edgeTo = new int[G.V()];
			for (int v = 0; v < G.V(); v++)
				redDistTo[v] = INFINITY;
		}

		/******************************************************************************
		 * Constructor takes in two iterables and performs a BFS from two
		 * individual sources at the same time
		 ******************************************************************************/
		private RedBlueBFS(Digraph G, Iterable<Integer> redSources, Iterable<Integer> blueSources) {
			redMarked = new boolean[G.V()];
			blueMarked = new boolean[G.V()];
			redDistTo = new int[G.V()];
			blueDistTo = new int[G.V()];
			edgeTo = new int[G.V()];
			for (int v = 0; v < G.V(); v++) {
				redDistTo[v] = INFINITY;
				blueDistTo[v] = INFINITY;
			}
			ancestor = bfs(G, redSources, blueSources);
			redBlueDistToAncestor = redDistTo[ancestor] + blueDistTo[ancestor];
		}

		/******************************************************************************
		 * computes all the possible common ancestors of the synsets in the
		 * first iterable and the second iterable
		 ******************************************************************************/
		private int bfs(Digraph G, Iterable<Integer> redSources, Iterable<Integer> blueSources) {

			Stack<Integer> possibleSCAs = new Stack<Integer>();
			Queue<Integer> redQ = new Queue<Integer>();
			Queue<Integer> blueQ = new Queue<Integer>();
			for (int s : redSources) {
				redMarked[s] = true;
				redDistTo[s] = 0;
				redQ.enqueue(s);
			}

			for (int s : blueSources) {
				blueMarked[s] = true;
				blueDistTo[s] = 0;
				blueQ.enqueue(s);

				// if noun1 == noun2, distance = 0 and ancestor is itself
				if (redMarked[s] && blueMarked[s])
					return s;
			}

			// keep searching as long as one of the Queues still has a vertex
			while (!redQ.isEmpty() || !blueQ.isEmpty()) {

				// BFS using first iterable
				if (!redQ.isEmpty()) {
					int v = redQ.dequeue();
					for (int w : G.adj(v)) {
						if (!blueMarked[w]) { // keep searching
							edgeTo[w] = v;
							if (redDistTo[v] + 1 < redDistTo[w])
								redDistTo[w] = redDistTo[v] + 1;
							redMarked[w] = true;
							redQ.enqueue(w);
						} else { // ancestor found by red
							if (redDistTo[v] + 1 < redDistTo[w])
								redDistTo[w] = redDistTo[v] + 1;
							redMarked[w] = true;
							possibleSCAs.push(w);
						}

					}

				}

				// BFS using second iterable
				if (!blueQ.isEmpty()) {
					int v = blueQ.dequeue();
					for (int w : G.adj(v)) {
						if (!redMarked[w]) { // keep searching
							edgeTo[w] = v;
							if (blueDistTo[v] + 1 < blueDistTo[w])
								blueDistTo[w] = blueDistTo[v] + 1;
							blueMarked[w] = true;
							blueQ.enqueue(w);
						} else { // ancestor found by blue
							if (blueDistTo[v] + 1 < blueDistTo[w])
								blueDistTo[w] = blueDistTo[v] + 1;
							blueMarked[w] = true;
							possibleSCAs.push(w);
						}
					}

				}

			}

			return findShortest(possibleSCAs);
		}

		/******************************************************************************
		 * takes in an iterable with all the possible common ancestors and
		 * returns the shortest one.
		 ******************************************************************************/
		private int findShortest(Stack<Integer> possibleSCAs) {
			int sca = -1;
			int shortestDist = Integer.MAX_VALUE;
			for (int v : possibleSCAs) {
				int currentDist = redDistTo[v] + blueDistTo[v];
				if (currentDist < shortestDist) {
					shortestDist = currentDist;
					sca = v;
				}
			}
			return sca;
		}
	}
}
