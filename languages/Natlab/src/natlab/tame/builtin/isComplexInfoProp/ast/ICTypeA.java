package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.ArgICType;
import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.valueanalysis.value.Value;

public class ICTypeA extends ICType{
	
	
	public ICTypeA()
	{
		
	}

	
	public String toString()
	{
		return "ANY";
	}


	@Override
	public isComplexInfoPropMatch match(boolean isPatternSide,
			isComplexInfoPropMatch previousMatchResult, List<? extends Value<?>> argValues) {
		
		if(true==isPatternSide)//on the symbol on the LHS
		{
			/*
			 * If the argument is any ( we do not know for sure if it is real or complex then increment 
			 * any but do not increment R or X. Consume the argument. save the complexity info and all other 
			 * info to previousmatch and return
			 */
			isComplexInfoPropMatch match = new isComplexInfoPropMatch(previousMatchResult);

			//if(null != argValues.get(previousMatchResult.getNumMatched()))
			if(argValues.size() > previousMatchResult.getNumMatched())
			{
				Value<?> argument = argValues.get(previousMatchResult.getNumMatched());// get the value of argument
			//	ArgICType isArgComplex = new ArgICType(argument); //returns -1=complex, 0=any, 1= real
				 int isArgComplex =  (new ArgICType()).getArgICType(argument);
				
				
				 
				//int isArgComplex =0;
				//TODO - implement this method
				if (0 == isArgComplex || 1 == isArgComplex || -1 == isArgComplex) //i.e it is any
				{
					//mATCHED 
					// set the attributes of match object 
					
					//set values here and add to the  
					match.consumeArg();
					match.setLastMatchSucceed(true);
					match.setLastMatchICType("ANY");
					match.incNumAargs(1);
					if(1 == isArgComplex) match.incNumRargs(1);
					if(-1 == isArgComplex) match.incNumXargs(1);
					// System.out.println("matched argument to ANY\n");
										
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
				match.loadOutput("ANY");
				// System.out.println("ANY.");
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
