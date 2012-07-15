package natlab.tame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import natlab.tame.builtin.Builtin;
import natlab.tame.callgraph.SimpleFunctionCollection;
import natlab.tame.callgraph.StaticFunction;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysisPrinter;
import natlab.tame.valueanalysis.advancedMatrix.AdvancedMatrixValue;
import natlab.tame.valueanalysis.advancedMatrix.AdvancedMatrixValueFactory;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.ValueFactory;
import natlab.toolkits.filehandling.genericFile.GenericFile;
import natlab.toolkits.path.FileEnvironment;
import natlab.toolkits.path.FilePathEnvironment;

public class AdvancedTamerTool {
	
	public IntraproceduralValueAnalysis<AggrValue<AdvancedMatrixValue>> 
    tameMatlabToSingleFunction(java.io.File mainFile, List<AggrValue<AdvancedMatrixValue>> inputValues){
		GenericFile gFile = GenericFile.create(mainFile); //file -> generic file
		FileEnvironment env = new FileEnvironment(gFile); //get path environment obj
		SimpleFunctionCollection callgraph = new SimpleFunctionCollection(env); //build simple callgraph
		StaticFunction function = callgraph.getAsInlinedStaticFunction(); //inline all


		@SuppressWarnings("unchecked")
		IntraproceduralValueAnalysis<AggrValue<AdvancedMatrixValue>> analysis = 
        new IntraproceduralValueAnalysis<AggrValue<AdvancedMatrixValue>>(
        		null, function, new AdvancedMatrixValueFactory(), 
        		Args.<AggrValue<AdvancedMatrixValue>>newInstance(inputValues));
		//System.out.println("before analyze!");
		analysis.analyze(); //run analysis
		return analysis;
}

/**
* This is the same as tameMatlabToSingleFunction, but takes 
* a list of PrimitiveClassReferences as arguments, rather than
* abstract values. PrimitiveClassReference is an enum of 
* the builtin matlab classes representing matrizes.
*/

	public IntraproceduralValueAnalysis<AggrValue<AdvancedMatrixValue>> 
	tameMatlabToSingleFunctionFromClassReferences(java.io.File mainFile, List<PrimitiveClassReference> inputValues){
//System.out.println(inputValues);
		AdvancedMatrixValueFactory factory = new AdvancedMatrixValueFactory();
		ArrayList<AggrValue<AdvancedMatrixValue>> list = new ArrayList<AggrValue<AdvancedMatrixValue>>(inputValues.size());
		for (PrimitiveClassReference ref : inputValues){
			list.add(new AdvancedMatrixValue(Constant.get(0)));         //TODO change to read isComplex input from user
		}
		return tameMatlabToSingleFunction(mainFile, list);
	}


public static void main(String[] args){
	
	GenericFile gFile = GenericFile.create("/home/2011/vkumar5/hello.m"); //file -> generic file
	/*/home/xuli/test/hello.m */
	FileEnvironment env = new FileEnvironment(gFile); //get path environment obj
	List<AggrValue<AdvancedMatrixValue>> inputValues = getListOfInputValues(args);
	SimpleFunctionCollection callgraph = new SimpleFunctionCollection(env); //build simple callgraph
	ValueFactory<AggrValue<AdvancedMatrixValue>> factory = new AdvancedMatrixValueFactory();
	Args<AggrValue<AdvancedMatrixValue>> someargs = Args.<AggrValue<AdvancedMatrixValue>>newInstance(Collections.EMPTY_LIST); 
	ValueAnalysis<AggrValue<AdvancedMatrixValue>> analysis = new ValueAnalysis<AggrValue<AdvancedMatrixValue>>(
			callgraph, 
			/*Args.newInstance((factory.getValuePropagator().call(Builtin.getInstance("i"),someargs).get(0).get(PrimitiveClassReference.DOUBLE)))*/
			Args.newInstance(inputValues), 
			factory);
	System.out.println(analysis.toString());
	
	
    for (int i = 0; i < analysis.getNodeList().size(); i++){
    	System.out.println(ValueAnalysisPrinter.prettyPrint(
    			analysis.getNodeList().get(i).getAnalysis()));        	
    }
}

public ValueAnalysis<AggrValue<AdvancedMatrixValue>> analyze(String[] args, FileEnvironment env){
	
	//GenericFile gFile = GenericFile.create("/home/2011/vkumar5/hello.m"); 
	/*/home/xuli/test/hello.m */
	//FileEnvironment env = new FileEnvironment(gFile); //get path environment obj
	List<AggrValue<AdvancedMatrixValue>> inputValues = getListOfInputValues(args);
	SimpleFunctionCollection callgraph = new SimpleFunctionCollection(env); //build simple callgraph
	ValueFactory<AggrValue<AdvancedMatrixValue>> factory = new AdvancedMatrixValueFactory();
	Args<AggrValue<AdvancedMatrixValue>> someargs = Args.<AggrValue<AdvancedMatrixValue>>newInstance(Collections.EMPTY_LIST); 
	ValueAnalysis<AggrValue<AdvancedMatrixValue>> analysis = new ValueAnalysis<AggrValue<AdvancedMatrixValue>>(
			callgraph, 
			/*Args.newInstance((factory.getValuePropagator().call(Builtin.getInstance("i"),someargs).get(0).get(PrimitiveClassReference.DOUBLE)))*/
			Args.newInstance(inputValues), 
			factory);
	System.out.println(analysis.toString());
	
	
    for (int i = 0; i < analysis.getNodeList().size(); i++){
    	System.out.println(ValueAnalysisPrinter.prettyPrint(
    			analysis.getNodeList().get(i).getAnalysis()));        	
    }
	return analysis;
}

public static List<AggrValue<AdvancedMatrixValue>> getListOfInputValues(String[] args) {
	List<PrimitiveClassReference> ls = new ArrayList<PrimitiveClassReference>(1);
	//ls.add(PrimitiveClassReference.INT8);
//	ls.add(PrimitiveClassReference.DOUBLE);
//	
//	ArrayList<AggrValue<AdvancedMatrixValue>> list = new ArrayList<AggrValue<AdvancedMatrixValue>>(ls.size());
//	for (PrimitiveClassReference ref : ls){
//		list.add(new AdvancedMatrixValue(ref));      //TODO change to read isComplex input from user
//	}
	
	ArrayList<AggrValue<AdvancedMatrixValue>> list = new ArrayList<AggrValue<AdvancedMatrixValue>>(args.length);
	for(String argSpecs : args)
	{
		String delims = "[\\&]";
		String[] specs = argSpecs.split(delims);
		
		/*TODO Below is just to test. Add actual code to make sense of the argument specs*/ 
		list.add(new AdvancedMatrixValue(PrimitiveClassReference.DOUBLE,specs[2])); 
		
   	
	}
	return list;

}
}