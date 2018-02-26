/******************************************************************************
 *  Author:       Rafael Zuniga
 *  
 *  Compilation:  javac WordnNet.java
 *  Dependencies: ShortestCommonAncestor.java
 *
 ******************************************************************************/
import java.util.*;

public class WordNet {

	// key = nouns, value = all ids (synsets) where the noun appears
	private Hashtable<String, Stack<Integer>> nounToInts;
	// key = id,   value = noun
	private Hashtable<Integer, String> numToSynset;
	private Digraph G;
	private ShortestCommonAncestor sca;
	private int gRoot;
	private int verticesCount;

	
	/******************************************************************************
	 *  constructor takes the name of the two input files
	 ******************************************************************************/
	public WordNet(String synsets, String hypernyms) {
		
		// validate
		if (synsets == null || hypernyms == null)
			throw new java.lang.NullPointerException();
			
		nounToInts = new Hashtable<String, Stack<Integer>>();
		numToSynset = new Hashtable<Integer, String>();
		readSynsets(synsets);
		Stack<String> edgesStack = readIntoStack(hypernyms);
		createDigraph(edgesStack);
		sca = new ShortestCommonAncestor(G);
	}
	

	/******************************************************************************
	 *  parses all hypernym relations in hypernynms input file into a stack
	 ******************************************************************************/
	private Stack<String> readIntoStack(String hypernyms) {
		Stack<String> inputStack = new Stack<String>();
		In in = new In(hypernyms);

		while (in.hasNextLine()) {

			String tempLine = in.readLine();
			String[] tempVW = tempLine.split("\\,");

			// parse vertices that have more than one edge
			if (tempVW.length > 2) {
				for (int i = 1; i < tempVW.length; i++) {
					String temp = tempVW[0] + "," + tempVW[i];
					inputStack.push(temp);
				}
			} else { // if line has only one v and one w
				inputStack.push(tempLine);
			}

		}
		
		return inputStack;
		
	}
	
	private void createDigraph(Stack<String> edgesStack) {
		int gSize = edgesStack.size();
		G = new Digraph(verticesCount);
		for (int i = 0; i < gSize; i++) {

			String[] temp = edgesStack.pop().split("\\,");
			if (temp.length == 1){ // found root
				gRoot = Integer.parseInt(temp[0]);
				continue;
			}

			int v = Integer.parseInt(temp[0]);
			int w = Integer.parseInt(temp[1]);
			G.addEdge(v, w);

		}
	}

	private void readSynsets(String synsets) {
				In synsetsIn = new In(synsets);

				// read one line at a time
				while (!synsetsIn.isEmpty()) {
					verticesCount++;
					// read SCV 1 line at a time
					String[] tempLine = synsetsIn.readLine().split("\\,");
					// first field : id
					int id = Integer.parseInt(tempLine[0]);       
					// split second field into individual nouns
					String[] splitNouns = tempLine[1].split(" "); 

					// set numToSynsets
					numToSynset.put(id, tempLine[1]);

					Stack<Integer> intStack = new Stack<Integer>();
					intStack.push(id);

					// fills the hashtable
					for (int i = 0; i < splitNouns.length; i++) {

						// if this is a new noun, put it in the hashtable
						if (!nounToInts.containsKey(splitNouns[i])) {
							nounToInts.put(splitNouns[i], intStack);
						} else { // if noun already in hashtable, push 
							nounToInts.get(splitNouns[i]).push(id);
						}

					}
				}
		
	}

	/******************************************************************************
	 *  all wordnet nouns
	 ******************************************************************************/
	public Iterable<String> nouns() {
		return nounToInts.keySet();
	}

	/******************************************************************************
	 *  is the word a WordNet noun?
	 ******************************************************************************/
	public boolean isNoun(String word) {
		return nounToInts.containsKey(word);
	}

	
	/******************************************************************************
	 *  a synset (second field of synsets.txt) that is a shortest common ancestor
	 *   of noun1 and noun2 (defined below)
	 ******************************************************************************/
	public String sca(String noun1, String noun2) {
		if (!isNoun(noun1) || !isNoun(noun2)){
			throw new java.lang.IllegalArgumentException(" input is not a wordnet noun");
		}
		return numToSynset.get(sca.ancestor(nounToInts.get(noun1), nounToInts.get(noun2)));
	}

	/******************************************************************************
	 *  distance between noun1 and noun2 (defined below)
	 ******************************************************************************/
	public int distance(String noun1, String noun2) {
		if (!isNoun(noun1) || !isNoun(noun2)){
			throw new java.lang.IllegalArgumentException(" input is not a wordnet noun");
		}
		return sca.length(nounToInts.get(noun1), nounToInts.get(noun2));
	}

	/******************************************************************************
	 *  unit testing of this class
	 ******************************************************************************/
	public static void main(String[] args) {
		WordNet wNet = new WordNet(args[0], args[1]);
		System.out.println(args[2] + " and " + args[3]);
		System.out.println("sca : " + wNet.sca(args[2], args[3]));
		System.out.println("dis : " + wNet.distance(args[2], args[3]));
		System.out.println("root :" + wNet.gRoot);
	}
}