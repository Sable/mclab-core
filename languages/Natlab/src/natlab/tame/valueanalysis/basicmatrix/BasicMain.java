package natlab.tame.valueanalysis.basicmatrix;

import java.util.*;
import java.io.*;

import natlab.tame.BasicTamerTool;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.tir.TIRFunction;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysisPrinter;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;

public class BasicMain {

	public static void main(String[] args){
		String file = "/home/2011/vkumar5/hello.m";

		//  prompt the user to enter the number of input argument of this function
		System.out.print("Please enter the number of input argument of this function: ");

		//  open up standard input
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	    String argNum = null;

	    //  read the username from the command-line; need to use try/catch with the
	    //  readLine() method
	    try{
	    	argNum = br.readLine();
	    }catch (IOException ioe) {
	         System.out.println("IO error trying to read your name!");
	         System.exit(1);
	    }


   // 	int intArgNum = Integer.parseInt(argNum);
    	int intArgNum = 1;
	    System.out.println("Thanks for the number of input argument of this function, " + intArgNum);
	    
		BasicTamerTool tool = new BasicTamerTool();
		List<PrimitiveClassReference> ls = new ArrayList<PrimitiveClassReference>(intArgNum);
		for(int i=1;i<=intArgNum;i++){
			ls.add(PrimitiveClassReference.DOUBLE);
		}
		IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>>  analysis = tool.tameMatlabToSingleFunctionFromClassReferences(
				new java.io.File(file),ls);
		
		TIRFunction function = analysis.getTree();
		System.out.println("-------------IR-ast-----------------");
		System.out.println(function.getPrettyPrinted()); //print IR-ast
		System.out.println("-------------value analysis---------");
		System.out.println(ValueAnalysisPrinter.prettyPrint(analysis)); //combined flow analysis/ast print
		
	}
}
