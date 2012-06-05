package natlab.tame;

import java.util.*;

import natlab.tame.builtin.*;
import natlab.tame.callgraph.*;
import natlab.tame.classes.reference.*;
import natlab.tame.valueanalysis.*;
import natlab.tame.valueanalysis.advancedMatrix.AdvancedMatrixValue;
import natlab.tame.valueanalysis.advancedMatrix.AdvancedMatrixValueFactory;
import natlab.tame.valueanalysis.aggrvalue.*;
import natlab.tame.valueanalysis.simplematrix.*;
import natlab.tame.valueanalysis.value.*;
import natlab.toolkits.filehandling.genericFile.*;
import natlab.toolkits.path.*;

public class TamerTool {

	
	/**
	 * tames a matlab project, by inlining all the code into one function,
	 * using the tame IR, and then running the value analysis.
	 * Returns an IntraproceduralValueAnalysis node, which contains
	 * both the function (via getTree()), and the value flowmap associated
	 * with every statement (via getInFlowSets() and getOutFlowSets(), which
	 * return maps that associate flow maps with every statement).
	 * 
	 * The inputs are the location of the entry point (main file), and the
	 * abstract values of the incoming arguments to the entry point, as a 
	 * list of AggrValue<SimpleMatrixValue>, i.e. SimpleMatrixValue. One can
	 * use the constructors provided by SimpleMatrixValue or SimpleMatrixValueFactory
	 * to generate such abstract values. Alternatively, one can use the
	 * tameMatlabToSingleFunctionFromClassReferences method, which only needs
	 * the PrimitiveClassReferences of arguments.
	 * 
	 * This is a rough approximation of matlab, and does not support
	 * recursion (do to full inlining), early returns, or overloading. There may also
	 * be issues with variable names. This is intended to start playing with
	 * simple Matlab projects, to get a sense on how to approach a possible backend.
	 * 
	 * This uses The Builtin information provided by the natlab.tame.builtin package
	 */
	public IntraproceduralValueAnalysis<AggrValue<SimpleMatrixValue>> 
			tameMatlabToSingleFunction(java.io.File mainFile, List<AggrValue<SimpleMatrixValue>> inputValues){
		GenericFile gFile = GenericFile.create(mainFile); //file -> generic file
		FilePathEnvironment path = new FilePathEnvironment(gFile, Builtin.getBuiltinQuery()); //get path environment obj
		FunctionCollection callgraph = new FunctionCollection(path); //build simple callgraph
		StaticFunction function = callgraph.getAsInlinedStaticFunction(); //inline all

		//build intra-analysis
		@SuppressWarnings("unchecked")
		IntraproceduralValueAnalysis<AggrValue<SimpleMatrixValue>> analysis = 
				new IntraproceduralValueAnalysis<AggrValue<SimpleMatrixValue>>(
						null, function, new SimpleMatrixValueFactory(), 
						Args.<AggrValue<SimpleMatrixValue>>newInstance(inputValues));
		analysis.analyze(); //run analysis
		return analysis;
	}
		
	/**
	 * This is the same as tameMatlabToSingleFunction, but takes 
	 * a list of PrimitiveClassReferences as arguments, rather than
	 * abstract values. PrimitiveClassReference is an enum of 
	 * the builtin matlab classes representing matrizes.
	 */
	public IntraproceduralValueAnalysis<AggrValue<SimpleMatrixValue>> 
			tameMatlabToSingleFunctionFromClassReferences(java.io.File mainFile, List<PrimitiveClassReference> inputValues){
		SimpleMatrixValueFactory factory = new SimpleMatrixValueFactory();
		ArrayList<AggrValue<SimpleMatrixValue>> list = new ArrayList<AggrValue<SimpleMatrixValue>>(inputValues.size());
		for (PrimitiveClassReference ref : inputValues){
			list.add(new SimpleMatrixValue(ref));
		}
		return tameMatlabToSingleFunction(mainFile, list);
	}
	
	
	
	//TODO give more useful functions!
	
	
	
	public static void main(String[] args) {
		
		GenericFile gFile = GenericFile.create("/home/2011/vkumar5/hello.m"); //file -> generic file
		FilePathEnvironment path = new FilePathEnvironment(gFile, Builtin.getBuiltinQuery()); //get path environment obj
		FunctionCollection callgraph = new FunctionCollection(path); //build simple callgraph
		ValueFactory<AggrValue<AdvancedMatrixValue>> factory = new AdvancedMatrixValueFactory();
		Args<AggrValue<AdvancedMatrixValue>> someargs = Args.<AggrValue<AdvancedMatrixValue>>newInstance(Collections.EMPTY_LIST); 
		ValueAnalysis<AggrValue<AdvancedMatrixValue>> analysis = new ValueAnalysis<AggrValue<AdvancedMatrixValue>>(
				callgraph, 
				Args.newInstance((factory.getValuePropagator().call(Builtin.getInstance("i"),someargs , 1).get(0).get(PrimitiveClassReference.DOUBLE))), 
				factory);
		System.out.println(analysis.toString());
		
		
        for (int i = 0; i < analysis.getNodeList().size(); i++){
        	System.out.println(ValueAnalysisPrinter.prettyPrint(
        			analysis.getNodeList().get(i).getAnalysis()));        	
        }
	}


	//getCallgraph(inputfile,args)
	//getCallgraph(inputfile,factory,args)
	//...
}







