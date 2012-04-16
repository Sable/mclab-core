package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPStringLiteral extends SPAbstractVertcatExprArg
{
	String id;
	public SPStringLiteral(String id)
	{
		this.id = id;
		//System.out.println(id);
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, ArrayList<Integer> argValues)
	{
		return previousMatchResult;
	}
	
	public String toString()
	{
		return "'"+id.toString()+"'";
	}
}
