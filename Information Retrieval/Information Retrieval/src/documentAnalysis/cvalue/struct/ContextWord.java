package documentAnalysis.cvalue.struct;

/**
 * Computation of global weights of context words.
 * 
 * @author Lizhen Qu
 *
 */
public class ContextWord {
	private String word;
	private double globalWeight;
	private int globalFreq;
	
	public ContextWord(String word) {
		super();
		this.word = word;
		globalFreq = 0;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}	

	public double getGlobalWeight() {
		return globalWeight;
	}

	public void setGlobalWeight(double globalWeight) {
		this.globalWeight = globalWeight;
	}

	public int getGlobalFreq() {
		return globalFreq;
	}

	public void increGlobalFreq(int freq) {
		this.globalFreq+=freq;
	}
	
	
}
