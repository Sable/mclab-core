package natlab.tame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import natlab.tame.callgraph.SimpleFunctionCollection;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysisPrinter;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValueFactory;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.ValueFactory;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FileEnvironment;

public class BasicTamerTool {

	/*public IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> tameMatlabToSingleFunction(
			java.io.File mainFile, List<AggrValue<BasicMatrixValue>> inputValues) {
		GenericFile gFile = GenericFile.create(mainFile);
		FileEnvironment env = new FileEnvironment(gFile);
		SimpleFunctionCollection callgraph = new SimpleFunctionCollection(env);
		StaticFunction function = callgraph.getAsInlinedStaticFunction();

		// build intra-analysis
		@SuppressWarnings("unchecked")
		IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis = new IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>>(
				null, function, new BasicMatrixValueFactory(),
				Args.<AggrValue<BasicMatrixValue>> newInstance(inputValues));
		analysis.analyze();
		return analysis;
	}*/

	/**
	 * This is the same as tameMatlabToSingleFunction, but takes a list of
	 * PrimitiveClassReferences as arguments, rather than abstract values.
	 * PrimitiveClassReference is an enum of the builtin matlab classes
	 * representing matrizes.
	 */
	// XU expands it to support initial shape input
	/*public IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> tameMatlabToSingleFunctionFromClassReferences(
			java.io.File mainFile, List<PrimitiveClassReference> inputValues) {
		// System.out.println(inputValues);
		ArrayList<AggrValue<BasicMatrixValue>> list = new ArrayList<AggrValue<BasicMatrixValue>>(
				inputValues.size());
		for (PrimitiveClassReference ref : inputValues) {
			list.add(new BasicMatrixValue(Constant.get(1.0)));// XU modified
																// @21:53
																// 5.28.2012
			// list.add(new BasicMatrixValue(new BasicMatrixValue(ref),(new
			// ShapeFactory()).newShapeFromIntegers(Constant.get(1.0).getShape())));
			// //here, we create list of basicMatrixValue with the input default
			// class (double)
		}
		return tameMatlabToSingleFunction(mainFile, list);
	}*/

	//TODO give more useful functions!
	
	public static void main(String[] args){
		
		GenericFile gFile = GenericFile.create("/Users/Pepe/Desktop/School/Thesis/myBenchmarks/hello.m"); //file -> generic file
		FileEnvironment env = new FileEnvironment(gFile); //get path environment obj
		SimpleFunctionCollection callgraph = new SimpleFunctionCollection(env); //build simple callgraph
		List<AggrValue<BasicMatrixValue>> inputValues = getListOfInputValues(args);
		ValueFactory<AggrValue<BasicMatrixValue>> factory = new BasicMatrixValueFactory();
		ValueAnalysis<AggrValue<BasicMatrixValue>> analysis = new ValueAnalysis<AggrValue<BasicMatrixValue>>(
				callgraph,
				// Args.newInstance((factory.getValuePropagator().call(Builtin.getInstance("i"),someargs).get(0).get(PrimitiveClassReference.DOUBLE))),
				Args.newInstance(inputValues), factory);
		System.out.println(analysis.toString());
		// System.out.println(analysis.getNodeList().get(0).getAnalysis().getTree());

		for (int i = 0; i < analysis.getNodeList().size(); i++) {
			System.out.println(ValueAnalysisPrinter.prettyPrint(analysis
					.getNodeList().get(i).getAnalysis()));
		}
	}

	public ValueAnalysis<AggrValue<BasicMatrixValue>> analyze(String[] args,
			FileEnvironment env) {
		SimpleFunctionCollection callgraph = new SimpleFunctionCollection(env);
		List<AggrValue<BasicMatrixValue>> inputValues = getListOfInputValues(args);
		ValueFactory<AggrValue<BasicMatrixValue>> factory = new BasicMatrixValueFactory();
		Args<AggrValue<BasicMatrixValue>> someargs = Args
				.<AggrValue<BasicMatrixValue>> newInstance(Collections.EMPTY_LIST);
		ValueAnalysis<AggrValue<BasicMatrixValue>> analysis = new ValueAnalysis<AggrValue<BasicMatrixValue>>(
				callgraph,Args.newInstance(inputValues), factory);
		System.out.println(analysis.toString());

		for (int i = 0; i < analysis.getNodeList().size(); i++) {
			System.out.println(ValueAnalysisPrinter.prettyPrint(analysis
					.getNodeList().get(i).getAnalysis()));
		}
		return analysis;
	}

	public static List<AggrValue<BasicMatrixValue>> getListOfInputValues(
			String[] args) {
		ArrayList<AggrValue<BasicMatrixValue>> list = new ArrayList<AggrValue<BasicMatrixValue>>(
				args.length);
		for (String argSpecs : args) {
			String delims = "[\\&]";
			String[] specs = argSpecs.split(delims);

			/*
			 * TODO Below is just to test. Add actual code to make sense of the
			 * argument specs
			 */
			list.add(new BasicMatrixValue("n", PrimitiveClassReference.DOUBLE,
					specs[1]));

		}
		return list;
	}
}
