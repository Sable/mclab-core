package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPUpperIndex extends SPAbstractVertcatExprArg
{
	String s;
	SPAbstractScalarExpr n;
	public SPUpperIndex(String s, SPAbstractScalarExpr n)
	{
		this.s = s;
		this.n = n;
		//System.out.println(s+"()");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues)
	{
		return previousMatchResult;
	}
	
	public String toString()
	{
		return s.toString()+"("+n.toString()+")";
	}
}
