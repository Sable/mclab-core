package natlab.tame.callgraph;

import natlab.tame.interproceduralAnalysis.InterproceduralAnalysisNode;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysisPrinter;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.aggrvalue.AggrValueFactory;
import natlab.tame.valueanalysis.aggrvalue.MatrixValue;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Res;
import natlab.toolkits.path.FileEnvironment;

/**
 * callgraph that gets constructed by the value analysis
 * 
 * 
 * TODO - go wild with the interfaces
 * TODO - maybe add specific callgraphs that fill in the generics, like SimpleCallgraph extends Callgraph<SimpleMatrixValue>?
 *   - advantage: no need to specify factory, and arguments could be simplified
 */

public class Callgraph<D extends MatrixValue<D>> extends
		GenericCallgraph<ValueAnalysis<AggrValue<D>>, IntraproceduralValueAnalysis<AggrValue<D>>, Args<AggrValue<D>>, Res<AggrValue<D>>>
//implements FunctionCollection  - TODO
{
	public Callgraph(FileEnvironment fileEnvironment,
			Args<? extends AggrValue<D>> args, AggrValueFactory<D> factory){
		super(new ValueAnalysis(
				new IncrementalFunctionCollection(fileEnvironment), args, factory));
	}
	
	public String prettyPrint(){
		//TODO make better - should be actual matlab code for sure?
		String s = "";
		for (InterproceduralAnalysisNode<IntraproceduralValueAnalysis<AggrValue<D>>,?,?> node : getAnalysis().getNodeList()){
			s += ValueAnalysisPrinter.prettyPrint(node.getAnalysis())+"\n\n";
		}
		return s;
	}
	
}
