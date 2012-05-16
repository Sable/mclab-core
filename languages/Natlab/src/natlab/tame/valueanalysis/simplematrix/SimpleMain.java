package natlab.tame.valueanalysis.simplematrix;

import java.util.Collections;

import natlab.tame.TamerTool;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.tir.TIRFunction;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysisPrinter;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;

public class SimpleMain {
	
	public static void main(String args){
		String file = args;
		TamerTool tool = new TamerTool();
		IntraproceduralValueAnalysis<AggrValue<SimpleMatrixValue>>  analysis = tool.tameMatlabToSingleFunctionFromClassReferences(
				new java.io.File(file),Collections.singletonList(PrimitiveClassReference.DOUBLE));
		
		TIRFunction function = analysis.getTree();
		System.out.println("-------------IR-ast-----------------");
		System.out.println(function.getPrettyPrinted()); //print IR-ast
		System.out.println("-------------value analysis---------");
		System.out.println(ValueAnalysisPrinter.prettyPrint(analysis)); //combined flow analysis/ast print
		
	}
}
