package natlab.toolkits.rewrite.inline;

import ast.ASTNode;
import ast.Expr;
import ast.LValueExpr;
import ast.List;
import ast.Stmt;

/**
 * The Inliner.QueryObject describes how one Program
 * is about to be inlined within another, at one specific
 * point.
 * 
 * This can be used by the Inline Query Object 
 */
public class InlineInfo<InlinedScriptOrFunction extends ASTNode,TargetScriptOrFunction extends ASTNode>{
    private InlinedScriptOrFunction inlinedProgram;
    private TargetScriptOrFunction target;
    private Stmt callStatement;
    private List<Expr> parameters;
    private List<LValueExpr> targets;
    boolean isParametric;
    
    /**
     * constructor: note - the inlinedFuctnion should be a copy
     */
    protected InlineInfo(InlinedScriptOrFunction inlinedProgram,TargetScriptOrFunction target,
            Stmt callStatement, List<Expr> parameters, List<LValueExpr> targets,
            boolean isParametric){
        this.inlinedProgram = inlinedProgram;
        this.target = target;
        this.callStatement = callStatement;
        this.parameters = parameters;
        this.targets = targets;
    }
    
    /**
     * returns the function that is being inlined
     * This is a unique copy that can be modified
     */
    public InlinedScriptOrFunction getInlinedScriptOrFunction(){
        return inlinedProgram;
    }
    /**
     * replaces the function or script that is being inlined
     * @param function
     */
    public void setInlinedProgram(InlinedScriptOrFunction program){
        this.inlinedProgram = program;
    }
    /**
     * returns the function or Script in which the code is inliend
     */
    public TargetScriptOrFunction getTarget(){ return target; }
    /**
     * returns the statement of the call where the function is
     * about to get inlined
     */
    public Stmt getCallStatement(){ return callStatement; }
    /**
     * returns the list of expressions that the function is being called with
     */
    public List<Expr> getParameters(){ return parameters; }
    /**
     * returns the lvalues that the result of the function is being assigned to
     */
    public List<LValueExpr> getTargets(){ return targets; }
    /**
     * returns whether there are parenthesis around the call
     * all calls that have one or more arguments are parametric,
     * but if the call has zero arguments it may or may not be parametric,
     * i.e. foo vs foo()
     */
    public boolean isParametric(){ return isParametric; }
}

