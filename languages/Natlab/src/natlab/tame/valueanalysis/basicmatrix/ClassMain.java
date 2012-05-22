package natlab.tame.valueanalysis.basicmatrix;

import java.util.Collections;

import natlab.tame.TamerTool;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.tir.TIRFunction;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysisPrinter;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.simplematrix.*;

public class ClassMain {

	public static void main(String[] args){
		String file = "/home/xuli/test/hello.m";
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
