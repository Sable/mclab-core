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
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.*;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.ValueFactory;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FileEnvironment;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.shape.ShapeFactory;

public class BasicTamerTool {
	
	public static void main(String[] args){
		
		GenericFile gFile = GenericFile.create("/home/xu/for_test/testRange.m"); //file -> generic file
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

	//TODO give more useful functions!

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
			list.add(new BasicMatrixValue(PrimitiveClassReference.DOUBLE,specs[1]));
		}
		return list;
	}
}
