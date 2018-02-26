
public class Outcast {
	private WordNet wordnet;

	/******************************************************************************
	 * constructor takes a WordNet object
	 ******************************************************************************/
	public Outcast(WordNet wordnet) {
		this.wordnet = wordnet;
	}

	/******************************************************************************
	 * given an array of WordNet nouns, return an outcast
	 ******************************************************************************/
	public String outcast(String[] nouns) {
		int largestDist = Integer.MIN_VALUE;
		String outcast = null;
		for (int i = 0; i < nouns.length; i++) {
			int sum = 0;
			for (int j = 0; j < nouns.length; j++) {
				sum += wordnet.distance(nouns[i], nouns[j]);
			}
			if (sum > largestDist) {
				outcast = nouns[i];
				largestDist = sum;
			}
		}
		return outcast;
	}

	/******************************************************************************
	 * takes from the command line the name of a synset file, the name of a 
	 * hypernym file, followed by the names of outcast files, and prints out an 
	 * outcast in each file
	 ******************************************************************************/
	public static void main(String[] args) {
		WordNet wordnet = new WordNet(args[0], args[1]);
	    Outcast outcast = new Outcast(wordnet);
	    for (int t = 2; t < args.length; t++) {
	        In in = new In(args[t]);
	        String[] nouns = in.readAllStrings();
	        StdOut.println(args[t] + ": " + outcast.outcast(nouns));
	    }
	}
}
