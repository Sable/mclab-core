package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPAssignStmt extends SPAbstractPattern
{
	SPAbstractVertcatExprArg lhs;
	SPAbstractVertcatExprArg rhs;
	public SPAssignStmt(SPAbstractVertcatExprArg lhs, SPAbstractVertcatExprArg rhs)
	{
		this.lhs = lhs;
		this.rhs = rhs;
		//System.out.println("=");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues)
	{
		//System.out.println("inside assignment");
		//System.out.println(previousMatchResult.getAllLowerCase());
		//System.out.println(previousMatchResult.getAllResults());
		ShapePropMatch match = lhs.match(isPatternSide, previousMatchResult, argValues);
		ShapePropMatch rhsMatch = rhs.match(isPatternSide, match, argValues);
		return rhsMatch;
	}
	
	public String toString()
	{
		return lhs.toString()+"="+rhs.toString();
	}
}
