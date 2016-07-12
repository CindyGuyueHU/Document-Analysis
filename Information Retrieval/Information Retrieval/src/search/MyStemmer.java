package search;

import java.util.StringTokenizer;

import org.tartarus.snowball.ext.PorterStemmer;

public class MyStemmer {
	
	public static String Stem(String content) {
		String stemed_content = "";
		String word;
		StringTokenizer st = new StringTokenizer(content);
		PorterStemmer stem = new PorterStemmer();
		
		while (st.hasMoreTokens()) {
			word = st.nextToken();
			stem.setCurrent(word);
			stem.stem();
			word = stem.getCurrent();
			stemed_content = stemed_content + word.toLowerCase() + " ";
			//System.out.println(stemed_content);
		}
		
		return stemed_content;
	}

	public static void main(String[] args) {
	     System.out.println(Stem("Mining mined mine"));
	}

}
