package natlab.Static.interproceduralAnalysis;

import natlab.Static.callgraph.StaticFunction;

public interface InterproceduralAnalysisFactory<
       Analysis extends FunctionAnalysis<?,Arg,Res>,Arg,Res> {
    public Analysis newFunctionAnalysis(
            StaticFunction function, Arg argumentSet,
            InterproceduralAnalysisNode<Analysis, Arg, Res> node);
}
