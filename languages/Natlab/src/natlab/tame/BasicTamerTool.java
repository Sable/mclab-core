package natlab.tame;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.callgraph.SimpleFunctionCollection;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysisPrinter;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.*;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.ValueFactory;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FileEnvironment;

public class BasicTamerTool {
	
	public static void main(String[] args) {
		/*
		 * This main method is just for testing, doesn't follow the convention when passing a file 
		 * to a program, please replace "YOUR_FILE_NAME_AND_PATH" below with your real testing 
		 * file name and its path, and program argument support the type info of the input 
		 * argument, which means you can pass the type info of the input argument to the program, 
		 * currently, the type info is composed like double&3*3&REAL.
		 */
		// file -> generic file
		GenericFile gFile = GenericFile.create("/media/vineet/19F5-FD4C/Thesis/mclab_git/mclab/languages/Natlab/src/natlab/backends/x10/benchmarks/unit/simplest.m");
		// get path environment obj
		FileEnvironment env = new FileEnvironment(gFile);
		// build simple callgraph
		SimpleFunctionCollection callgraph = new SimpleFunctionCollection(env);
		// get input argument type info
		List<AggrValue<BasicMatrixValue>> inputValues = getListOfInputValues(args);
		ValueFactory<AggrValue<BasicMatrixValue>> factory = new BasicMatrixValueFactory();
		ValueAnalysis<AggrValue<BasicMatrixValue>> analysis = 
				new ValueAnalysis<AggrValue<BasicMatrixValue>>(
						callgraph, 
						Args.newInstance(inputValues), 
						factory);
		System.out.println(analysis.toString());

		for (int i = 0; i < analysis.getNodeList().size(); i++) {
			System.out.println(ValueAnalysisPrinter.prettyPrint(analysis
					.getNodeList().get(i).getAnalysis()));
		}
	}

	//TODO give more useful functions!

	public ValueAnalysis<AggrValue<BasicMatrixValue>> analyze (
			String[] args, 
			FileEnvironment env) {
		SimpleFunctionCollection callgraph = new SimpleFunctionCollection(env);
		List<AggrValue<BasicMatrixValue>> inputValues = getListOfInputValues(args);
		ValueFactory<AggrValue<BasicMatrixValue>> factory = new BasicMatrixValueFactory();
		ValueAnalysis<AggrValue<BasicMatrixValue>> analysis = 
				new ValueAnalysis<AggrValue<BasicMatrixValue>>(
				callgraph,Args.newInstance(inputValues), factory);
		System.out.println(analysis.toString());

		for (int i = 0; i < analysis.getNodeList().size(); i++) {
			System.out.println(ValueAnalysisPrinter.prettyPrint(analysis
					.getNodeList().get(i).getAnalysis()));
		}
		return analysis;
	}

	public static List<AggrValue<BasicMatrixValue>> getListOfInputValues (
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
			list.add(new BasicMatrixValue("n", PrimitiveClassReference.DOUBLE, specs[1]));
		}
		return list;
	}
}
