package natlab.tame.tamerplus;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import natlab.options.Options;
import natlab.tame.TamerTool;
import natlab.tame.callgraph.Callgraph;
import natlab.tame.callgraph.StaticFunction;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.tamerplus.transformation.TransformationEngine;
import natlab.tame.valueanalysis.simplematrix.SimpleMatrixValue;
import natlab.tame.valueanalysis.simplematrix.SimpleMatrixValueFactory;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FileEnvironment;

public class TamerPlusMain {
	public static void main(String args[]) {
		String fileName = "/home/sameer/interview/mclab/mbrt/drv_mbrt.m";//args[0];

		// Get the callgraph of the main function
		Callgraph<SimpleMatrixValue> callgraph = TamerTool.getCallgraph(
				new FileEnvironment(GenericFile.create(fileName)), Collections
						.singletonList(new SimpleMatrixValue(null,
								PrimitiveClassReference.DOUBLE)),
				new SimpleMatrixValueFactory());

		List<StaticFunction> functionList = callgraph.getAnalysis()
				.getFunctionCollection().getAllFunctions();

		for (StaticFunction function : functionList) {
			//TamerPlusUtils.debugMode();
			System.out.println(function.getAst().getPrettyPrinted());
			System.err.println(TransformationEngine.forAST(function.getAst())
					.getTIRToMcSAFIRWithoutTemp().getTransformedTree()
					.getPrettyPrinted());
		}
	}

	public static void main(Options options) {
		FileEnvironment fileEnvironment = new FileEnvironment(options);

		// Get the callgraph of the main function
		Callgraph<SimpleMatrixValue> callgraph = TamerTool.getCallgraph(
				fileEnvironment, Collections
						.singletonList(new SimpleMatrixValue(null,
								PrimitiveClassReference.DOUBLE)),
				new SimpleMatrixValueFactory());

		List<StaticFunction> functionList = callgraph.getAnalysis()
				.getFunctionCollection().getAllFunctions();

		for (StaticFunction function : functionList) {
			System.out.println(function.getAst().getPrettyPrinted());
			System.err.println(TransformationEngine.forAST(function.getAst())
					.getTIRToMcSAFIRWithoutTemp().getTransformedTree()
					.getPrettyPrinted());

			// write the transformed result to files.
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(
						fileEnvironment.getPwd().getPath() 
						+ "/" 
						+ function.getName() + ".tamerplus"));
				out.write(TransformationEngine.forAST(function.getAst())
						.getTIRToMcSAFIRWithoutTemp().getTransformedTree()
						.getPrettyPrinted().toString());
				out.flush();
				out.close();
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}
}
