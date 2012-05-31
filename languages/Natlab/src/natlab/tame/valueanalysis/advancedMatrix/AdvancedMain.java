package natlab.tame.valueanalysis.advancedMatrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import natlab.tame.AdvancedTamerTool;
import natlab.tame.TamerToolPlusShape;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.tir.TIRFunction;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysisPrinter;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.simplematrix.SimpleMatrixValue;

public class AdvancedMain {
	public static void main(String[] args){
		String file = "/home/2011/vkumar5/hello.m";
		AdvancedTamerTool tool = new AdvancedTamerTool();
		IntraproceduralValueAnalysis<AggrValue<AdvancedMatrixValue>>  analysis = tool.tameMatlabToSingleFunctionFromClassReferences(
				new java.io.File(file),Collections.singletonList(PrimitiveClassReference.DOUBLE));
		
		TIRFunction function = analysis.getTree();
		System.out.println("-------------IR-ast-----------------");
		System.out.println(function.getPrettyPrinted()); //print IR-ast
		System.out.println("-------------Advanced value analysis---------");
		System.out.println(ValueAnalysisPrinter.prettyPrint(analysis)); //combined flow analysis/ast print
		
	}
}

/*Note
*
List<PrimitiveClassReference> ls = new ArrayList<PrimitiveClassReference>(4);
ls.add(PrimitiveClassReference.DOUBLE);
ls.add(PrimitiveClassReference.DOUBLE);
ls.add(PrimitiveClassReference.DOUBLE);
ls.add(PrimitiveClassReference.DOUBLE);
IntraproceduralValueAnalysis<AggrValue<SimpleMatrixValue>>  analysis = tool.tameMatlabToSingleFunctionFromClassReferences(
		new java.io.File(file),ls);
*/