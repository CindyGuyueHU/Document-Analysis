/*
 * This class chunks the size of the training data
 * @author Guyue HU
 */
package nlp.opennlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Chunk {

	private static final String OUTPUT_PATH = "/Users/guyuehu/Desktop/Courses/comp6490/assignment5/IE-Lab/conll2002/esp.train3";
	private static final String INPUT_PATH = "/Users/guyuehu/Desktop/Courses/comp6490/assignment5/IE-Lab/conll2002/esp.train";
	static int size;
	
	
	public static void main(String[] args) throws IOException {

		File inf = new File(INPUT_PATH);
		BufferedReader br = new BufferedReader(new FileReader(inf));
		File outf = new File(OUTPUT_PATH);
		BufferedWriter bw = new BufferedWriter(new FileWriter(outf));

		ArrayList<String> training_data = new ArrayList<String>();

		// read from a file
		String s = br.readLine();
		while (s != null) {
			training_data.add(s);
			s = br.readLine();
		}
		br.close();
		
		size = training_data.size() * 3 / 4;
		
		System.out.println(training_data.size());
		System.out.println(size);
		
		// Pick the first "size" number of lines from the training data 
		for (int i = 0; i < size; i++) {
			bw.write(training_data.get(i) + "\n");
		}
		
		bw.close();
	}

}
