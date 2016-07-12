package ml.classifier;

import text.*;

public class KMeansClustering {
	
	public final static String DATA_SRC = "blog_data_test/";
	//public final static String DATA_SRC = "data/two_newsgroups/";
	//public final static String DATA_SRC = "data/newsgroups/";
	//public final static String DATA_SRC = "data/more_newsgroups/";

	public final static boolean REMOVE_STOPWORDS = true;
	public final static int     NUM_TOP_WORDS    = 1500;
	public final static int     NUM_CLUSTERS_K    = 3;

	public static void main(String[] args) {
		
		
		UnigramBuilder ub = new UnigramBuilder(
				DATA_SRC, 
				NUM_TOP_WORDS,
				REMOVE_STOPWORDS);
		
		ub.initialize(NUM_CLUSTERS_K);
		
		while (ub.set_clusters()) {
			ub.recalculate_centroids();
		};
		
		ub.print_results();
	}

}
