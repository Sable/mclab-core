package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
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
			isComplexInfoPropMatch previousMatchResult, List<? extends Value<?>> argValues) {
		
		if (qop.toString().equals("*"))
		{
	        //String typeOfEquationValue = xv.toString();// save the type of xv i.e X,R or A
			 int localMatchCtr =0;
	         isComplexInfoPropMatch lastMatch = new isComplexInfoPropMatch(previousMatchResult); //match returns same as previousmatch instead of null
	         while (lastMatch.getNumMatched()<argValues.size() && (true == lastMatch.getLastMatchSucceed() || localMatchCtr==0))
				{
					lastMatch = xv.match(isPatternSide, lastMatch, argValues);
					localMatchCtr++;
				}
				return lastMatch;
			
		}
		else if (qop.toString().equals("+"))
		{
			isComplexInfoPropMatch lastMatch = xv.match(isPatternSide, previousMatchResult, argValues); 
			
			// System.out.println(lastMatch.getNumMatched()+"~"+lastMatch.getLastMatchICType()+"\n");
		
			while (lastMatch.getNumMatched()<argValues.size() && true == lastMatch.getLastMatchSucceed())
			{
				lastMatch = xv.match(isPatternSide, lastMatch, argValues);
			}
			return lastMatch;
			
		}
		else if (qop.toString().equals("?"))
		{
			isComplexInfoPropMatch lastMatch = xv.match(isPatternSide, previousMatchResult, argValues);
			if(true == lastMatch.getError())
			{
				lastMatch = previousMatchResult;
			}
			return lastMatch;
		}
	//	return lastMatch;
		return previousMatchResult; //TODO - make sure of this
		
	}
}
