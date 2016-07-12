/** Simple Unigram analysis of data.
 * 
 * @author Scott Sanner
 */

package text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

import nlp.nicta.filters.StopWordChecker;
import util.DocUtils;
import util.FileFinder;

public class UnigramBuilder {

	public StopWordChecker _swc = new StopWordChecker();
	public TreeSet<WordCount> _wordCounts;
	public HashMap<String, Integer> _topWord2Index;
	public ArrayList<String> features = new ArrayList<String>();
	public ArrayList<Cluster> clusters = new ArrayList<Cluster>();

	public ArrayList<Doc> docs = new ArrayList<Doc>();

	public UnigramBuilder(String directory, int num_top_words,
			boolean ignore_stop_words) {
		ArrayList<File> files = FileFinder.GetAllFiles(directory, "", true);
		HashMap<String, WordCount> word2count = new HashMap<String, WordCount>();
		System.out.println("Found " + files.size() + " files.");

		int file_count = 0;
		for (File f : files) {
			String file_content = DocUtils.ReadFile(f);
			Map<Object, Double> word_counts = DocUtils
					.ConvertToFeatureMap(file_content);
			for (Map.Entry<Object, Double> me : word_counts.entrySet()) {
				String key = (String) me.getKey();
				WordCount wc = word2count.get(key);
				if (wc == null) {
					wc = new WordCount(key, (int) me.getValue().doubleValue());
					word2count.put(key, wc);
				} else
					wc._count++;
			}
			if (++file_count % 500 == 0)
				System.out.println("Read " + file_count + " files.");
		}
		System.out
				.println("Extracted " + word2count.size() + " unique tokens.");

		_wordCounts = new TreeSet<WordCount>(word2count.values());
		_topWord2Index = new HashMap<String, Integer>();
		int index = 0;
		for (WordCount wc : _wordCounts) {
			if (ignore_stop_words && _swc.isStopWord(wc._word))
				continue;
			System.out.println("[index:" + index + "] " + wc);
			_topWord2Index.put(wc._word, index);
			features.add(wc._word);
			if (++index >= num_top_words)
				break;
		}

		// Use sparse data structure to store the feature vectors
		// author: Guyue HU
		for (File f : files) {
			docs.add(new Doc());
		}

		index = 0;

		for (File f : files) {
			Doc d = docs.get(index);
			d.name = f.getName();
			String file_content = DocUtils.ReadFile(f);
			Map<Object, Double> word_counts = DocUtils
					.ConvertToFeatureMap(file_content);
			for (Map.Entry<Object, Double> me : word_counts.entrySet()) {
				String key = (String) me.getKey();
				Integer wc = _topWord2Index.get(key);
				if (wc != null) {

					d.tf.put(key, me.getValue().doubleValue());
				}
			}
			docs.set(index, d);
			index++;
		}

		for (int i = 0; i < index; i++) {
			System.out.println("[Blog " + i + "]");
			System.out.println(docs.get(i).tf);
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UnigramBuilder UB = new UnigramBuilder("blog_data/" /* data source */,
		/* num top words */100, /* remove stopwords */true);
	}

	// randomly initialize the centroids of k clusters
	public void initialize(int k) {
		for (int i = 0; i < k; i++) {
			int temp = ThreadLocalRandom.current().nextInt(0,  docs.size());
			System.out.println("initial: " + temp);
			clusters.add(new Cluster(docs.get(temp).tf));
		}
		
	}

	// set a doc to the nearest centroid cluster
	// flag = false if no change
	public boolean set_clusters() {
		boolean flag = false;
		for (int i = 0; i < docs.size(); i++) {
			int clus = docs.get(i).cluster;
			double current_cos = CosineDistance(docs.get(i), clusters.get(clus).centroid);
			clusters.get(clus).set_doc(docs.get(i), current_cos);
			for (int j = 0; j < clusters.size(); j++) {
				double cos = CosineDistance(docs.get(i), clusters.get(j).centroid);
				if (cos > current_cos) {
					current_cos = cos;
					docs.get(i).set_cluster(j);
					clusters.get(clus).delete_doc(docs.get(i));
					clusters.get(j).set_doc(docs.get(i), cos);
					flag = true;
				}
			}
			
		}
		
		return flag;
		
	}

	// calculate the (unnormalized) cosine distance
	private double CosineDistance(Doc doc1, HashMap<String, Double> tf2) {
		double value = 0;
		HashMap<String, Double> tf1 = doc1.tf;
		
		if (tf1.size() > tf2.size()) {
			for (String s : tf2.keySet()) {
				if (tf1.get(s) != null) {
					value += tf1.get(s) * tf2.get(s);
				}
			}
		} else {
			for (String s : tf1.keySet()) {
				if (tf2.get(s) != null) {
					value = value + tf1.get(s) * tf2.get(s);
				}
			}
		}
		return value;
	}

	// recalculate the centroid for all clusters
	public void recalculate_centroids() {
		for (int k = 0; k < clusters.size(); k++) {
			clusters.get(k).recal();
		}
		
	}

	// print the results
	public void print_results() {
		for (int k = 0; k < clusters.size(); k++) {
			System.out.println("[ Cluster: " + (k+1) + " ]");		
			clusters.get(k).print_r();
		}
		
	}

}
