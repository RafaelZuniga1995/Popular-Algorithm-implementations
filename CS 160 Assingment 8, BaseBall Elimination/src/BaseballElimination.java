
/******************************************************************************
 *  Name:         Rafael Zuniga
 *  Compilation:  javac BaseballElimination.java
 *  Execution:    java BaseballElimination teams4.txt
 *  Dependencies: FordFulkerson.java
 *
 *
 ******************************************************************************/

import java.util.Hashtable;

public class BaseballElimination {

	private int[] w; // wins
	private int[] l; // loss
	private int[] r; // remaining games
	private int[][] g; // remaining games between i and j
	private int numTeams;
	private Hashtable<String, Integer> teams; // team name -> number
	private Hashtable<Integer, String> numToTeam; // number -> team name
	private long numGameVertices;
	private int networkSize;

	/******************************************************************************
	 * creates a baseball division from given filename
	 ******************************************************************************/
	public BaseballElimination(String filename) {
		In in = new In(filename);
		numTeams = in.readInt();
		w = new int[numTeams];
		l = new int[numTeams];
		r = new int[numTeams];
		g = new int[numTeams][numTeams];
		teams = new Hashtable<String, Integer>();
		numToTeam = new Hashtable<Integer, String>();
		numGameVertices = nChoseK(numTeams - 1, 2);
		// +2 for source and sink
		networkSize = (int) (1 + numGameVertices + numTeams + 1);

		for (int i = 0; i < numTeams; i++) {
			String name = in.readString();
			teams.put(name, i);
			numToTeam.put(i, name);
			w[i] = in.readInt();
			l[i] = in.readInt();
			r[i] = in.readInt();
			for (int j = 0; j < numTeams; j++)
				g[i][j] = in.readInt();
		}
	}

	/******************************************************************************
	 * number of teams
	 ******************************************************************************/
	public int numberOfTeams() {
		return numTeams;
	}

	/******************************************************************************
	 * all teams
	 ******************************************************************************/
	public Iterable<String> teams() {
		return teams.keySet();
	}

	/******************************************************************************
	 * number of wins for given team
	 ******************************************************************************/
	public int wins(String team) {
		validateTeam(team);
		int i = teams.get(team);
		return w[i];
	}

	/******************************************************************************
	 * number of losses for given team
	 ******************************************************************************/
	public int losses(String team) {
		validateTeam(team);
		int i = teams.get(team);
		return l[i];
	}

	/******************************************************************************
	 * number of remaining games for given team
	 ******************************************************************************/
	public int remaining(String team) {
		validateTeam(team);
		int i = teams.get(team);
		return r[i];
	}

	/******************************************************************************
	 * number of remaining games between team1 and team2
	 ******************************************************************************/
	public int against(String team1, String team2) {
		validateTeam(team1);
		validateTeam(team2);
		int i = teams.get(team1);
		int j = teams.get(team2);
		return g[i][j];
	}

	/******************************************************************************
	 * is given team eliminated?
	 ******************************************************************************/
	public boolean isEliminated(String team) {
		validateTeam(team);
		int s = teams.get(team);
		if (trivialElimination(s))
			return true;

		FlowNetwork flowNetwork = createFlowNetwork(s);

		// Non Trivial elimination
		FordFulkerson ff = new FordFulkerson(flowNetwork, networkSize - 2, networkSize - 1);
		for (FlowEdge flowEdge : flowNetwork.adj(networkSize - 2))
			if (flowEdge.residualCapacityTo(flowEdge.to()) != 0)
				return true;

		return false;
	}

	// creates the division FlowNetwork
	private FlowNetwork createFlowNetwork(int s) {

		FlowNetwork flowNetwork = new FlowNetwork(networkSize);

		// let sizeNetwork-2 = source, and sizeNetwork-1 = t
		int k = numTeams; // reserves space for numTeams
		for (int i = 0; i < g.length; i++) {
			for (int j = i + 1; j < g.length; j++) {
				if (i == s || j == s)
					continue;

				FlowEdge sToGame = new FlowEdge(networkSize - 2, k, g[i][j]);
				flowNetwork.addEdge(sToGame);
				// gameToTeam1: v = edge from (the game of g[i][j]) to team i
				FlowEdge gameToTeam1 = new FlowEdge(k, i, Integer.MAX_VALUE);
				flowNetwork.addEdge(gameToTeam1);
				// gameToTeam1: v = edge from (the game of g[i][j]) to team j
				FlowEdge gameToTeam2 = new FlowEdge(k++, j, Integer.MAX_VALUE);
				flowNetwork.addEdge(gameToTeam2);
			}
		}

		for (int i = 0; i < numTeams; i++) {

			if ((w[s] + r[s] - w[i]) > 0) {
				FlowEdge teamToSink = new FlowEdge(i, networkSize - 1, w[s] + r[s] - w[i]);
				flowNetwork.addEdge(teamToSink);
			} else {
				FlowEdge teamToSink = new FlowEdge(i, networkSize - 1, 0);
				flowNetwork.addEdge(teamToSink);
			}
		}

		return flowNetwork;
	}

	private boolean trivialElimination(int s) {

		for (int i = 0; i < numTeams; i++) {
			if (i == s)
				continue;
			if (w[s] + r[s] < w[i]) {
				return true;
			}
		}
		return false;
	}

	// used to get the number of vertices our flownetwork should be
	private int nChoseK(int n, int k) {
		if (k == 0)
			return 1;
		return (n * nChoseK(n - 1, k - 1)) / k;
	}

	// subset R of teams that eliminates given team; null if not eliminated
	public Iterable<String> certificateOfElimination(String team) {
		validateTeam(team);
		int s = teams.get(team);

		FlowNetwork flowNetwork = createFlowNetwork(s);

		// if eliminated trivially, only 1 team should be in R
		for (int i = 0; i < numTeams; i++) {
			if (i == s)
				continue;
			if (w[s] + r[s] < w[i]) {
				Stack<String> R = new Stack<String>();
				R.push(numToTeam.get(i));
				return R;
			}
		}

		// get R using mincut properties
		FordFulkerson ff = new FordFulkerson(flowNetwork, networkSize - 2, networkSize - 1);
		Stack<String> R = new Stack<String>();
		for (int i = 0; i < numTeams; i++) {
			if (i == s)
				continue;
			if (ff.inCut(i))
				R.push(numToTeam.get(i));
		}

		if (R.size() == 0)
			return null;
		else
			return R;
	}

	private void validateTeam(String team) {
		if (!teams.containsKey(team))
			throw new java.lang.IllegalArgumentException();
	}

	public static void main(String[] args) {
		BaseballElimination division = new BaseballElimination(args[0]);

		for (String team : division.teams()) {
			if (division.isEliminated(team)) {
				StdOut.print(team + " is eliminated by the subset R = { ");
				for (String t : division.certificateOfElimination(team))
					StdOut.print(t + " ");
				StdOut.println("}");
			} else {
				StdOut.println(team + " is not eliminated");
			}
		}

	}
}
