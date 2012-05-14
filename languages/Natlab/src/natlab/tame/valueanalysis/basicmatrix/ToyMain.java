package natlab.tame.valueanalysis.basicmatrix;

import java.util.Collections;

import natlab.tame.TamerToolPlusShape;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.tir.TIRFunction;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysisPrinter;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.*;

public class ToyMain {

	public static void main(String[] args){
		String file = "/home/2011/vkumar5/mclab/Project/languages/Natlab/src/natlab/backends/x10/testing/shape_and_complex/hello.m";
		TamerToolPlusShape tool = new TamerToolPlusShape();
		IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>>  analysis = tool.tameMatlabToSingleFunctionFromClassReferences(
				new java.io.File(file),Collections.singletonList(PrimitiveClassReference.DOUBLE));
		
		TIRFunction function = analysis.getTree();
		System.out.println("-------------IR-ast-----------------");
		System.out.println(function.getPrettyPrinted()); //print IR-ast
		System.out.println("-------------value analysis---------");
		System.out.println(ValueAnalysisPrinter.prettyPrint(analysis)); //combined flow analysis/ast print
		
	}
}
