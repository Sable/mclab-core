// =========================================================================== //
//                                                                             //
// Copyright 2011 Anton Dubrau and McGill University.                          //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

package natlab.tame.interproceduralAnalysis;

/**
 * TODO 
 * node in the interprocedural analysis 
 * - stores function, functionanalysis, callstring, argument, result
 * - takes care of a map of <callsite,interproc analysis node>
 * 
 * @param <F> the FunctionAnalysis type used to analyse each function/argument pair
 * @param <A>  the argument set that is given to the function to run the analysis
 * @param <R> the result set that the analysis returns for that function
 */

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import natlab.tame.callgraph.FunctionCollection;
import natlab.tame.callgraph.StaticFunction;
import natlab.toolkits.path.FunctionReference;
import analysis.StructuralAnalysis;
import ast.ASTNode;

public class InterproceduralAnalysisNode<F extends FunctionAnalysis<A, R>, A, R> {
	private StaticFunction function;
	private F functionAnalysis;
	private CallString<A> callString;
	private InterproceduralAnalysis<F, A, R> interprocAnalysis;
	private InterproceduralAnalysisFactory<F, A, R> factory;
	private A argument;
	private FunctionCollection callgraph;
	private HashMap<ASTNode<?>, Callsite<F, A, R>> callsites = new HashMap<ASTNode<?>, Callsite<F, A, R>>();
	static final boolean DEBUG = false;
	private boolean isRecursive = false; // this may change during analysis
	private R currentRecursiveResult = null;

	/**
	 * constructs an InterproceduralAnalysisNode, registers the node with the
	 * analysis engine, runs the analysis
	 * 
	 * @param tree
	 *            the function for this node
	 * @param analysis
	 *            the analysis for the function
	 * @param callString
	 *            the call string of this node's call (must include the call to
	 *            the function this analysises)
	 */
	protected InterproceduralAnalysisNode(
			InterproceduralAnalysis<F, A, R> interprocAnalysis,
			FunctionCollection callgraph,
			InterproceduralAnalysisFactory<F, A, R> analysisFactory,
			FunctionReference ref, CallString<A> callString, A argument) {
		if (DEBUG)
			System.out.println("new intra proc anal node  " + ref + "("
					+ argument + ") - " + callString);

		// initialize/assign data
		this.function = callgraph.get(ref);
		this.callString = callString;
		this.interprocAnalysis = interprocAnalysis;
		this.argument = argument;
		this.factory = analysisFactory;
		this.callgraph = callgraph;

		// register with analysis
		this.interprocAnalysis.putNode(ref, argument, this);

		// create analysis
		functionAnalysis = analysisFactory.newFunctionAnalysis(function,
				argument, this);

		// run analysis
		functionAnalysis.analyze();
		
		// if it was found that this is a recursive call, do fixed point
		// iteration
		if (isRecursive) {
			if (DEBUG)
				System.out.println("fixed point itreation on " + this
						+ "\n first result" + functionAnalysis.getResult());

			// fixed point is reached when the current result equals the new
			// result
			while (!currentRecursiveResult.equals(functionAnalysis.getResult())) {
				currentRecursiveResult = functionAnalysis.getResult();
				functionAnalysis = analysisFactory.newFunctionAnalysis(
						function, argument, this);
				functionAnalysis.analyze();
				if (DEBUG)
					System.out.println(" newer result "
							+ currentRecursiveResult);
			}
			if (DEBUG)
				System.out.println(" finished iteration, got "
						+ currentRecursiveResult);
		}
	}

	/**
	 * the same as the other analyze function, except the call object gets
	 * created within the funciton call
	 */
	public R analyze(FunctionReference functionRef, A arguments,
			Callsite<F, A, R> callsite) {
		return analyze(new Call<A>(functionRef, arguments), callsite);
	}

