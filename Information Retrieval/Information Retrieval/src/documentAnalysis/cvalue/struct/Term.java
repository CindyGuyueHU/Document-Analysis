package documentAnalysis.cvalue.struct;

import java.util.HashMap;
import java.util.Map;

/**
 * Keyphrase (Term).
 * 
 * @author Lizhen Qu
 *
 */
public class Term {
	private String[] term;
	private int totalFrequency;
	private int freqAsNestedStr;
	private int numLongerCandidateTerms;
	private double cValue;
	private double ncValue;
	private Map<String, Integer> contextWord2freq; 
	
	public Term(String[] term) {
		super();
		this.term = term;
		this.totalFrequency = 0;
		freqAsNestedStr = 0;
		this.numLongerCandidateTerms = 0;
		contextWord2freq = new HashMap<String, Integer>();
	}
	
	public void increTotalFrequency(){
		totalFrequency++;
	}

	public int getTotalFrequency() {
		return totalFrequency;
	}

	public void setTotalFrequency(int totalFrequency) {
		this.totalFrequency = totalFrequency;
	}

	public int getFreqAsNestedStr() {
		return freqAsNestedStr;
	}

	/**
	 * Add frequency as nested substrings.
	 * @param freqOfLongerCandidateTerm
	 */
	public void addFreqAsNestedStr(int freqOfLongerCandidateTerm) {
		this.freqAsNestedStr += freqOfLongerCandidateTerm;
	}

	public int getNumLongerCandidateTerms() {
		return numLongerCandidateTerms;
	}
	
	public void increNumLongerCandidateTerms(){
		numLongerCandidateTerms++;
	}

	public String[] getTerm() {
		return term;
	}
	
	public String getTermStr() {
		StringBuilder str = new StringBuilder();
		
		for(String s : term){
			str.append(s);
			str.append(' ');
		}
		
		return str.toString().trim();
	}

	public double getcValue() {
		return cValue;
	}

	public void setcValue(double cValue) {
		this.cValue = cValue;
	}

	public double getNcValue() {
		return ncValue;
	}

	public void setNcValue(double ncValue) {
		this.ncValue = ncValue;
	}
	
	public void increContextWordFreq(String word){
	
		if(contextWord2freq.containsKey(word)){
			contextWord2freq.put(word, contextWord2freq.get(word) + 1);
		}else{
			contextWord2freq.put(word, 1);
		}
	}

	public Map<String, Integer> getContextWord2freq() {
		return contextWord2freq;
	}

	@Override
	public String toString() {
		return " [" + totalFrequency
				+ ", " + freqAsNestedStr
				+ ", " + numLongerCandidateTerms
				+ "]";
	}
	
	
}
