package natlab.tame;

import java.util.*;

import natlab.options.Options;
import natlab.tame.builtin.*;
import natlab.tame.builtin.classprop.ClassPropTool;
import natlab.tame.builtin.classprop.ast.*;
import natlab.tame.callgraph.*;
import natlab.tame.classes.reference.*;
import natlab.tame.valueanalysis.*;
import natlab.tame.valueanalysis.advancedMatrix.AdvancedMatrixValue;
import natlab.tame.valueanalysis.advancedMatrix.AdvancedMatrixValueFactory;
import natlab.tame.valueanalysis.aggrvalue.*;
import natlab.tame.valueanalysis.simplematrix.*;
import natlab.tame.valueanalysis.value.*;
import natlab.toolkits.filehandling.genericFile.*;
import natlab.toolkits.path.*;

public class TamerTool {

	
	/**
	 * tames a matlab project, by inlining all the code into one function,
	 * using the tame IR, and then running the value analysis.
	 * Returns an IntraproceduralValueAnalysis node, which contains
	 * both the function (via getTree()), and the value flowmap associated
	 * with every statement (via getInFlowSets() and getOutFlowSets(), which
	 * return maps that associate flow maps with every statement).
	 * 
	 * The inputs are the location of the entry point (main file), and the
	 * abstract values of the incoming arguments to the entry point, as a 
	 * list of AggrValue<SimpleMatrixValue>, i.e. SimpleMatrixValue. One can
	 * use the constructors provided by SimpleMatrixValue or SimpleMatrixValueFactory
	 * to generate such abstract values. Alternatively, one can use the
	 * tameMatlabToSingleFunctionFromClassReferences method, which only needs
	 * the PrimitiveClassReferences of arguments.
	 * 
	 * This is a rough approximation of matlab, and does not support
	 * recursion (do to full inlining), early returns, or overloading. There may also
	 * be issues with variable names. This is intended to start playing with
	 * simple Matlab projects, to get a sense on how to approach a possible backend.
	 * 
	 * This uses The Builtin information provided by the natlab.tame.builtin package
	 */
	public IntraproceduralValueAnalysis<AggrValue<SimpleMatrixValue>> 
			tameMatlabToSingleFunction(java.io.File mainFile, List<AggrValue<SimpleMatrixValue>> inputValues){
		GenericFile gFile = GenericFile.create(mainFile); //file -> generic file
		FileEnvironment env = new FileEnvironment(gFile); //get path environment obj
		SimpleFunctionCollection callgraph = new SimpleFunctionCollection(env); //build simple callgraph
		StaticFunction function = callgraph.getAsInlinedStaticFunction(); //inline all

		//build intra-analysis
		@SuppressWarnings("unchecked")
		IntraproceduralValueAnalysis<AggrValue<SimpleMatrixValue>> analysis = 
				new IntraproceduralValueAnalysis<AggrValue<SimpleMatrixValue>>(
						null, function, new SimpleMatrixValueFactory(), 
						Args.<AggrValue<SimpleMatrixValue>>newInstance(inputValues));
		analysis.analyze(); //run analysis
		return analysis;
	}
		
	/**
	 * This is the same as tameMatlabToSingleFunction, but takes 
	 * a list of PrimitiveClassReferences as arguments, rather than
	 * abstract values. PrimitiveClassReference is an enum of 
	 * the builtin matlab classes representing matrizes.
	 */
	public IntraproceduralValueAnalysis<AggrValue<SimpleMatrixValue>> 
			tameMatlabToSingleFunctionFromClassReferences(java.io.File mainFile, List<PrimitiveClassReference> inputValues){
		SimpleMatrixValueFactory factory = new SimpleMatrixValueFactory();
		ArrayList<AggrValue<SimpleMatrixValue>> list = new ArrayList<AggrValue<SimpleMatrixValue>>(inputValues.size());
		for (PrimitiveClassReference ref : inputValues){
			list.add(new SimpleMatrixValue(ref));
		}
		return tameMatlabToSingleFunction(mainFile, list);
	}
	
	
	
	//TODO give more useful functions!
	//TODO give usage example, like I showed Vineet
	
