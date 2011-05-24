package natlab.Static.interproceduralAnalysis.callStringAnalysis;

import annotations.ast.ASTNode;
import ast.*;
import natlab.Static.callgraph.StaticFunction;
import natlab.Static.interproceduralAnalysis.*;
import natlab.toolkits.analysis.*;
import natlab.toolkits.path.FunctionReference;

public class CallStringAnalysis extends AbstractPreorderAnalysis<HashSetFlowSet<CallString<?>>>
        implements FunctionAnalysis<HashSetFlowSet<CallString<?>>, CallString<?>, HashSetFlowSet<CallString<?>>>{

    /**
     * factory for this object
     */
    public static class Factory implements InterproceduralAnalysisFactory
    <CallStringAnalysis, CallString<?>, HashSetFlowSet<CallString<?>>>{
        @Override
        public CallStringAnalysis newFunctionAnalysis(
                StaticFunction function,
                CallString<?> argumentSet,
                InterproceduralAnalysisNode<CallStringAnalysis, CallString<?>, HashSetFlowSet<CallString<?>>> node) {
            return new CallStringAnalysis(function,node);
        }
    }

    private CallString<?> callstring; //current context
    private StaticFunction function; //need static function to resolve names
    private HashSetFlowSet<CallString<?>> result; //we only flow one set
    private static final Object arg = new Object(); //argument dummy
    private InterproceduralAnalysisNode<CallStringAnalysis, CallString<?>, HashSetFlowSet<CallString<?>>> node;
    private CallStringAnalysis(StaticFunction function,
            InterproceduralAnalysisNode<CallStringAnalysis, CallString<?>, HashSetFlowSet<CallString<?>>> node) {
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
                    result.union(this.node.analyze(ref, callstring, null));
                }
            }
        }
    }
    
    
    @Override
    public void caseCondition(Expr condExpr) {
        caseASTNode( condExpr );
    }

    @Override
    public HashSetFlowSet<CallString<?>> newInitialFlow() {
        return new HashSetFlowSet<CallString<?>>();
    }

    @Override
    public HashSetFlowSet<CallString<?>> getDefaultResult() {
        return newInitialFlow();
    }

    public ast.Function getTree() {
        return (Function)super.getTree();
    };
    
    @Override
    public HashSetFlowSet<CallString<?>> getResult() {
        return result;
    }
    
}
