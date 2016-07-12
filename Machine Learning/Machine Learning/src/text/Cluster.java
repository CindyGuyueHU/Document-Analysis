
/*
 * The class of clusters and operations on clusters
 * author: Guyue HU
 */
package text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Cluster {
	
	HashMap<String, Double> centroid = new HashMap<String, Double>(); // centroid of the cluster
	HashMap<Doc, Double> docs = new HashMap<Doc, Double> (); // all docs in this cluster
	ArrayList<Doc> printed = new ArrayList<Doc>(); // recorded printed docs
	
	public Cluster(HashMap<String, Double> c){
		centroid = c;
	}

	// add a doc and its distance from the centroid to the cluster
	public void set_doc(Doc d, double c) {
		docs.put(d, c);
		
	}

	// remove a doc from a cluster
	public void delete_doc(Doc d) {
		docs.remove(d);		
	}

	// recalculate the centroid of the cluster
	public void recal() {
		//System.out.println("tried");
		HashMap<String, Double> newcentroid = centroid;
		for (String s : newcentroid.keySet()) {
			newcentroid.put(s, 0.0);
		}
		for (Doc d : docs.keySet()) {
			HashMap<String, Double> tf = d.tf;
			for (String s : tf.keySet()) {
				if (newcentroid.get(s) == null) {
					newcentroid.put(s, tf.get(s));
				} else {
					newcentroid.put(s, newcentroid.get(s) + tf.get(s));
				}
			}
		}	
		double k = (double)docs.size();
		for (String s : newcentroid.keySet()) {
			newcentroid.put(s, newcentroid.get(s)/k);
		}
	}

	// print the results
	public void print_r() {
		List mapValues = new ArrayList(docs.values());
		Collections.sort(mapValues);
//		for (int i = 0; i < mapValues.size(); i++) {
//			System.out.println(mapValues.get(i));
//		}
		
		for (int i = mapValues.size(); i > mapValues.size()-5 ; i-- ){
			if (i >= 0) {
				for (Doc d : docs.keySet()) {
					double d1 = (double) mapValues.get(i-1);
					double d2 = docs.get(d);
					if ((d1 == d2) && !(printed.contains(d))) {
						System.out.println(d.name);
						printed.add(d);
					}
				}
			}
		}
		
	}
	

}
