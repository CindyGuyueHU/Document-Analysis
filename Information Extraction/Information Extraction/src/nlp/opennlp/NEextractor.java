package nlp.opennlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/*
 * This class read the result of crf_test and print it in required format to a file
 * @author Guyue HU
 * 
 */
public class NEextractor {

	private static final String OUTPUT_PATH = "/Users/guyuehu/Desktop/Courses/comp6490/assignment5/IE-Lab/NE-Extractor-Result.txt";
	private static final String INPUT_PATH = "/Users/guyuehu/Desktop/Courses/comp6490/assignment5/IE-Lab/conll2002/esp.result";

	static HashMap<String, Integer> loc = new HashMap<String, Integer>();
	static HashMap<String, Integer> org = new HashMap<String, Integer>();
	static HashMap<String, Integer> per = new HashMap<String, Integer>();
	static HashMap<String, Integer> misc = new HashMap<String, Integer>();
	
	public static void main(String[] args) throws IOException {
		
		
		File inf = new File(INPUT_PATH);
		BufferedReader br = new BufferedReader(new FileReader(inf));
		File outf = new File(OUTPUT_PATH);
		BufferedWriter bw = new BufferedWriter(new FileWriter(outf));

		// read from a file
		String s = br.readLine();
		while (s != null) {
			process(s);
			s = br.readLine();
		}
		br.close();
		
		//System.out.println(loc);
		//System.out.println(org);
		//System.out.println(per);
		//System.out.println(misc);
		
		
		display("Location", loc, bw);
		display("Organization", org, bw);
		display("Person", per, bw);
		display("Misc", misc, bw);
		
		bw.close();
	}

	// write the result in required format
	private static void display(String s, HashMap<String, Integer> hm, BufferedWriter bw) throws IOException {
		bw.write(s + "\n");
		for (Map.Entry<String, Integer> entry : hm.entrySet()) {
			  String key = entry.getKey();
			  String value = entry.getValue().toString();
			  bw.write("\t" + key + "\t" + value + "\n");
			  //System.out.println(key + "\t" + value);
			}
		bw.write("\n");
	}

	// process the results of crf_test to hash maps of different entity categories
	private static void process(String s) {
		StringTokenizer st = new StringTokenizer(s);
		int i = 0;
		String word = "";
		String tag = "";
		while (st.hasMoreTokens()) {
			i++;
			//System.out.println(i);
			if (i == 1) {
				word = st.nextToken();
			} else if (i == 4) {
				tag = st.nextToken();
			} else {
				st.nextToken();
			}
		}
		//System.out.println(word + "\t" + tag);
		int index = tag.indexOf("-");
		//System.out.println("index :"+ index);
		if (index != -1) {
			tag = tag.substring(index+1, tag.length());
			//System.out.println(tag);
		}
		
		if (tag.equals("ORG")) {
			put_to_hashmap(word, org);
		} else if (tag.equals("LOC")) {
			put_to_hashmap(word, loc);
		} else if (tag.equals("PER")) {
			put_to_hashmap(word, per);
		} else if (tag.equals("MISC")) {
			put_to_hashmap(word,misc);
		}
	}

	private static void put_to_hashmap(String word,
			HashMap<String, Integer> hm) {
		if (hm.containsKey(word)) {
			hm.put(word, hm.get(word)+1);
		} else {
			hm.put(word, 1);
		}
		
	}

}