	/**
	 * computes the Result for the given function, argument set, where the call
	 * is happening at the given callsite
	 * 
	 * If the given function/argument combination has already been analyzed,
	 * then it will not be again analyzed, but rather the previously computed
	 * result will be returned.
	 * 
	 * If the given function/argument combination is currently being processed
	 * (i.e. it's in the current call string), then this is a recursive call,
	 * then this will return the default result of the function analysis created
	 * by the function analysis factory
	 * 
	 * This should only be called by Function Analyses, during the analysis
	 * phase.
	 */
	public R analyze(Call<A> call, Callsite<F, A, R> callsite) {
		System.out.println("analyze " + call);
		A arg = call.getArguments();
		FunctionReference function = call.getFuncionReference();
		R result = null;
		InterproceduralAnalysisNode<F, A, R> node = null;

		// check whether this is a recursive call
		if (callString.contains(function, arg)) {
			node = interprocAnalysis.getNode(function, arg);
			if (DEBUG)
				System.out.println("found recursive call " + callString
						+ " -- " + node);
			if (node.currentRecursiveResult == null) {
				node.currentRecursiveResult = node.getAnalysis()
						.getDefaultResult();
				if (DEBUG)
					System.out.println("created default result "
							+ node.currentRecursiveResult);
			}
			// set the node to be recursianalyzeve
			node.isRecursive = true;
			callsite.addCall(call, node);
			return node.currentRecursiveResult;
		} else {
			// not a recursive call - try to find the result in the
			// interprocedural analysis
			if (DEBUG)
				System.out.println("try to find node " + function.name + "("
						+ arg + ")");
			node = interprocAnalysis.getNode(function, arg);
			if (node == null) {
				if (DEBUG)
					System.out.println("creating new node " + function.name
							+ "(" + arg + ")");
				// create new interpocedural analysis
				node = new InterproceduralAnalysisNode<F, A, R>(
						interprocAnalysis, callgraph, factory, function,
						callString.add(function, arg, callsite.getASTNode()),
						arg);
			} else {
				if (DEBUG)
					System.out.println("found existing node "
							+ node.function.getName() + "(" + node.argument
							+ ")");
			}
			result = node.getResult();
		}

		// register call site - will overwrite old, invalidated value
		System.out.println("add to callsite " + call);
		callsite.addCall(call, node);
		return result;
	}

	/**
	 * sets/overrides the callsite object for the given ast node and returns it.
	 * For every pass of the analysis, this should be called to get/set a new
	 * callsite object
	 */
	public Callsite<F, A, R> createCallsiteObject(ASTNode<?> astNode) {
		Callsite<F, A, R> callsite = new Callsite<F, A, R>(this, astNode);
		callsites.put(astNode, callsite);
		return callsite;
	}

	/**
	 * returns the call site object associated with the ast node
	 */
	public Callsite<F, A, R> getCallsite(ASTNode<?> node) {
		return callsites.get(node);
	}

	/**
	 * returns all call sites
	 */
	public Map<ASTNode<?>, Callsite<F, A, R>> getAllCallsites() {
		return Collections.unmodifiableMap(callsites);
	}

	public StaticFunction getFunction() {
		return function;
	}

	public F getAnalysis() {
		return functionAnalysis;
	}

	public CallString<A> getCallString() {
		return callString;
	}

	public Call<A> getCall() {
		return callString.getTopCall();
	}

	/**
	 * returns the result of the function analysis.
	 */
	public R getResult() {
		return functionAnalysis.getResult();
	}

	@Override
	public String toString() {
		return "AnalysisNode: " + function.getName() + "(" + argument
				+ ")  --> " + getResult();
	}

	/**
	 * prints the complete analysis node with code and flowsets
	 */
	public String getPrettyPrinted() {
		return "AnalysisNode: "
				+ function.getName()
				+ "("
				+ argument
				+ "):\n"
				+ function.getAst().getAnalysisPrettyPrinted(
						(StructuralAnalysis<?>) functionAnalysis, true, true)
				+ "\nresult: " + getResult();
	}

	/**
	 * returns the interprocedural analysis that this node is part of
	 */
	public InterproceduralAnalysis<F, A, R> getInterproceduralAnalysis() {
		return this.interprocAnalysis;
	}
}
