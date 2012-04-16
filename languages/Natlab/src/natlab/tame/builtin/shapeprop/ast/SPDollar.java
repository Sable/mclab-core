package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Value;
import natlab.tame.valueanalysis.components.constant.*;;

public class SPDollar extends SPAbstractVectorExpr
{
	public SPDollar ()
	{
		//System.out.println("$");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, ArrayList<Integer> argValues)
	{
		//System.out.println("inside dollar");
		if (isPatternSide==true){  //we find a DOLLAR in the tree, we have to distinguish whether it is in pattern matching part, or output part
			if (argValues.get(previousMatchResult.getNumMatched())!=null){
				ArrayList<Integer> shape = new ArrayList<Integer>(2);
				shape.add(1);
				shape.add(1);
				int value = argValues.get(previousMatchResult.getNumMatched());
				HashMap<String, Integer> valueDollar = new HashMap<String, Integer>();
				valueDollar.put("DOLLAR", value);
				HashMap<String, List<Integer>> shapeDollar = new HashMap<String, List<Integer>>();
				shapeDollar.put("DOLLAR", shape);
				ShapePropMatch match = new ShapePropMatch(previousMatchResult, valueDollar, shapeDollar);
				match.comsumeArg();
				match.saveLatestMatchedUpperCase("DOLLAR");
				//System.out.println(match.getValueOfVariable("DOLLAR"));
				//System.out.println(match.getAllResults());
				//System.out.println(match.getAllLowercase());
				System.out.println("mathcing a DOLLAR!");
				return match;
			}
			else
				return null;
		}
		else{
			//System.out.println("printing the output shape info:");
			//System.out.println(previousMatchResult.getAllResults());
			//System.out.println(previousMatchResult.getAllLowerCase());
			ArrayList<Integer> dollarShape = new ArrayList<Integer>(2);
			dollarShape.add(1);
			dollarShape.add(1);
			System.err.println("the output shape is "+dollarShape);
			return previousMatchResult;
		}
	}
		
	
	public String toString()
	{
		return "$";
	}
}
