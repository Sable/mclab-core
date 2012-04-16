package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Value;

public class SPLowercase extends SPAbstractScalarExpr
{
	String s;
	public SPLowercase(String s)
	{
		this.s = s;
		//System.out.println(s);
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, ArrayList<Integer> argValues)
	{
		if(isPatternSide==true){
			HashMap<String, Integer> valueDollar = new HashMap<String, Integer>();
			valueDollar.put(s, null);
			ShapePropMatch match = new ShapePropMatch(previousMatchResult, valueDollar, null);
			match.saveLatestMatchedLowerCase(s);
			//System.out.println(match.getAllLowerCase());
			return match;
		}
		else{
			previousMatchResult.addToVertcatExpr(previousMatchResult.getValueOfVariable(s));
			return previousMatchResult;
		}
		
	}
	
	public String toString()
	{
		return s.toString();
	}
}
