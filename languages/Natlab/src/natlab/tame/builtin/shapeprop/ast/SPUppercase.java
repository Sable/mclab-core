package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Value;

public class SPUppercase extends SPAbstractVectorExpr
{
	String s;
	public SPUppercase (String s)
	{
		this.s = s;
		//System.out.println(s);
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, ArrayList<Integer> argValues)
	{
		return previousMatchResult;
	}
	
	public String toString()
	{
		return s.toString();
	}
}
