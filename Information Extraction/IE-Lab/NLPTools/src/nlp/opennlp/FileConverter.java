/*
 * This class converts the plain text file into the CRF test input format
 * and output to esp.test
 * @author Guyue HU
 * 
 */
package nlp.opennlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import nlp.opennlp.POSTagger.POSTagging;

public class FileConverter {

	private static final String OUTPUT_PATH = "/Users/guyuehu/Desktop/Courses/comp6490/assignment5/IE-Lab/conll2002/esp.test";
	private static final String INPUT_PATH = "/Users/guyuehu/Desktop/Courses/comp6490/assignment5/IE-Lab/testSet.txt";

	public static void main(String[] args) throws IOException {
		
		// Read the plain text from file
		File inf = new File(INPUT_PATH);
		BufferedReader br = new BufferedReader(new FileReader(inf));
		File outf = new File(OUTPUT_PATH);
		BufferedWriter bw = new BufferedWriter(new FileWriter(outf ));
		
		String text  = "";
		String s = br.readLine();
		while (s != null) {
			text = text + s;
			s = br.readLine();
		}
		br.close();
		
		// tokenize and tag the text
		POSTagger tagger = new POSTagger();
		System.out.println("----------------------\n");
        POSTagging process = tagger.process(text, 1); // 3 best taggings
        System.out.print(process);  
		
        // write the tagged tokens to the file
        StringBuilder sb = new StringBuilder();
		for (int si = 0; si < process._taggings.length; si++) {
			//sb.append("Sentence #" + si + " [" + process._tokens[si].length + "]: ");
			for (int ti = 0; ti < process._taggings[si].length; ti++) {
				for (int wi = 0; wi < process._taggings[si][ti].length; wi++) {
					//System.out.print(process._tokens[si][wi] + "\t");
					bw.write(process._tokens[si][wi] + "\t");
					//System.out.println(process._taggings[si][ti][wi] + "\t" + "0");
					bw.write(process._taggings[si][ti][wi] + "\t" + "O" + "\n");
				}
			}
			bw.write("\n");
			//System.out.println("\n");
		}
		bw.close();
        
	}

}
