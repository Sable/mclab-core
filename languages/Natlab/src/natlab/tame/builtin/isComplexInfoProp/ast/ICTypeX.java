package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.ArgICType;
import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.valueanalysis.value.Value;

public class ICTypeX extends ICType{
	
	
	public ICTypeX()
	{
		
	}

	
	public String toString()
	{
		return "COMPLEX";
	}


	@Override
	public isComplexInfoPropMatch match(boolean isPatternSide,
			isComplexInfoPropMatch previousMatchResult, List<? extends Value<?>> argValues) {
		// TODO Auto-generated method stub
		if(true==isPatternSide)//on the symbol on the LHS
		{
			
			isComplexInfoPropMatch match = new isComplexInfoPropMatch(previousMatchResult);

			
			if(argValues.size() > previousMatchResult.getNumMatched())
			{
				Value<?> argument = argValues.get(previousMatchResult.getNumMatched());// get the value of argument
		
				 int isArgComplex =  (new ArgICType()).getArgICType(argument);
				 // System.out.println("argument is #"+argument+"\n");
				
				 
			
				if (-1 == isArgComplex) //i.e it is any
				{
				
					match.consumeArg();
					match.setLastMatchSucceed(true);
					match.setLastMatchICType("COMPLEX");
					match.incNumXargs(1);
					// System.out.println("matched argument to COMPLEX\n");
										
				}
				else
				{   

					match.setLastMatchSucceed(false);
					
				}
			}
			else
			{
				match.setError(true);
				
			}
			if (null == match.getLastMatchICType())
			{
				match.setLastMatchSucceed(false);
			}
			return match;
		}
		else
		{	//RHS
			isComplexInfoPropMatch match = new isComplexInfoPropMatch(previousMatchResult);
			if (match.getNumMatched() == argValues.size())
			{
				//LHS matched 
				match.loadOutput("COMPLEX");
				// System.out.println("COMPLEX.");
				return match;
			}
			else
			{
				//match fail somewhere on LHS
				match.setError(true);
				return match;
			}
		}
	}
}