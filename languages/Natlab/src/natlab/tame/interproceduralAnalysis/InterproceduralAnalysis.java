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

import java.util.LinkedHashMap;

import natlab.tame.callgraph.FunctionCollection;
import natlab.toolkits.path.FunctionReference;

/**
 * This is the engine that can run interprocedural analyses.
 * This will take a factory that can construct FunctionAnalysis objects,
 * and a call graph, and run the inter procedural analysis.
 * 
 * Mostly just a cache of analyses
 * 
 * TODO - add support for multiple entry points
 * 
 * @author ant6n
 *
 * @param <F> the FunctionAnalysis type used to analyse each function/argument pair
 * @param <A>  the argument set that is given to the function to run the analysis
 * @param <R> the result set that the analysis returns for that function
 */

public class InterproceduralAnalysis<F extends FunctionAnalysis<A,R>,A,R> {
    private FunctionCollection callgraph;
    private InterproceduralAnalysisFactory<F, A, R> factory;
    private A mainArgs;
    // TODO - should this be weak or something?
    // should it be a hashmap of hashmaps?? -- or clear after analyze?
    //see 'Key' below - TODO turn into Call<A>
    LinkedHashMap<Key,InterproceduralAnalysisNode<F, A, R>> nodes =
        new LinkedHashMap<Key, InterproceduralAnalysisNode<F,A,R>>();
    
    
    public InterproceduralAnalysis(
            InterproceduralAnalysisFactory<F, A, R> factory,
            FunctionCollection callgraph,
            A mainArgs) {
        this.callgraph = callgraph;
        this.factory = factory;
        this.mainArgs = mainArgs;
        analyze();
    }
    
    
    /**
     * factory method
     */
    public static <Analysis extends FunctionAnalysis<Arg,Res>,Arg,Res>
    InterproceduralAnalysis<Analysis,Arg,Res> create(
            InterproceduralAnalysisFactory<Analysis, Arg, Res> factory,
            FunctionCollection callgraph,
            Arg mainArgs) {
        return new InterproceduralAnalysis<Analysis,Arg,Res>(factory,callgraph,mainArgs);
    }
    
    
    /**
     * run the analysis
     */
    private void analyze(){
        new InterproceduralAnalysisNode<F, A, R>(
                this, callgraph, factory, callgraph.getMain(), 
                new CallString<A>(callgraph.getMain(),mainArgs), mainArgs);
    }
    
    /**
     * returns the node for the main
     * TODO - this should be entry points
     */
    public InterproceduralAnalysisNode<F, A, R> getMainNode(){
        return getNode(callgraph.getMain(),mainArgs);
    }
    
    
    /**
     * returns the node associated with the given function reference/argument pair.
     * If there is none, returns null.
     */
    public InterproceduralAnalysisNode<F, A, R> getNode(
            FunctionReference ref,A arg){
        return nodes.get(new Key(ref,arg));
    }
    
    /**
     * puts the node into the analysis
     * TODO - add more info
     */
    public void putNode(FunctionReference ref, A arg, InterproceduralAnalysisNode<F, A, R> node){
        nodes.put(new Key(ref,arg), node);
    }
    
    class Key{
        FunctionReference ref;
        A arg;
        private Key(FunctionReference ref,A arg){
            this.ref = ref;
            this.arg = arg;
        }
        public int hashCode() {
            return 0;
            //System.out.println((ref.hashCode() + 4783973*(arg == null?0:arg.hashCode()))+" - "+getPrettyPrinted());
            //return ref.hashCode() + 4783973*(arg == null?0:arg.hashCode());
        }
        @SuppressWarnings("unchecked")
        public boolean equals(Object obj) {
            if (obj instanceof InterproceduralAnalysis.Key) {
                InterproceduralAnalysis.Key key = (InterproceduralAnalysis.Key) obj;
                return key.ref.equals(ref) &&
                ((arg==null || key.arg==null)?(arg==key.arg):key.arg.equals(arg));
            }
            return false;
        }
        
        @Override
        public String toString() {
            return ref.getname()+"("+arg+")";
        }
    }
    
    
    public String getPrettyPrinted(){
        String s = "";
        for (InterproceduralAnalysisNode<F, A, R> node : nodes.values()){
            s += node.getPrettyPrinted()+"\n\n";
        }
        return s;
    }
    
    @Override
    public String toString() {
        String s = "InterproceduralAnalysis:"+getMainNode().getFunction().getName();
        for (InterproceduralAnalysisNode<?,?,?> node : nodes.values()){
            s += "\n  "+node;
        }
        return s;
    }

}