	//example main...
	public static void main(String[] args) {
		GenericFile gFile = GenericFile.create("/home/xu/for_test/McFor/mcfor_test/dich/drv_dich.m"); //file -> generic file
		FileEnvironment env = new FileEnvironment(gFile); //get path environment obj
		SimpleFunctionCollection callgraph = new SimpleFunctionCollection(env); //build simple callgraph
		ValueFactory<AggrValue<SimpleMatrixValue>> factory = new SimpleMatrixValueFactory();
		Args<AggrValue<SimpleMatrixValue>> someargs = Args.<AggrValue<SimpleMatrixValue>>newInstance(Collections.EMPTY_LIST); 
		ValueAnalysis<AggrValue<SimpleMatrixValue>> analysis = new ValueAnalysis<AggrValue<SimpleMatrixValue>>(
				callgraph, 
				Args.newInstance((factory.getValuePropagator().call(Builtin.getInstance("i"),someargs).get(0).get(PrimitiveClassReference.DOUBLE))), 
				factory);
		System.out.println(analysis.toString());
		
		
        for (int i = 0; i < analysis.getNodeList().size(); i++){
        	System.out.println(ValueAnalysisPrinter.prettyPrint(
        			analysis.getNodeList().get(i).getAnalysis()));        	
        }
	}


	//getCallgraph(inputfile,args)
	//getCallgraph(inputfile,factory,args)
	//...

	/**
	 * given a file environment and input classes, returns a callgraph
	 */
	public static Callgraph<SimpleMatrixValue> getSimpleCallgraphFromClassReferences(FileEnvironment env,List<PrimitiveClassReference> inputClasses){
		ArrayList<AggrValue<SimpleMatrixValue>> list = new ArrayList<AggrValue<SimpleMatrixValue>>(inputClasses.size());
		for (PrimitiveClassReference ref : inputClasses){
			list.add(new SimpleMatrixValue(ref));
		}
		return new Callgraph<SimpleMatrixValue>(
				env,
				Args.<AggrValue<SimpleMatrixValue>>newInstance(list),
				new SimpleMatrixValueFactory());
	}
	
	
	public static Callgraph<SimpleMatrixValue> getSimpleCallgraph(FileEnvironment env,List<AggrValue<SimpleMatrixValue>> inputValues){
		return new Callgraph<SimpleMatrixValue>(
				env,
				Args.<AggrValue<SimpleMatrixValue>>newInstance(inputValues),
				new SimpleMatrixValueFactory());
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <D extends MatrixValue<D>> Callgraph<D> getCallgraph(FileEnvironment env,List<? extends AggrValue<D>> inputValues,AggrValueFactory<D> factory){
		return new Callgraph<D>(
				env,
				new Args(inputValues),
				factory);
	}
	
	
	
	/**
	 * the entry point coming from natlab.Main - uses the options object to select the proper behavior
	 */
	public static void main(Options options){
		//** parse args ********
		FileEnvironment fileEnvironment = new FileEnvironment(options); //get path/files
		boolean inline = options.inline(); //get inline
		boolean outputToFile = false; //output TODO

		//arguments - TODO for now just parse them as inputs
		String args = "double"; //start with the default
		if (options.arguments() != null && options.arguments().length() > 0){
			args = options.arguments();
		}
		CP classProp = ClassPropTool.parse(args);
		List<CP> classPropList = new LinkedList<CP>();
		if (classProp instanceof CPChain){
			classPropList = ((CPChain)classProp).asList();
		} else if (classProp instanceof CPBuiltin){
			classPropList.add(classProp);
		} else {
			throw new UnsupportedOperationException("Arguments have to be a list of builtin classes");
		}
		List<PrimitiveClassReference> inputClasses = new LinkedList<PrimitiveClassReference>();
		for (CP element : classPropList){
			if (element instanceof CPBuiltin){
				inputClasses.add(PrimitiveClassReference.valueOf(((CPBuiltin)element).toString().toUpperCase()));
			} else {
				throw new UnsupportedOperationException(
						"Arguments have to be list of builtin classes, received "+element);
			}
		}
		
		
		//** build callgraph *******		
		ArrayList<AggrValue<SimpleMatrixValue>> list = new ArrayList<AggrValue<SimpleMatrixValue>>(inputClasses.size());
		for (PrimitiveClassReference ref : inputClasses){
			list.add(new SimpleMatrixValue(ref));
		}
		
		
		Callgraph<SimpleMatrixValue> callgraph = new Callgraph<SimpleMatrixValue>(
				fileEnvironment,
				Args.<AggrValue<SimpleMatrixValue>>newInstance(list),
				new SimpleMatrixValueFactory());
		
		
		//** output info ***********
		System.out.println(callgraph.prettyPrint());
		
	}
	
}







