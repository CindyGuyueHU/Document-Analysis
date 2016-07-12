/*
 * This is the class contains the information about all documents
 */

package text;

import java.util.ArrayList;
import java.util.HashMap;

public class Doc {
	
	public String name; // the name of the document
	public int cluster = 0; // the cluster it belongs to
	public HashMap<String, Double> tf = new HashMap<String, Double>(); // term frequency (sparse representation)
	
	// set the cluster of the documents
	public void set_cluster(int j) {
		cluster = j;
		
	}

}
