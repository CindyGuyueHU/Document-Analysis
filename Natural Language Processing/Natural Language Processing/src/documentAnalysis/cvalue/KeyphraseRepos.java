package documentAnalysis.cvalue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import documentAnalysis.cvalue.struct.Term;
import documentAnalysis.cvalue.struct.TermList;
import documentAnalysis.nlp.struct.ComparableObj;

/**
 * Keyphrase repository
 * 
 *
 */
public class KeyphraseRepos {
	// Key is the length of terms, value is a list of terms
	private TreeMap<Integer, TermList> len2termList;
	// a list of pairs consisting of a term and its c-value
	private List<ComparableObj<Term, Double>> outputTermList;

	public KeyphraseRepos() {
		super();
		len2termList = new TreeMap<Integer, TermList>();
		outputTermList = new ArrayList<ComparableObj<Term, Double>>();
	}
	
	public Term updateTotalFreq(String termKey,String[] term, int length){
		TermList termList;
		if(len2termList.containsKey(length)){
			termList = len2termList.get(length);
		}else{
			termList = new TermList();
			len2termList.put(length, termList);
		}
		Term termStat;
		if(termList.containsKey(termKey)){
			termStat = termList.get(termKey);
		}else{
			termStat = new Term(term);
			termList.put(termKey, termStat);
		}
		termStat.increTotalFrequency();
		return termStat;
	}
	
	public void printAllTerms(){
		System.out.println("Terms after filtering.");
		for(Map.Entry<Integer, TermList> t2l : len2termList.entrySet()){
			System.out.println("Term of length " + t2l.getKey());
			System.out.println(t2l.getValue());
			System.out.println();
		}
	}

	public TreeMap<Integer, TermList> getLen2termList() {
		return len2termList;
	}
	
	public void replaceTermList(int length, TermList newTermList){
		len2termList.put(length, newTermList);
	}

	public void addTermToOutputList(Term term){
		outputTermList.add(new ComparableObj<Term, Double>(term, term.getcValue()));
	}
	
	public void rankOutputTermList(){
		Collections.sort(outputTermList);
	}
	
	public void printOutputTermList(){
		for(ComparableObj<Term,Double> entry : outputTermList){
			Term term = entry.getComObject();
			System.out.println(term.getTermStr() + "\t" + entry.getComValue()+ "\t" + term.getTotalFrequency());
		}
	}
	
	public void printOutputTermListWithAllValues(){
		for(ComparableObj<Term,Double> entry : outputTermList){
			Term term = entry.getComObject();
			System.out.println(term.getTermStr() + "\t" + term.getcValue()+"\t"+term.getNcValue() + "\t" + term.getTotalFrequency());
		}
	}

	public List<ComparableObj<Term, Double>> getOutputTermList() {
		return outputTermList;
	}

	public void setOutputTermList(List<ComparableObj<Term, Double>> outputTermList) {
		this.outputTermList = outputTermList;
	}
}
