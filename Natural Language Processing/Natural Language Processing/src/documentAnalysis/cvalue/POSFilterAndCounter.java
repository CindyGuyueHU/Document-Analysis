package documentAnalysis.cvalue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import documentAnalysis.nlp.TextProcessingException;
import documentAnalysis.nlp.struct.Sentence;
import documentAnalysis.nlp.struct.TaggedToken;
import documentAnalysis.nlp.utils.POSTagConverter;

/**
 * POS filter, counting word frequency and co-occurrence of context words.
 * 
 *
 */
public class POSFilterAndCounter {
	private KeyphraseRepos phraseRepos;
	private Pattern filterPattern;
	// Store lemma of each term
	private boolean useLemma = false;
	private boolean useLowercase = true;
	private static final String nounPattern = "N+N";
	private static final String adjPattern = "(A|N)+N";
	private static final String complexPattern = "((A|N)+|((A|N)*(NP)?)(A|N)*)N";

	public POSFilterAndCounter(KeyphraseRepos phraseRepos, POSPattern posPattern) {
		this.phraseRepos = phraseRepos;

		if (POSPattern.NOUN_PATTERN.equals(posPattern)) {
			filterPattern = Pattern.compile(nounPattern);
		}

		if (POSPattern.ADJ_NOUN_PATTERN.equals(posPattern)) {
			filterPattern = Pattern.compile(adjPattern);
		}

		if (POSPattern.COMPLEX_PATTERN.equals(posPattern)) {
			filterPattern = Pattern.compile(complexPattern);
		}
	}

	/**
	 * The method will filter terms based POS patterns and store the term
	 * frequency into keyphrase repository for the latter usage.
	 * 
	 * @param sens
	 *            input sentence
	 * @param contextSearchRange
	 *            search window size
	 */
	public void filterAndCountTotalFreq(Sentence sens, int contextSearchRange) {
		String posSeq = convertToPOSStr(sens);

		if (filterPattern != null) {
			// TODO: Use regular expressions to implement POS filter for the
			// filterPattern.
			// Put the filtered terms into phraseRepos.
			// Each term will be stored based on the two options:
			// 1. useLemma: each word in a term should be converted into lemma
			// 2. useLowercase: each word in a term should be converted into
			// lower case.
			//
			// Hint: the group function of regular expressions will find the
			// longest match of a string.
			// The substrings of the longest match could also match the target
			// pattern.
			// For example, use group function of regular expression you will
			// find "base noun phrase"
			// as a match to the pattern "N+N". However, "noun phrase" is also a
			// match of the N+N pattern.
			// Take the exceptional cases into account while you implement POS
			// filters.

			// System.out.println(sens);
			// System.out.println(posSeq);

			Set<String> searchedTerms = new HashSet<String>();

			Matcher matcher = filterPattern.matcher(posSeq);
			while (matcher.find()) {
				if ((contextSearchRange == 0) || (matcher.start() == 0)) {
					
					String substring = matcher.group();
					//System.out.println(substring + "\t[" + matcher.start()
					//		+ " , " + matcher.end() + "]");

					String termKey = "";
					String[] term = new String[matcher.end() - matcher.start()];
					Sentence subSens1 = new Sentence();
					Sentence subSens2 = new Sentence();
					int idx = 0;
					for (int i = matcher.start(); i < matcher.end(); i++) {
						String s = convertString(sens.get(i));
						termKey = termKey + s + " ";
						term[idx] = s;
						idx++;
						if (i != matcher.end() - 1) {
							subSens1.add(sens.get(i));
						}
						if (i != matcher.start()) {
							subSens2.add(sens.get(i));
						}
					}

					indexTerm(matcher.end() - matcher.start(), term, termKey,
							searchedTerms);
					//System.out.println(termKey);

					if (subSens1.size() > 1) {
						filterAndCountTotalFreq(subSens1, -1);
					}

					if ((subSens2.size() > 1) && (contextSearchRange == 0)) {
						filterAndCountTotalFreq(subSens2, 0);
					}
				}
			}

		}
	}

	/**
	 * Each TaggedToken will be converted to a string based on the two options:
	 * 1. useLemma: each word in a term should be converted into lemma 2.
	 * useLowercase: each word in a term should be converted into
	 * 
	 */
	private String convertString(TaggedToken t) {
		String s = "";
		if (useLemma) {
			s = t.getLemma();
		} else {
			s = t.toString();
		}

		if (useLowercase) {
			s = s.toLowerCase();
		}

		return s;
	}

	/**
	 * Put terms with length > 1 into phrase repository.
	 * 
	 * @param len
	 * @param term
	 * @param termKey
	 * @param searchedTerms
	 *            the set of terms that are already searched.
	 */
	private void indexTerm(int len, String[] term, String termKey,
			Set<String> searchedTerms) {
		if (len > 1) {
			phraseRepos.updateTotalFreq(termKey, term, len);
			searchedTerms.add(termKey);
		}
	}

	/**
	 * Convert a sentence into a sequence of coarse-grained POS tags. Each
	 * coarse-grained POS tag is denoted by only one character.
	 * 
	 * @param sens
	 * @return
	 */
	private String convertToPOSStr(Sentence sens) {
		StringBuilder buffer = new StringBuilder();

		for (TaggedToken token : sens) {
			buffer.append(POSTagConverter.map(token.getPos()));
		}
		return buffer.toString();
	}

	public boolean isUseLemma() {
		return useLemma;
	}

	public void setUseLemma(boolean useLemma) {
		this.useLemma = useLemma;
	}

	public boolean isUseLowercase() {
		return useLowercase;
	}

	public void setUseLowercase(boolean useLowercase) {
		this.useLowercase = useLowercase;
	}

}
