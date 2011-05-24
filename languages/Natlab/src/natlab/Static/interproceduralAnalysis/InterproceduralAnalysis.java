package natlab.Static.interproceduralAnalysis;

import java.util.HashMap;
import java.util.HashSet;

import annotations.ast.ASTNode;
import natlab.Static.callgraph.*;
import natlab.toolkits.path.FunctionReference;

/**
 * This is the engine that can run interprocedural analyses.
 * This will take a factory that can construct FunctionAnalysis objects,
 * and a call graph, and run the inter procedural analysis.
 * 
 * Mostly just a cache of analyses
 * 
 * @author ant6n
 *
 * @param <Analysis>
 * @param <Arg>
 * @param <Res>
 */

public class InterproceduralAnalysis<Analysis extends FunctionAnalysis<Arg,Res>,Arg,Res> {
    private FunctionCollection callgraph;
    private InterproceduralAnalysisFactory<Analysis, Arg, Res> factory;
    private Arg mainArgs;
    
    public InterproceduralAnalysis(
            InterproceduralAnalysisFactory<Analysis, Arg, Res> factory,
            FunctionCollection callgraph,
            Arg mainArgs) {
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
        new InterproceduralAnalysisNode<Analysis, Arg, Res>(
                this, callgraph, factory, callgraph.getMain(), 
                new CallString<Arg>(callgraph.getMain(),mainArgs), mainArgs);
    }
    
    /**
     * returns the node for the main
     */
    public InterproceduralAnalysisNode<Analysis, Arg, Res> getMainNode(){
        return getNode(callgraph.getMain(),mainArgs);
    }
    
    
    // TODO - should this be weak or something?
    // should it be a hashmap of hashmaps??
    HashMap<Key,InterproceduralAnalysisNode<Analysis, Arg, Res>> nodes =
        new HashMap<Key, InterproceduralAnalysisNode<Analysis,Arg,Res>>();
    /**
     * returns the node associated with the given function reference/argument pair.
     * If there is none, returns null.
     */
    public InterproceduralAnalysisNode<Analysis, Arg, Res> getNode(
            FunctionReference ref,Arg arg){
        return nodes.get(new Key(ref,arg));
    }
    
    /**
     * puts the node into the analysis
     * TODO - add more info
     */
    public void putNode(FunctionReference ref, Arg arg, InterproceduralAnalysisNode<Analysis, Arg, Res> node){
        nodes.put(new Key(ref,arg), node);
    }
    
    class Key{
        FunctionReference ref;
        Arg arg;
        private Key(FunctionReference ref,Arg arg){
            this.ref = ref;
            this.arg = arg;
        }
        public int hashCode() {
            return ref.hashCode() + 4783973*(arg == null?0:arg.hashCode());
        }
        @SuppressWarnings("unchecked")
        public boolean equals(Object obj) {
            if (obj instanceof InterproceduralAnalysis.Key) {
                InterproceduralAnalysis.Key key = (InterproceduralAnalysis.Key) obj;
                return key.ref.equals(ref)
                && (arg==null || key.arg==null)?(arg==key.arg):key.arg.equals(arg);
            }
            return false;
        }
    }
}



