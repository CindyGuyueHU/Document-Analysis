/** Exhibits standard Lucene searches for ranking documents.
 * 
 * @author Scott Sanner, Paul Thomas
 */

package search;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

public class SimpleSearchRanker {

	private static final String RESULT_PATH = "/Users/guyuehu/Desktop/Courses/comp6490/assignment1/trec_eval.8.1/gov.res";
	private static final String TOPIC_PATH = "/Users/guyuehu/Desktop/Courses/comp6490/assignment1/gov-test-collection/topics/gov.topics";
	
	String        _indexPath;
	StandardQueryParser   _parser;
	IndexReader   _reader;
	IndexSearcher _searcher;
	DecimalFormat _df = new DecimalFormat("#.####");
	
	public SimpleSearchRanker(String index_path, String default_field, Analyzer a) 
		throws IOException {
		_indexPath = index_path;
		Directory d = new SimpleFSDirectory(Paths.get(_indexPath));
		DirectoryReader dr = DirectoryReader.open(d);
		_searcher  = new IndexSearcher(dr);
		_parser    = new StandardQueryParser(a);
	}
	
	public void doSearch(String id, String query, int num_hits, PrintWriter ps) 
		throws Exception {
		
		Query q = _parser.parse(query, "CONTENT");
		TopScoreDocCollector collector = TopScoreDocCollector.create(num_hits);
		_searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		System.out.println("Found " + hits.length + " hits " + " for query " + query + ":");
		for (int i = 0; i < hits.length; i++) {
		    int docId = hits[i].doc;
		    Document d = _searcher.doc(docId);
		    // ps.println((i + 1) + ". (" + _df.format(hits[i].score) + ") " + d.get("PATH"));
		    ps.println(id + "\t" + "Q0" + "\t" + d.get("NAME") + "\t" + i + "\t" +  _df.format(hits[i].score) + "\t" + "CINDYSEARCH");
		    System.out.println(id + "\t" + "Q0" + "\t" + d.get("NAME") + "\t" + i + "\t" +  _df.format(hits[i].score) + "\t" + "CINDYSEARCH");
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		String INDEX_PATH = "src/search/lucene.index";
		String default_field = "CONTENT";
		ArrayList<String> topics = new ArrayList<String>();
		ArrayList<String> idx = new ArrayList<String>();
		
		FileIndexBuilder b = new FileIndexBuilder(INDEX_PATH);
		SimpleSearchRanker r = new SimpleSearchRanker(b._indexPath, default_field, b._analyzer);
		
		// See the following for query parser syntax
		//   https://lucene.apache.org/core/5_2_0/queryparser/org/apache/lucene/queryparser/classic/package-summary.html#package_description
		//
		// IN SHORT: the default scoring function for OR terms is a variant of TF-IDF
		//           where one can individually boost the importance of query terms with
		//           a multipler using ^
		
		/*
		// Standard single term
		r.doSearch("Obama", 4, System.out);

		// Multiple term (implicit OR)
		r.doSearch("Obama Hillary", 5, System.out);

		// Wild card
		r.doSearch("Ob*ma", 5, System.out);
		
		// Edit distance
		r.doSearch("Obama~.4", 5, System.out);
		
		// Fielded search (FIELD:...), boolean (AND OR NOT)
		r.doSearch("FIRST_LINE:Obama AND Hillary", 5, System.out);
		r.doSearch("FIRST_LINE:Obama AND NOT Hillary", 5, System.out);

		// Phrase search (slop factor ~k allows words to be within k distance)
		r.doSearch("\"Barack Obama\"", 5, System.out);
		r.doSearch("\"Barack Obama\"~5", 5, System.out);
		
		// Note: can boost terms or subqueries using ^ (e.g., ^10 or ^.1) -- default is 1 
		r.doSearch("Obama^10 Hillary^0.1", 5, System.out);
		r.doSearch("(FIRST_LINE:\"Barack Obama\")^10 OR Hillary^0.1", 5, System.out);

		// Reversing boost... see change in ranking 
		r.doSearch("Obama^0.1 Hillary^10", 5, System.out);
		r.doSearch("(FIRST_LINE:\"Barack Obama\")^0.1 OR Hillary^10", 5, System.out);

		// Complex query
		r.doSearch("(FIRST_LINE:\"Barack Obama\"~5^10 AND Obama~.4) OR Hillary", 5, System.out);
		*/
		
		try (BufferedReader br = new BufferedReader(new FileReader(TOPIC_PATH))) {
			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
				int index = sCurrentLine.indexOf(" ");
				String s1 = sCurrentLine.substring(0, index);
				String s = sCurrentLine.substring(index+1, sCurrentLine.length());
				idx.add(s1);
				topics.add(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		PrintWriter writer = new PrintWriter(RESULT_PATH);
		
		for (int i = 0; i < topics.size(); i++) {
			String t = topics.get(i);
			t = t.replace("/", " OR ");
			t = MyStemmer.Stem(t);
			String id = idx.get(i);
			System.out.println(t);
			r.doSearch(id, t, 50, writer);
		}
		
		writer.close();
		
	}

}
