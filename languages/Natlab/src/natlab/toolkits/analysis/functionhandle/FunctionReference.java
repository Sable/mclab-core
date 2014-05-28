package natlab.toolkits.analysis.functionhandle;

import ast.FunctionHandleExpr;
import ast.LambdaExpr;

/**
 * a reference to a function
 * could be a function handle or a lambda expression
 */


public class FunctionReference {
	boolean isHandle=false;
	boolean isLambda=false;
	LambdaExpr lambda;
	FunctionHandleExpr handle;
	
	public FunctionReference(LambdaExpr lambda){
		isLambda=true;
		this.lambda=lambda;
	}
	
	public FunctionReference(FunctionHandleExpr handle){
		isHandle=true;
		this.handle=handle;
	}

	public String toString(){
		if (isHandle) return handle.getPrettyPrinted();
		if (isLambda) return lambda.getPrettyPrinted();		
		return "unknown function reference";
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj instanceof FunctionReference){
			FunctionReference fn = (FunctionReference)obj;
			return isHandle==fn.isHandle && isLambda==fn.isLambda
				&& lambda==fn.lambda && handle==fn.handle;
		} else return false;		
	}
	
}
