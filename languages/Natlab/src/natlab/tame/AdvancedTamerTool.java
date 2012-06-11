package natlab.tame;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.builtin.Builtin;
import natlab.tame.callgraph.SimpleFunctionCollection;
import natlab.tame.callgraph.StaticFunction;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.advancedMatrix.AdvancedMatrixValue;
import natlab.tame.valueanalysis.advancedMatrix.AdvancedMatrixValueFactory;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.*;
import natlab.tame.valueanalysis.value.Args;
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
			list.add(new AdvancedMatrixValue(ref));         //here, we create list of basicMatrixValue with the input default class (double)
		}
		return tameMatlabToSingleFunction(mainFile, list);
	}
}
