package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Value;

public class SPNumber extends SPAbstractScalarExpr
{
	Number n;
	public SPNumber (Number n)
	{
		this.n = n;
		//System.out.println(n.toString());
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues)
	{
		if(isPatternSide==true){
			HashMap<String, Integer> assignment = new HashMap<String, Integer>();
			assignment.put(previousMatchResult.getLatestMatchedLowercase(), n.intValue());
			ShapePropMatch newMatch = new ShapePropMatch(previousMatchResult, assignment, null);
			return newMatch;
		}
		else{
			return null;
		}
		
	}
	
	public String toString()
	{
		return n.toString();
	}
}
