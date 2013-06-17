package natlab.tame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import natlab.tame.callgraph.SimpleFunctionCollection;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysisPrinter;
import natlab.tame.valueanalysis.advancedMatrix.AdvancedMatrixValue;
import natlab.tame.valueanalysis.advancedMatrix.AdvancedMatrixValueFactory;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.ValueFactory;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FileEnvironment;

public class AdvancedTamerTool {

	

	public static void main(String[] args) {
		String file = "/media/vineet/19F5-FD4C/Thesis/mclab_git/mclab/languages/Natlab/src/natlab/backends/x10/benchmarks/unit/simplest";
		//String file = "/home/2011/vkumar5/for_test";
		
		String fileIn = file + ".m";
		
		GenericFile gFile = GenericFile.create(fileIn);
		/* /home/xuli/test/hello.m */
		FileEnvironment env = new FileEnvironment(gFile); // get path
															// environment obj
		AdvancedTamerTool tool = new AdvancedTamerTool();
		// System.out.println(args[0]);
		 tool.analyze(
				args, env);


	}

	public ValueAnalysis<AggrValue<AdvancedMatrixValue>> analyze(String[] args,
			FileEnvironment env) {

		List<AggrValue<AdvancedMatrixValue>> inputValues = getListOfInputValues(args);
		SimpleFunctionCollection callgraph = new SimpleFunctionCollection(env); // build
																				// simple
																				// callgraph
		ValueFactory<AggrValue<AdvancedMatrixValue>> factory = new AdvancedMatrixValueFactory();
		Args<AggrValue<AdvancedMatrixValue>> someargs = Args
				.<AggrValue<AdvancedMatrixValue>> newInstance(Collections.EMPTY_LIST);
		ValueAnalysis<AggrValue<AdvancedMatrixValue>> analysis = new ValueAnalysis<AggrValue<AdvancedMatrixValue>>(
				callgraph,
				/*
				 * Args.newInstance((factory.getValuePropagator().call(Builtin.
				 * getInstance
				 * ("i"),someargs).get(0).get(PrimitiveClassReference.DOUBLE)))
				 */
				Args.newInstance(inputValues), factory);
		System.out.println(analysis.toString());

		for (int i = 0; i < analysis.getNodeList().size(); i++) {
			System.out.println(ValueAnalysisPrinter.prettyPrint(analysis
					.getNodeList().get(i).getAnalysis()));
		}
		return analysis;
	}

	public static List<AggrValue<AdvancedMatrixValue>> getListOfInputValues(
			String[] args) {
		List<PrimitiveClassReference> ls = new ArrayList<PrimitiveClassReference>(
				1);
		// ls.add(PrimitiveClassReference.INT8);
		// ls.add(PrimitiveClassReference.DOUBLE);
		//
		// ArrayList<AggrValue<AdvancedMatrixValue>> list = new
		// ArrayList<AggrValue<AdvancedMatrixValue>>(ls.size());
		// for (PrimitiveClassReference ref : ls){
		// list.add(new AdvancedMatrixValue(ref)); //TODO change to read
		// isComplex input from user
		// }

		ArrayList<AggrValue<AdvancedMatrixValue>> list = new ArrayList<AggrValue<AdvancedMatrixValue>>(
				args.length);
		for (String argSpecs : args) {
			// System.out.println(argSpecs);
			String delims = "[\\&]";
			String[] specs = argSpecs.split(delims);

			/*
			 * TODO Below is just to test. Add actual code to make sense of the
			 * argument specs
			 */
			/* TODO also add code to read INT, FLOAT, etc. */
			list.add(new AdvancedMatrixValue(null, PrimitiveClassReference.DOUBLE,
					specs[1], specs[2]));
			// XU changed here to support initial input shape info.
			// @25th,Jul,2012

		}
		return list;

	}
}