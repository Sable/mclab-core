package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.Value;

public class SPNumber extends SPAbstractScalarExpr
{
	static boolean Debug = true;
	Number n;
	public SPNumber (Number n)
	{
		this.n = n;
		//System.out.println(n.toString());
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues)
	{
		if(isPatternSide==true){//because number can pop up everywhere, so just store it in latestMatchedNumber!
			if(previousMatchResult.getArrayIndexAssign()==true){
				//FIXME
				if (Debug) System.out.println("inside number of an arrayIndex assignment!");
				if (Debug) System.out.println("currently, all the matched matrix are "+previousMatchResult.getAllUppercase());
				List<Integer> dimensions = new ArrayList<Integer>(previousMatchResult.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase()).getDimensions());
				dimensions.remove((previousMatchResult.getLatestMatchedNumber()-1));
				dimensions.add((previousMatchResult.getLatestMatchedNumber()-1), n.intValue());
				if (Debug) System.out.println(previousMatchResult.getAllUppercase());
				if (Debug) System.out.println("new dimension is "+dimensions);
				if (Debug) System.out.println("shape of "+previousMatchResult.getLatestMatchedUppercase()+" is "+previousMatchResult.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase()));
				HashMap<String, Shape<?>> uppercase = new HashMap<String, Shape<?>>();
				uppercase.put(previousMatchResult.getLatestMatchedUppercase(),(new ShapeFactory()).newShapeFromIntegers(dimensions));
				if (Debug) System.out.println(uppercase);
				if (Debug) System.out.println("currently, all the matched matrix are "+previousMatchResult.getAllUppercase());
				ShapePropMatch match = new ShapePropMatch(previousMatchResult, null, uppercase);
				if (Debug) System.out.println("currently, all the matched matrix are "+match.getAllUppercase());
				match.setArrayIndexAssign(false);
				return match;
			}
			if (Debug) System.out.println("inside match a number!");
			previousMatchResult.saveLatestMatchedNumber(n.intValue());
			return previousMatchResult;
		}
		else{
			return null;
		}
		
	}
	
	public String toString()
	{
		return n.toString();
	}
}
