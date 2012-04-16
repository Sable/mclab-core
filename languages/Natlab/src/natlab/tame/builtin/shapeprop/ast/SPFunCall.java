package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPFunCall extends SPAbstractVertcatExprArg
{
	String i;
	SPAbstractVertcatExprArg ls;
	public SPFunCall(String i, SPAbstractVertcatExprArg ls)
	{
		this.i = i;
		this.ls = ls;
		//System.out.println("functionCall:"+i);
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, ArrayList<Integer> argValues)
	{
		//System.out.println("function: "+i);
		if(i.indexOf("previousScalar")==0){
			//System.out.println("inside previousScalar");
			HashMap<String, Integer> value = new HashMap<String, Integer>();
			value.put(previousMatchResult.getLatestMatchedLowerCase(), previousMatchResult.getValueOfVariable(previousMatchResult.getLatestMatchedUpperCase()));
			ShapePropMatch matchResult = new ShapePropMatch(previousMatchResult, value, null);
			//System.out.println(matchResult.getAllLowerCase());
            return matchResult;
		}
		return null;
	}
	
	public String toString()
	{
		return i.toString()+"("+(ls==null?"":ls.toString())+")";
	}
}
