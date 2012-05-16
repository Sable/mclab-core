package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropTool;
import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class ICTypeA extends ICType{
	
	
	public ICTypeA()
	{
		
	}

	
	public String toString()
	{
		return "A";
	}


	@Override
	public isComplexInfoPropMatch match(boolean isPatternSide,
			isComplexInfoPropMatch previousMatchResult, List<Integer> argValues) {
		
		if(true==isPatternSide)//on the symbol on the LHS
		{
			/*
			 * If the argument is any ( we do not know for sure if it is real or complex then increment 
			 * any but do not increment R or X. Consume the argument. save the complexity info and all other 
			 * info to previousmatch and return
			 */
			isComplexInfoPropMatch match = new isComplexInfoPropMatch(previousMatchResult);

			if(null != argValues.get(previousMatchResult.getNumMatched()))
			{
				int argument = argValues.get(previousMatchResult.getNumMatched());// get the value of argument
			//	ArgICType isArgComplex = new ArgICType(argument); //returns -1=complex, 0=any, 1= real
				 int isArgComplex =  ArgICType.getArgICType(argument);
				//TODO - implement this method
				if (0 == isArgComplex) //i.e it is any
				{
					//mATCHED 
					// set the attributes of match object 
					
					//set values here and add to the  
					match.consumeArg();
					match.setLastMatchSucceed(true);
					match.setLastMatchICType("ANY");
					match.incNumAargs(1);
					System.out.println("matched argument to ANY\n");
										
				}
				else
				{
					match.setLastMatchSucceed(false);
				}
			}
			return match;
		}
		else
		{
			return previousMatchResult; //TODO change for RHS
		}
		
	}
}
