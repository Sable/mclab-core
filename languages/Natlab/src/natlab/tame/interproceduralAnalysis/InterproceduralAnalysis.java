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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import natlab.tame.callgraph.FunctionCollection;
import natlab.toolkits.path.FunctionReference;

/**
 * This is the engine that can run interprocedural analyses. This will take a
 * factory that can construct FunctionAnalysis objects, and a call graph, and
 * run the inter procedural analysis.
 * 
 * TODO - add support for multiple entry points
 * 
 * @author ant6n
 * 
 * @param <F>
 *            the FunctionAnalysis type used to analyse each function/argument
 *            pair
 * @param <A>
 *            the argument set that is given to the function to run the analysis
 * @param <R>
 *            the result set that the analysis returns for that function
 */

public class InterproceduralAnalysis<F extends FunctionAnalysis<A, R>, A, R> {
	private FunctionCollection callgraph;
	private InterproceduralAnalysisFactory<F, A, R> factory;
	private A mainArgs;
	// TODO - should this be weak or something?
	// should it be a hashmap of hashmaps?? -- or clear after analyze?
	Map<Call<A>, InterproceduralAnalysisNode<F, A, R>> nodes = new HashMap<Call<A>, InterproceduralAnalysisNode<F, A, R>>();

	// XU change Anton's WeakHashMap to HashMap

	public InterproceduralAnalysis(
			InterproceduralAnalysisFactory<F, A, R> factory,
			FunctionCollection callgraph, A mainArgs) {
		this.callgraph = callgraph;
		this.factory = factory;
		this.mainArgs = mainArgs;
		analyze();

	}

	/**
	 * factory method
	 */
	public static <Analysis extends FunctionAnalysis<Arg, Res>, Arg, Res> InterproceduralAnalysis<Analysis, Arg, Res> create(
			InterproceduralAnalysisFactory<Analysis, Arg, Res> factory,
			FunctionCollection callgraph, Arg mainArgs) {
		return new InterproceduralAnalysis<Analysis, Arg, Res>(factory,
				callgraph, mainArgs);
	}

	/**
	 * run the analysis
	 */
	private void analyze() {
		new InterproceduralAnalysisNode<F, A, R>(this, callgraph, factory,
				callgraph.getMain(), new CallString<A>(callgraph.getMain(),
						mainArgs), mainArgs);
	}

	/**
	 * returns the node for the main
	 */
	public InterproceduralAnalysisNode<F, A, R> getMainNode() {
		return getNode(callgraph.getMain(), mainArgs);
	}

	/**
	 * returns the node associated with the given function reference/argument
	 * pair. If there is none, returns null.
	 */
	public InterproceduralAnalysisNode<F, A, R> getNode(FunctionReference ref,
			A arg) {
		return nodes.get(new Call<A>(ref, arg));
	}

	/**
	 * returns all nodes in this list
	 */
	public java.util.List<InterproceduralAnalysisNode<F, A, R>> getNodeList() {
		return new ArrayList<InterproceduralAnalysisNode<F, A, R>>(
				nodes.values());
	}

	/**
	 * puts the node into the analysis
	 */
	protected void putNode(FunctionReference ref, A arg,
			InterproceduralAnalysisNode<F, A, R> node) {
		nodes.put(new Call<A>(ref, arg), node);
	}

	public String getPrettyPrinted() {
		String s = "";
		for (InterproceduralAnalysisNode<F, A, R> node : nodes.values()) {
			s += node.getPrettyPrinted() + "\n\n";
		}
		return s;
	}

	/**
	 * return the function collection that this interprocedural analysis is
	 * built upon.
	 */
	public FunctionCollection getFunctionCollection() {
		return callgraph;
	}

	@Override
	public String toString() {
		String s = "InterproceduralAnalysis:"
				+ getMainNode().getFunction().getName();
		for (InterproceduralAnalysisNode<?, ?, ?> node : nodes.values()) {
			s += "\n  " + node;
		}
		return s;
	}

}
