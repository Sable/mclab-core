package natlab.tame;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
	
	private Boolean debug = true;
	private Boolean doIntOk = true;
	private Boolean doVarRename = false; //TODO test and debug the analysis before turning it on
	public static void main(String[] args) {
		// String file =
		// "/home/2011/vkumar5/mclab_git/mclab/languages/Natlab/src/natlab/backends/x10/benchmarks/mc_for_benchmarks/nb1d/drv_nb1d";
		// String file = "/home/2011/vkumar5/for_test";
		String file = "/Volumes/Macintosh HD 2/work/McGill/mclab/mix10/benchmarks/matlab/new-benchmarks/fdtd/drv_fdtd";
		String fileIn = file + ".m";
		
		String fileOut = "/Volumes/Macintosh HD 2/work/McGill/mclab/mix10/benchmarks/matlab/new-benchmarks/fdtd/fdtd.tame";

		GenericFile gFile = GenericFile.create(fileIn);
		/* /home/xuli/test/hello.m */
		FileEnvironment env = new FileEnvironment(gFile); // get path
															// environment obj
		AdvancedTamerTool tool = new AdvancedTamerTool();
		// System.out.println(args[0]);
		ValueAnalysis<AggrValue<AdvancedMatrixValue>> analysis = tool.analyze(args, env);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileOut,true));
			
			for (int i = 0; i < analysis.getNodeList().size(); i++) {
				out.write(ValueAnalysisPrinter.prettyPrint(analysis
						.getNodeList().get(i).getAnalysis()));
			}
			//out.write(analysis.toString());
			out.close();
		} catch (IOException e) {
			System.out.println("Exception ");

		}
	}

	public ValueAnalysis<AggrValue<AdvancedMatrixValue>> analyze(String[] args,
			FileEnvironment env) {

		List<AggrValue<AdvancedMatrixValue>> inputValues = getListOfInputValues(args);
		SimpleFunctionCollection callgraph = new SimpleFunctionCollection(env); 
		
		/*
		 * Rename variables with conflicting types
		 */
//		if (doVarRename)
//			callgraph = RenameTypeConflictVars.renameConflictVarsInDifferentWebs(
//				callgraph, inputValues);
		
	
		ValueFactory<AggrValue<AdvancedMatrixValue>> factory = new AdvancedMatrixValueFactory();
		ValueAnalysis<AggrValue<AdvancedMatrixValue>> analysis = new ValueAnalysis<AggrValue<AdvancedMatrixValue>>(
				callgraph, Args.newInstance(inputValues), factory);
		
		/*
		 * transform callgraph with IntegerOkay analysis
		 */
//		if (doIntOk)
//		    analysis = IntOkAnalysis.analyzeForIntOk(callgraph, inputValues);
//		
//		if (doVarRename)
//			callgraph = RenameTypeConflictVars.renameConflictVarsInDifferentWebs(
//				callgraph, inputValues);
//		
//		if (doIntOk)
//		    analysis = IntOkAnalysis.analyzeForIntOk(callgraph, inputValues);
		
		if(debug)  System.out.println(analysis.toString());
		
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
		ArrayList<AggrValue<AdvancedMatrixValue>> list = new ArrayList<AggrValue<AdvancedMatrixValue>>(
				args.length);
		for (String argSpecs : args) {
			
			String delims = "[\\&]";
			String[] specs = argSpecs.split(delims);

			/* TODO also add code to read INT, FLOAT, etc. */
			list.add(new AdvancedMatrixValue("n",
					PrimitiveClassReference.DOUBLE, specs[1], specs[2]));
			}
		return list;

	}
}