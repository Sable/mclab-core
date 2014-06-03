package natlab.tame.callgraph;

import natlab.tame.interproceduralAnalysis.FunctionAnalysis;
import natlab.tame.interproceduralAnalysis.InterproceduralAnalysis;
/**
 * a callgraph is an object that wraps an interprocedural analysis to be a callgraph object  
 */
public class GenericCallgraph<I extends InterproceduralAnalysis<F,A,R>,F extends FunctionAnalysis<A, R>,A,R> {
	I analysis;
	public GenericCallgraph(I analysis) {
		this.analysis = analysis;
	}
	
	public I getAnalysis(){
		return analysis;
	}
}
