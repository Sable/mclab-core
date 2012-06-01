package natlab.tame.builtin.shapeprop.ast;

import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Value;

public class SPAssignStmt extends SPAbstractPattern{
	SPAbstractVertcatExprArg lhs;
	SPAbstractVertcatExprArg rhs;
	public SPAssignStmt(SPAbstractVertcatExprArg lhs, SPAbstractVertcatExprArg rhs){
		this.lhs = lhs;
		this.rhs = rhs;
		//System.out.println("=");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues, int num){
		previousMatchResult.setIsInsideAssign(true);
		ShapePropMatch match = lhs.match(isPatternSide, previousMatchResult, argValues, num);
		ShapePropMatch rhsMatch = rhs.match(isPatternSide, match, argValues, num);
		rhsMatch.setIsInsideAssign(false);
		return rhsMatch;
	}
	
	public String toString(){
		return lhs.toString()+"="+rhs.toString();
	}
}
