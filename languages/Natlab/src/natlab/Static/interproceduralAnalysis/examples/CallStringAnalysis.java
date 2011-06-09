package natlab.Static.interproceduralAnalysis.examples;

import java.util.LinkedHashSet;

import ast.*;
import natlab.Static.callgraph.StaticFunction;
import natlab.Static.interproceduralAnalysis.*;
import natlab.toolkits.analysis.*;
import natlab.toolkits.path.FunctionReference;

public class CallStringAnalysis extends AbstractDepthFirstAnalysis<CallStringAnalysis.CallStrings>
        implements FunctionAnalysis<CallString<?>,CallStringAnalysis.CallStrings>{
    
    /**
     * the flowset
     */
    public static class CallStrings extends LinkedHashSet<CallString<?>>{
        private static final long serialVersionUID = 1L;
        
    }

    /**
     * factory for this object
     */
    public static class Factory implements InterproceduralAnalysisFactory
    <CallStringAnalysis, CallString<?>, CallStrings>{
        @Override
        public CallStringAnalysis newFunctionAnalysis(
                StaticFunction function,
                CallString<?> argumentSet,
                InterproceduralAnalysisNode<CallStringAnalysis, CallString<?>, CallStrings> node) {
            return new CallStringAnalysis(function,node);
        }        
    }

    private CallString<?> callstring; //current context
    private StaticFunction function; //need static function to resolve names
    private CallStrings result; //we only flow one set
    private static final Object arg = new Object(); //argument dummy
    private InterproceduralAnalysisNode<CallStringAnalysis, CallString<?>, CallStrings> node;
    private CallStringAnalysis(StaticFunction function,
            InterproceduralAnalysisNode<CallStringAnalysis, CallString<?>, CallStrings> node) {
        super(function.getAst());
        this.node = node;
        this.callstring = node.getCallString();
        this.function = function;
        this.result = newInitialFlow();
        result.add(callstring);
    }
    
    @Override
    public void caseName(Name node) {
        System.out.print("-"+node.getID()+" "+function);
        String name = node.getID();
        System.out.println(function.getCalledFunctions().keySet());
        if (function.getCalledFunctions().containsKey(name)){
            FunctionReference ref = function.getCalledFunctions().get(name);
            if (ref == null){
                System.err.println("unresolved call to "+name);
            } else {
                if (ref.isBuiltin()){
                    result.add(callstring.add(ref, null, null)); //TODO
                } else {
                    System.out.println(" call "+ref);
                    result.addAll(this.node.analyze(ref, callstring, null));
                }
            }
        }
    }
    
    
    @Override
    public void caseCondition(Expr condExpr) {
        caseASTNode( condExpr );
    }

    @Override
    public CallStrings newInitialFlow() {
        return new CallStrings();
    }

    @Override
    public CallStrings getDefaultResult() {
        return newInitialFlow();
    }

    public ast.Function getTree() {
        return (Function)super.getTree();
    };
    
    @Override
    public CallStrings getResult() {
        return result;
    }
    
}
