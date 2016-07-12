package documentAnalysis.cvalue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import documentAnalysis.cvalue.struct.ContextWord;
import documentAnalysis.cvalue.struct.Term;
import documentAnalysis.cvalue.struct.TermList;
import documentAnalysis.nlp.NLPPipeline;
import documentAnalysis.nlp.TextProcessingException;
import documentAnalysis.nlp.struct.ComparableObj;
import documentAnalysis.nlp.struct.Document;
import documentAnalysis.nlp.struct.Sentence;

/**
 * The main class for keyphrase extraction.
 * 
 *
 */
public class KeyphraseExtractor {

	private KeyphraseRepos phraseRepos;
	// start of configuration parameters
	// convert all words to lemmas
	private boolean useLemma = true;
	// convert all words in lower case
	private boolean useLowercase = true;
	// select pos filter
	private POSPattern posPattern = POSPattern.COMPLEX_PATTERN;
	// set minimal term frequency
	private int minGlobalFreq = 2;
	// set threshold of cvalue
	private float cValueThreshold = 1f;

	// end of configuration parameters

	public KeyphraseExtractor() {
		super();
		phraseRepos = new KeyphraseRepos();
	}

	public void applyFilters(File dir) throws TextProcessingException {

		POSFilterAndCounter posFilter = new POSFilterAndCounter(phraseRepos,
				posPattern);
		posFilter.setUseLemma(useLemma);
		posFilter.setUseLowercase(useLowercase);

		NLPPipeline pipeline = new NLPPipeline(useLemma, true);
		int searchRange = 0;
		for (Document doc : pipeline.process(dir)) {
			for (Sentence sentence : doc) {
				if (sentence.size() > 0) {
					posFilter.filterAndCountTotalFreq(sentence, searchRange);
				}
			}
		}

		try {
			StopListAndFreqFilter stopListFreqFilter = new StopListAndFreqFilter(
					minGlobalFreq,
					"/Users/guyuehu/Desktop/Courses/comp6490/assignment2/NLP_assignment/Q1_code/stoplist");
			stopListFreqFilter.filter(phraseRepos);
			phraseRepos.printAllTerms();
		} catch (IOException e) {
			throw new TextProcessingException(e);
		}
	}

	public void computeCValue() {
		TreeMap<Integer, TermList> len2termList = phraseRepos.getLen2termList();

		// TODO: implement the statistical part of C-Value in this method and
		// generate a list of terms ranked by c-value.
		// Hint : reuse the existing functions in this package will
		// significantly save your time.

		int maxLength = 200;
		Set<String> appearedTerms = new HashSet<String>();
		double cv;

		while (!len2termList.containsKey(maxLength)) {
			maxLength--;
		}

		while (maxLength > 1) {
			TermList tList = len2termList.get(maxLength);
			for (String s : tList.keySet()) {
				Term t = tList.get(s);

				// for every term in descent order
				// calculate the C-Value

				if (appearedTerms.contains(s)) {
					cv = log(maxLength)
							* (t.getTotalFrequency() - 1.0
									/ t.getNumLongerCandidateTerms()
									* t.getFreqAsNestedStr());
					// System.out.println();
					// System.out.println("Frequency of '" + s + "' is : "
					// + t.getTotalFrequency());
					// System.out.println("NumLongerCandidateTerms = "
					// + t.getNumLongerCandidateTerms());
					// System.out.println("FreqAsNestedStr = "
					// + t.getFreqAsNestedStr());
				} else {
					cv = log(maxLength) * t.getTotalFrequency();
					appearedTerms.add(s);
					// System.out.println();
					// System.out.println("Frequency of '" + s + "' is : "
					// + t.getTotalFrequency());
				}

				t.setcValue(cv);

				// if the c value exceeds the threshold
				// add current term to the output list
				// and for every subterm: update NumLongerCandidateTerms and
				// FreqAsNestedStr

				if (cv > cValueThreshold) {
					// System.out.println("CValue = " + cv);
					phraseRepos.addTermToOutputList(t);
					// System.out.println("MaxLength= " + maxLength);
					for (int i = 2; i < maxLength; i++) {
						TermList bList = len2termList.get(i);
						for (int j = 0; j < maxLength - i + 1; j++) {
							String subTermKey = "";
							String[] tl = t.getTerm();
							for (int k = j; k < j + i; k++) {
								subTermKey = subTermKey + tl[k] + " ";
							}
							if (bList.keySet().contains(subTermKey)) {
								Term subTerm = bList.get(subTermKey);
								subTerm.increNumLongerCandidateTerms();
								subTerm.addFreqAsNestedStr(t
										.getTotalFrequency()
										- t.getFreqAsNestedStr());
								appearedTerms.add(subTermKey);
							}
						}
					}
				}

			}

			maxLength--;
		}

		phraseRepos.rankOutputTermList();

	}

	public double log(int len) {
		return Math.log(len) / Math.log(2);
	}

	public void printOutputTermList() {
		phraseRepos.printOutputTermList();
	}

	public void printOutputTermListWithAllValues() {
		phraseRepos.printOutputTermListWithAllValues();
	}

	public static void main(String[] args) {
		KeyphraseExtractor extractor = new KeyphraseExtractor();

		// It requires the file path of the data directory as the argument of
		// the main program.
		String errorMsg = "INPUT_DIRECTORY_FILE_PATH\n";
		if (args.length == 1) {
			String dirFilePath = args[0];
			File dir = new File(dirFilePath);
			if (dir.exists()) {
				if (dir.isDirectory()) {
					try {
						extractor.applyFilters(dir);
						extractor.computeCValue();
						System.out.println("##TERM\tC-Value\tFrequency");
						extractor.printOutputTermList();

					} catch (TextProcessingException e) {
						e.printStackTrace();
					}

				} else {
					System.err.println(dirFilePath + " is not a directory.");
				}
			} else {
				System.err.println(dirFilePath + " cannot be found.");
			}

		} else {
			System.err.println(errorMsg);
		}

	}

}
