package natlab.tame.tir;

import java.util.LinkedList;

import natlab.tame.tir.analysis.TIRNodeCaseHandler;
import ast.Expr;
import ast.LambdaExpr;
import ast.List;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;

public class TIRCreateLambdaStmt extends TIRAbstractCreateFunctionHandleStmt {

    /**
     * creates an assignment of the form
     * lhs = \@(params),function(vars)
     */
    public TIRCreateLambdaStmt(Name lhs,Name function,List<Name> params,TIRCommaSeparatedList vars){
        super(lhs); 
        if (!vars.isAllNameExpressions()) throw new UnsupportedOperationException("function call in lambda must use only names");
        //TODO - check whether the lambda params match the called vars
        setRHS(new LambdaExpr(
                params,
                new ParameterizedExpr(new NameExpr(function),vars)));
    }

	
    /**
     * returns the Name of the function on the rhs
     * @return
     */
    public Name getFunctionName(){
    	return ((NameExpr)((ParameterizedExpr)((LambdaExpr)getRHS()).getBody()).getTarget()).getName();
    }

    
    /**
     * returns the lamdba parameters as a list of names - will return an empty list
     * if this is not lambda
     */
    public List<Name> getLambdaParams(){
    	return ((LambdaExpr)getRHS()).getInputParamList();
    }
    

    /**
     * returns a list of the variables that are enclosed by this function handle creation
     * (i.e. curried values), i.e. for
     * f = \@(x1,x2,...) function(a1,a2,...,x1,x2,...)
     * it will return [a1,a2,...]
     */
    public java.util.List<Name> getEnclosedVars(){
    	LinkedList<Name> result = new LinkedList<Name>();
    	List<Name> params = getLambdaParams();
    	List<Expr> args = ((ParameterizedExpr)((LambdaExpr)getRHS()).getBody()).getArgs();
    	for (int i = 0; i < (args.getNumChild()-params.getNumChild()); i++){
    		result.add(((NameExpr)args.getChild(i)).getName());
    	}
    	return result;
    }
    
    //TODO - get the other variables

    
    @Override
    public void tirAnalyze(TIRNodeCaseHandler irHandler) {
    	irHandler.caseTIRCreateLambdaStmt(this);
    }
}



