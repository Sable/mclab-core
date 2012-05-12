package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.components.constant.HasConstant;
import natlab.tame.valueanalysis.components.shape.HasShape;
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.tame.valueanalysis.components.shape.ShapeFactory;
import natlab.tame.valueanalysis.value.Value;

public class SPLowercase extends SPAbstractScalarExpr
{
	String s;
	public SPLowercase(String s)
	{
		this.s = s;
		//System.out.println(s);
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues)
	{
		if(isPatternSide==true){
			if(previousMatchResult.isInsideVertcat()){
				System.out.println("a lowercase inside vertcat!");
				System.out.println(previousMatchResult.getNumInVertcat());
				Value<?> argument = argValues.get(previousMatchResult.getNumMatched());
				//get its shape information
				Shape<?> argumentShape = ((HasShape)argument).getShape();
				System.out.println("argument shape is "+argumentShape);
				HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
				lowercase.put(s, argumentShape.getDimensions().get(previousMatchResult.getNumInVertcat()));
				ShapePropMatch match = new ShapePropMatch(previousMatchResult, lowercase, null);
				System.out.println(match.getAllLowercase());
				match.setNumInVertcat(previousMatchResult.getNumInVertcat()+1);
				return match;
			}
			if(previousMatchResult.getAllLowercase().containsKey(s)){
				if(previousMatchResult.getArrayIndexAssign()==true){
					//FIXME
					System.out.println("inside lowercase of an arrayIndex assignment!");
					List<Integer> dimensions = previousMatchResult.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase()).getDimensions();
					dimensions.remove((previousMatchResult.getLatestMatchedNumber()-1));
					dimensions.add((previousMatchResult.getLatestMatchedNumber()-1), previousMatchResult.getValueOfVariable(s));
					System.out.println("new dimension is "+dimensions);
					System.out.println("shape of "+previousMatchResult.getLatestMatchedUppercase()+" is "+previousMatchResult.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase()));
					HashMap<String, Shape<?>> uppercase = new HashMap<String, Shape<?>>();
					uppercase.put(previousMatchResult.getLatestMatchedUppercase(),(new ShapeFactory()).newShapeFromIntegers(dimensions));
					System.out.println(uppercase);
					ShapePropMatch match = new ShapePropMatch(previousMatchResult, null, uppercase);
					match.setArrayIndexAssign(false);
					return match;
				}
				System.out.println(s+" is contained in the hashmap!");
				previousMatchResult.saveLatestMatchedLowercase(s);
				return previousMatchResult;
			}
			HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
			lowercase.put(s, null);
			ShapePropMatch match = new ShapePropMatch(previousMatchResult, lowercase, null);
			match.saveLatestMatchedLowercase(s);
			System.out.println("inside SPLowercase "+match.getLatestMatchedLowercase()+", vertcat expression now: "+match.getOutputVertcatExpr());
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
