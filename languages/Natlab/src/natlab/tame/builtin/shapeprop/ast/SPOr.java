package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPOr extends SPAbstractMatchExpr
{
	SPAbstractMatchExpr first;
	SPAbstractMatchExpr next;
	
	public SPOr (SPAbstractMatchExpr first,SPAbstractMatchExpr next)
	{
		this.first = first;
		this.next = next;
		//System.out.println("|");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValuess)
	{
		return previousMatchResult;
	}
	
	public String toString()
	{
		return first.toString()+"|"+next.toString();
	}
}
