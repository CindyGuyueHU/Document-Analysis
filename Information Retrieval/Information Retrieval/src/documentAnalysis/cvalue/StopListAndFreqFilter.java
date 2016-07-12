package documentAnalysis.cvalue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import documentAnalysis.cvalue.struct.Term;
import documentAnalysis.cvalue.struct.TermList;
import documentAnalysis.nlp.utils.StopList;

/**
 * Stop words and frequency filters.
 * 
 * @author Lizhen Qu
 *
 */
public class StopListAndFreqFilter {
	private int lowestFreq;
	private Set<String> stopList;
	public StopListAndFreqFilter(int lowestFreq, String stopListFilePath) throws IOException{
		super();
		this.lowestFreq = lowestFreq;
		this.stopList = StopList.getStopList(stopListFilePath);
	}
	
	public void filter(KeyphraseRepos repos){
		Set<Integer> emptyEntriesToRM = new HashSet<Integer>();
		for(Map.Entry<Integer, TermList> i2termList : repos.getLen2termList().entrySet()){
			int length = i2termList.getKey();
			TermList termList = i2termList.getValue();
			TermList newTermList = new TermList();
			for(Map.Entry<String, Term> s2t : termList.entrySet()){
				String termKey = s2t.getKey();
				Term termStat = s2t.getValue();
				
				if(termStat.getTotalFrequency() > lowestFreq){
					boolean containedStopWord = false;
					for(String word : termStat.getTerm()){
						if(stopList.contains(word)){
							containedStopWord = true;
						}
					}
					if(!containedStopWord){
						newTermList.put(termKey, termStat);
					}
				}
			}
			if(newTermList.size() > 0)
				repos.replaceTermList(length, newTermList);
			else
				emptyEntriesToRM.add(length);
			termList.clear();
		}
		for(int len : emptyEntriesToRM){
			repos.getLen2termList().remove(len);
		}
	}
}
