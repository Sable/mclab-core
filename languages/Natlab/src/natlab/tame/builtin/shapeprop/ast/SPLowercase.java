package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.Value;

public class SPLowercase extends SPAbstractScalarExpr
{
	static boolean Debug = true;
	String s;
	public SPLowercase(String s)
	{
		this.s = s;
		//System.out.println(s);
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues)
	{
		if(isPatternSide==true){
			//for matching a vertcat expression, this step should be similar to Uppercase matching, to exam whether equals!
			if(previousMatchResult.isInsideVertcat()==true){
				if (Debug) System.out.println("a lowercase inside vertcat!");
				if (Debug) System.out.println(previousMatchResult.getNumInVertcat());
				Value<?> argument = argValues.get(previousMatchResult.getNumMatched());
				//get its shape information
				Shape<?> argumentShape = ((HasShape)argument).getShape();
				if (Debug) System.out.println("argument shape is "+argumentShape);
				if(previousMatchResult.getAllLowercase().containsKey(s)){
					try{
						if(((previousMatchResult.getValueOfVariable(s))==argumentShape.getDimensions().get(previousMatchResult.getNumInVertcat()))==false){
							if (Debug) System.out.println("two lowercase value not equal, throw error shape!");
							if (Debug) System.out.println("MATLAB syntax error!");
							previousMatchResult.setIsError();
							HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
							lowercase.put(s, null);
							ShapePropMatch match = new ShapePropMatch(previousMatchResult, lowercase, null);
							match.saveLatestMatchedLowercase(s);
							return match;
						}
						HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
						lowercase.put(s, argumentShape.getDimensions().get(previousMatchResult.getNumInVertcat()));
						ShapePropMatch match = new ShapePropMatch(previousMatchResult, lowercase, null);
						if (Debug) System.out.println(match.getAllLowercase());
						match.setNumInVertcat(previousMatchResult.getNumInVertcat()+1);
						return match;
					}catch (Exception e){}
					//means one of them, the previous matched lowercase or current number in the argument, is null
					if (Debug) System.out.println("there is null information in dimensions!");
					previousMatchResult.setIsError();
					return previousMatchResult;
				}
				HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
				lowercase.put(s, argumentShape.getDimensions().get(previousMatchResult.getNumInVertcat()));
				ShapePropMatch match = new ShapePropMatch(previousMatchResult, lowercase, null);
				if (Debug) System.out.println(match.getAllLowercase());
				match.setNumInVertcat(previousMatchResult.getNumInVertcat()+1);
				return match;
			}
			//for assign a lowercase's value to an ArrayIndex FIXME
			if(previousMatchResult.getAllLowercase().containsKey(s)){
				if(previousMatchResult.isArrayIndexAssignRight()==true){
					//FIXME
					if (Debug) System.out.println("inside lowercase of an arrayIndex assignment!");
					List<Integer> dimensions = previousMatchResult.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase()).getDimensions();
					dimensions.remove((previousMatchResult.getLatestMatchedNumber()-1));
					dimensions.add((previousMatchResult.getLatestMatchedNumber()-1), previousMatchResult.getValueOfVariable(s));
					if (Debug) System.out.println("new dimension is "+dimensions);
					if (Debug) System.out.println("shape of "+previousMatchResult.getLatestMatchedUppercase()+" is "+previousMatchResult.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase()));
					HashMap<String, Shape<?>> uppercase = new HashMap<String, Shape<?>>();
					uppercase.put(previousMatchResult.getLatestMatchedUppercase(),(new ShapeFactory()).newShapeFromIntegers(dimensions));
					if (Debug) System.out.println(uppercase);
					ShapePropMatch match = new ShapePropMatch(previousMatchResult, null, uppercase);
					match.setArrayIndexAssignRight(false);
					return match;
				}
				if (Debug) System.out.println(s+" is contained in the hashmap!");
				previousMatchResult.saveLatestMatchedLowercase(s);
				return previousMatchResult;
			}
			//for store some information in a lowercase
			HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
			lowercase.put(s, null);
			ShapePropMatch match = new ShapePropMatch(previousMatchResult, lowercase, null);
			match.saveLatestMatchedLowercase(s);
			if (Debug) System.out.println("inside SPLowercase "+match.getLatestMatchedLowercase()+", vertcat expression now: "+match.getOutputVertcatExpr());
			return match;
		}
		else{
			previousMatchResult.addToVertcatExpr(previousMatchResult.getValueOfVariable(s));
			return previousMatchResult;
		}
		
	}
	
	public String toString()
	{
		return s.toString();
	}
}
