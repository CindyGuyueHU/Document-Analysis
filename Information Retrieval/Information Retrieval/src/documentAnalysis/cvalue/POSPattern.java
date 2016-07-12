package documentAnalysis.cvalue;

public enum POSPattern {
	//N+N
	NOUN_PATTERN,
	//(A|N)+N
	ADJ_NOUN_PATTERN,
	//((A|N)+|((A|N)*(NP)?)(A|N)*)N
	COMPLEX_PATTERN
}
