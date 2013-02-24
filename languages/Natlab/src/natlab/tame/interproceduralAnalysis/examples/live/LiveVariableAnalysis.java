package natlab.tame.interproceduralAnalysis.examples.live;

import java.util.*;

import natlab.tame.TamerTool;
import natlab.tame.callgraph.Callgraph;
import natlab.tame.callgraph.StaticFunction;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.interproceduralAnalysis.*;
import natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural.DefinedVariablesNameCollector;
import natlab.tame.valueanalysis.simplematrix.*;
import natlab.toolkits.filehandling.genericFile.GenericFile;
import natlab.toolkits.path.FileEnvironment;

/**
 * Interprocedural Backward Flow analysis that records which variables are live or not.
 * A flow set is a map of (variable name)->(live value). The live values are represented
 * by LiveValue objects. variables that are not in the map are always dead.
 * 
 * This analysis operates on a previously computed interprocedural analysis, rather than
 * some fort of function collection. It will thus use the callsites for that previous
 * analysis. The input and output set are just lists of live variables, but for the
 * input we also include the corresponding interprocedural analysis node of the previous
 * analysis, in order to have access to its call site objects.
 * 
 * Note: because this is a backward analysis, the inputs to the function analyses are
 * the live values for the output parameters, and the result is the live values for the
 * input parameters. "calls" are backwards.
 * The backward analysis that this analysis is baded on seems to use the convention
 * that the 'out' set is what flows out of a node, and the 'in' set is what
 * flows into a node (check that?)
 * 
 * This analysis doesn't record calls to builtins for the callsites, but it does
 * require them to be there from the previous analysis. the complete callsite
 * information objects of the previous analysis can be accessed by accessing
 * the interprocedural analysis node of the previous analysis via the 
 * getPreviousAnalysisNode() method in the interproceduralLivaVariableAnalysis class.
 * 
 */
public class LiveVariableAnalysis 
extends InterproceduralAnalysis<IntraproceduralLiveVariableAnalysis, LiveInput, List<LiveValue>>{

	// the factory that is used to created the intraprocedural nodes
	private static class Factory implements InterproceduralAnalysisFactory<IntraproceduralLiveVariableAnalysis, LiveInput, List<LiveValue>>{
		public IntraproceduralLiveVariableAnalysis newFunctionAnalysis(
				StaticFunction function,
				LiveInput argumentSet,
				InterproceduralAnalysisNode<IntraproceduralLiveVariableAnalysis, LiveInput, List<LiveValue>> node) {
			return new IntraproceduralLiveVariableAnalysis(node, argumentSet);
		}
	}
	
	/**
	 * creates a new liver variable analysis - starting with some previous
	 * analysis (like the value analysis). One could also build a callgraph,
	 * and then use callgraph.getAnalysis()
	 * The inputs are actually the live variables for the outputs
	 */
	public LiveVariableAnalysis(
			InterproceduralAnalysis<?,?,?> analysis, List<LiveValue> inputs) {
		super(new Factory(), analysis.getFunctionCollection(), new LiveInput(analysis.getMainNode(),inputs));
	}

	/**
	 * test/example run script. The single argument has to be a supplied m file. as entry point
	 * the main function should have a single double input, and one output
	 */
	public static void main(String[] args) {
		String file = "/Users/Pepe/Desktop/School/Thesis/myBenchmarks/hello.m"; //put a default file for testing, this will work for nobody else ;)
		if (args.length == 1){
			file = args[0];
		}
		//build callgraph
		Callgraph<SimpleMatrixValue> callgraph = TamerTool.getCallgraph(
				new FileEnvironment(GenericFile.create(file)),
				Collections.singletonList(new SimpleMatrixValue(PrimitiveClassReference.DOUBLE)), new SimpleMatrixValueFactory());
		
		LiveVariableAnalysis analysis = new LiveVariableAnalysis(callgraph.getAnalysis(),Collections.singletonList(LiveValue.getLive()));
		//System.out.println(analysis);
		for (int i = 0; i < analysis.getNodeList().size(); i++){
			System.out.println(
				analysis.getNodeList().get(i).getFunction().getAst().getAnalysisPrettyPrinted(
					analysis.getNodeList().get(i).getAnalysis(),true,true));
		}
	}
}
