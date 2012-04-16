package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPPlusFunCall extends SPAbstractVertcatExprArg
{
	String l;
	SPAbstractPattern f;
	public SPPlusFunCall(String l, SPAbstractPattern f)
	{
		this.l = l;
		this.f = f;
		//System.out.println("a plus of lowercase and fnCall");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, ArrayList<Integer> argValues)
	{
		return previousMatchResult;
	}
	
	public String toString()
	{
		return l.toString()+"+"+f.toString();
	}
}
