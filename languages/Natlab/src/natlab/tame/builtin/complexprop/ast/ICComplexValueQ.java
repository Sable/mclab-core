package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class ICComplexValueQ extends ICAbstractValue{
	
	ICAbstractValue xv;
	ICQOp qop;
	public ICComplexValueQ(ICAbstractValue xv, ICQOp qop)
	{
		this.xv = xv;
		this.qop = qop;
	}

	
	public String toString()
	{
		return xv.toString()+qop.toString();
	}


	@Override
	public isComplexInfoPropMatch match(boolean isPatternSide,
			isComplexInfoPropMatch previousMatchResult, List<Integer> argValues) {
		
		if (qop.toString().equals("*"))
		{
	        String typeOfEquationValue = xv.toString();// save the type of xv i.e X,R or A
	         isComplexInfoPropMatch lastMatch = xv.match(isPatternSide, previousMatchResult, argValues); //match returns same as previousmatch instead of null
			while (!lastMatch.equals(previousMatchResult) && lastMatch.getLastMatchICType().equals(typeOfEquationValue)) 
			{
				lastMatch = xv.match(isPatternSide, previousMatchResult, argValues);
			}
			
			return lastMatch ;
			
		}
		else if (qop.toString().equals("+"))
		{
			isComplexInfoPropMatch firstMatch = xv.match(isPatternSide, previousMatchResult, argValues); 
			String typeOfEquationValue = xv.toString();
			isComplexInfoPropMatch lastMatch = xv.match(isPatternSide, firstMatch, argValues); //match returns same as previousmatch instead of null
			
			while (!lastMatch.equals(previousMatchResult) && lastMatch.getLastMatchICType().equals(typeOfEquationValue))  //check once
			{
				lastMatch = xv.match(isPatternSide, firstMatch, argValues);
			}
			return lastMatch;
			
		}
		else if (qop.toString().equals("?"))
		{
			//TODO - do something
		}
		//return match;
		return previousMatchResult; //TODO - make sure of this
		
	}
}
