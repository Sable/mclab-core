package natlab.tame;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import natlab.options.Options;
import natlab.tame.callgraph.SimpleFunctionCollection;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.interproceduralAnalysis.InterproceduralAnalysisNode;
import natlab.tame.tamerplus.utils.IntOkAnalysis;
import natlab.tame.tamerplus.utils.RenameTypeConflictVars;
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

	private static Boolean doIntOk = true;
	private static Boolean doVarRename = false;

	/**
	 * This main method is just for testing, doesn't follow the convention when
	 * passing a file to a program, please replace "YOUR_FULL_PATH_FILE_NAME"
	 * below with your real testing file with its full path, and program
	 * argument support the type info of the input argument, which means you can
	 * pass the type info of the input argument to the program, currently, the
	 * type info is composed like double&3*3&REAL.
	 */
	public static void main(String[] args) {
		// file -> generic file
		GenericFile gFile = GenericFile
				.create("/home/sameer/interview/mclab/foo.m");
		// get path environment obj
		FileEnvironment env = new FileEnvironment(gFile);
		// build simple callgraph
		SimpleFunctionCollection callgraph = new SimpleFunctionCollection(env);
		// get input argument type info
		BasicTamerTool.doIntOk = false;
		ValueAnalysis<AggrValue<BasicMatrixValue>> analysis = analyze(args, env);
  }

	// TODO give more useful functions!
	public static void setDoIntOk(boolean value) {
		doIntOk = value;
	}

	public static ValueAnalysis<AggrValue<BasicMatrixValue>> analyze(
			String[] args, FileEnvironment env) {
		SimpleFunctionCollection callgraph = new SimpleFunctionCollection(env);
		List<AggrValue<BasicMatrixValue>> inputValues = getListOfInputValues(args);
		ValueFactory<AggrValue<BasicMatrixValue>> factory = new BasicMatrixValueFactory();

//		if (doVarRename)
//			callgraph = RenameTypeConflictVars
//					.renameConflictVarsInDifferentWebs(callgraph, inputValues);

		ValueAnalysis<AggrValue<BasicMatrixValue>> analysis = new ValueAnalysis<AggrValue<BasicMatrixValue>>(
				callgraph, Args.newInstance(inputValues), factory);

//		if (doIntOk)
//			analysis = IntOkAnalysis.analyzeForIntOk(callgraph, inputValues);

		return analysis;
	}

	public static List<AggrValue<BasicMatrixValue>> getListOfInputValues(
			String[] args) {
		ArrayList<AggrValue<BasicMatrixValue>> list = new ArrayList<AggrValue<BasicMatrixValue>>(
				args.length);
		for (String argSpecs : args) {
			String delims = "[\\&]";
			String[] specs = argSpecs.split(delims);
			PrimitiveClassReference clsType = PrimitiveClassReference
					.valueOf(specs[0]);
			list.add(new BasicMatrixValue(null, clsType, specs[1], specs[2]));
		}
		return list;
	}

	/**
	 * the entry point coming from natlab.Main - uses the options object to
	 * select the proper behavior
	 */
	public static void main(Options options) {
		FileEnvironment fileEnvironment = new FileEnvironment(options); // get
																		// path/files
		SimpleFunctionCollection callgraph = new SimpleFunctionCollection(
				fileEnvironment);

		// arguments - TODO for now just parse them as inputs
		String args = "DOUBLE&1*1&REAL"; // start with the default
		if (options.arguments() != null && options.arguments().length() > 0) {
			args = options.arguments();
		}

		// TODO now it's for testing...
		String[] argsList = { args };

		List<AggrValue<BasicMatrixValue>> inputValues = getListOfInputValues(argsList);
		ValueFactory<AggrValue<BasicMatrixValue>> factory = new BasicMatrixValueFactory();
		ValueAnalysis<AggrValue<BasicMatrixValue>> analysis = new ValueAnalysis<AggrValue<BasicMatrixValue>>(
				callgraph, Args.newInstance(inputValues), factory);

		for (int i = 0; i < analysis.getNodeList().size(); i++) {
			System.out.println(ValueAnalysisPrinter.prettyPrint(analysis
					.getNodeList().get(i).getAnalysis()));

			// write the transformed result to files.
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(
						fileEnvironment.getPwd().getPath()
								+ "/"
								+ analysis.getNodeList().get(i).getFunction()
										.getName() + ".tamer"));
				out.write(ValueAnalysisPrinter.prettyPrint(analysis
						.getNodeList().get(i).getAnalysis()));
				out.flush();
				out.close();
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}
}
