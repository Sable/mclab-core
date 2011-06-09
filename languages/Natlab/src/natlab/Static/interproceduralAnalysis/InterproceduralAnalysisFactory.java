package natlab.Static.interproceduralAnalysis;

import natlab.Static.callgraph.StaticFunction;

/**
* @param <F> the FunctionAnalysis type used to analyse each function/argument pair
* @param <A>  the argument set that is given to the function to run the analysis
* @param <R> the result set that the analysis returns for that function
*/
public interface InterproceduralAnalysisFactory<F extends FunctionAnalysis<A,R>, A,R> {
   public F newFunctionAnalysis(
            StaticFunction function, A argumentSet,
            InterproceduralAnalysisNode<F, A,R> node);
}
