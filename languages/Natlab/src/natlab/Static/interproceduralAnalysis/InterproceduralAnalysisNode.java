package natlab.Static.interproceduralAnalysis;

/**
 * TODO 
 * node in the interprocedural analysis 
 * - stores function, functionanalysis, callstring, argument, result
 * - takes care of a map of <callsite,interproc analysis node>
 * 
 * TODO - flag something as recursive
 * @param <F> the FunctionAnalysis type used to analyse each function/argument pair
 * @param <A>  the argument set that is given to the function to run the analysis
 * @param <R> the result set that the analysis returns for that function
 */


import java.util.*;
import natlab.Static.callgraph.*;
import natlab.toolkits.path.FunctionReference;
import annotations.ast.ASTNode;

public class InterproceduralAnalysisNode<FAnalysis extends FunctionAnalysis<Arg,Res>, Arg, Res> {

    private StaticFunction function;
    private FAnalysis functionAnalysis;
    private CallString<Arg> callString;
    private InterproceduralAnalysis<FAnalysis,Arg,Res> interprocAnalysis;
    private InterproceduralAnalysisFactory<FAnalysis,Arg,Res> factory;
    private Arg argument;
    private FunctionCollection callgraph;
    private HashMap<ASTNode<?>,InterproceduralAnalysisNode<FAnalysis, Arg, Res>> callsites = 
        new HashMap<ASTNode<?>, InterproceduralAnalysisNode<FAnalysis,Arg,Res>>();
    
    /**
     * constructs an InterproceduralAnalysisNode,
     * registers the node with the analysis engine,
     * runs the analysis 
     * 
     * @param tree the function for this node
     * @param analysis the analysis for the function
     * @param callString the call string of this node's call (must 
     *        include the call to the function this analysises)
     */
    protected InterproceduralAnalysisNode(
            InterproceduralAnalysis<FAnalysis,Arg,Res> interprocAnalysis,
            FunctionCollection callgraph,
            InterproceduralAnalysisFactory<FAnalysis, Arg, Res> analysisFactory,
            FunctionReference ref,
            CallString<Arg> callString,
            Arg argument){
        System.err.println("new node "+ref);
        
        //initialize/assign data
        this.function = callgraph.get(ref);
        this.callString = callString;
        this.interprocAnalysis = interprocAnalysis;
        this.argument = argument;
        this.factory = analysisFactory;
        this.callgraph = callgraph;
        
        //register with analysis
        this.interprocAnalysis.putNode(ref, argument, this);
        
        //create analysis
        functionAnalysis = analysisFactory.newFunctionAnalysis(function, argument, this);
        
        //run analysis
        functionAnalysis.analyze();
    }
    
    
    /**
     * computes the Result for the given function, argument set, where the
     * call is happening at the given callsite, using the given 
     * function analysis factory.
     * 
     * If the given function/argument combination has already been analyzed,
     * then it will not be again analyzed, but rather the previously computed
     * result will be returned.
     * 
     * If the given function/argument combination is currently being processed
     * (i.e. it's in the current call string), then this is a recursive call,
     * then this will return the default result of the function analysis 
     * created by the function analysis factory
     * 
     * This should only be called by Function Analyses, during the analysis
     * phase.
     * Note that this should work even if the supplied callsite is null (but
     * call strings will be incomplete).
     */
    public Res analyze(FunctionReference function,Arg arg,ASTNode callsite){
        Res result = null;
        InterproceduralAnalysisNode<FAnalysis, Arg, Res> node = null;
        
        //check whether this is a recursive call
        if (callString.contains(function, arg)){
            //TODO
            //find the recursive result - there has to be one already
            //TODO could there be a result already computed?
            // -- there should be, with what temporary results
            //tag the previous ref/arg node as recursive, to trigger fixed point compute
            throw new UnsupportedOperationException("encountered unsupported recusrive call");
        } else {
            //not a recursive call - try to find the result in the interprocedural analysis
            node =  interprocAnalysis.getNode(function, arg);
            if (node == null){
                //create new interpocedural analysis node and return the result
                node = new InterproceduralAnalysisNode<FAnalysis, Arg, Res>(
                        interprocAnalysis, callgraph, factory, function,
                        callString.add(function, arg, callsite), arg);
            }
            //TODO - make sure this makes sense
            result = node.getResult();
        }
        
        //register call site - will overwrite old, invalidated value
        setNodeForCallsite(callsite, node);
        return result;
    }

    /**
     * sets/overrides the callsite to call the given node
     */
    protected void setNodeForCallsite(ASTNode<?> callsite, 
            InterproceduralAnalysisNode<FAnalysis, Arg, Res> node){
        callsites.put(callsite, node);        
    }
    
    public StaticFunction getFunction(){
        return function;
    }
    public FunctionAnalysis<Arg, Res> getAnalysis(){
        return functionAnalysis;
    }
    public CallString<Arg> getCallString(){
        return callString;
    }
    /**
     * returns the result of the function analysis.
     */
    public Res getResult(){
        return functionAnalysis.getResult();
    }
    
    
    
}


