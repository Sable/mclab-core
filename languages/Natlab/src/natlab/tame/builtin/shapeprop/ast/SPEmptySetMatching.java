package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPEmptySetMatching extends SPAbstractVectorExpr
{
	public SPEmptySetMatching()
	{
		//System.out.println("an empty vector matching");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, ArrayList<Integer> argValues)
	{
		//System.out.println(argValues.get(previousMatchResult.getNumMatched()));
		ShapePropMatch match = new ShapePropMatch(previousMatchResult);
		if(isPatternSide==true){
			if(argValues.get(match.getNumMatched())==null){
				match.comsumeArg();
				System.out.println("matching an empty arg!");
				return match;
				}
			else{
				System.out.println("inside not empty");
				return null;
				}
			}
		else{
			System.err.println("the output shape is []");
			return previousMatchResult;
		}
	}
	
	public String toString()
	{
		return "[]";
	}
}
