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
			isComplexInfoPropMatch previousMatchResult, List<? extends Value<?>> argValues) {
		
		isComplexInfoPropMatch matchCase = ivl.match(isPatternSide, previousMatchResult, argValues);
		
		if (matchCase.getNumMatched() == argValues.size() && false == matchCase.getError())  // input args have been matched
		{	
			matchCase.setMatchIsDone();
			// System.out.println("input args matched...matching output args now");
			isPatternSide = false;
			isComplexInfoPropMatch outputMatch = ovl.match(isPatternSide, matchCase, argValues);
			outputMatch.setOutputIsDone();
			//
			// System.out.println("number of A args = "+outputMatch.getNumAargs());
			return outputMatch;
		}
		else
		{
			// System.out.println("Cannot match the arguments to equation!!! ");
			return previousMatchResult;
		}
		
	}

}
