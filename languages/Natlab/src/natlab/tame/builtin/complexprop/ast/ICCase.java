package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;


public class ICCase extends ICNode{
	
	ICinputValueList ivl;
	ICoutputValueList ovl;
	
	public ICCase (ICinputValueList ivl, ICoutputValueList ovl)
	{
		this.ivl = ivl;
		this.ovl = ovl;
		
	}
	
	public String toString()
	{
		return ivl.toString()+"->"+ovl.toString();
	}

	@Override
	public isComplexInfoPropMatch match(boolean isPatternSide,
			isComplexInfoPropMatch previousMatchResult, List<Integer> argValues) {
		
		isComplexInfoPropMatch match = ivl.match(isPatternSide, previousMatchResult, argValues);
		
		if (match.getNumMatched() == argValues.size())  // input args have been matched
		{
			System.out.println("input args matched...matching output args now");
			isPatternSide = false;
			isComplexInfoPropMatch outputMatch = ovl.match(isPatternSide, previousMatchResult, argValues);
			return outputMatch;
		}
		else
			
		return null;
	}

}
