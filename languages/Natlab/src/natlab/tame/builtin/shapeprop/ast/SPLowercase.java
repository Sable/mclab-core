package natlab.tame.builtin.shapeprop.ast;

import java.util.HashMap;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.*;

public class SPLowercase<V extends Value<V>> extends SPAbstractScalarExpr<V>{
	static boolean Debug = false;
	String s;
	public SPLowercase(String s){
		this.s = s;
		//System.out.println(s);
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int num){
		if(isPatternSide==true){
			//for matching a vertcat expression, this step should be similar to Uppercase matching, to exam whether equals!
			if(previousMatchResult.getIsInsideVertcat()==true){
				if (Debug) System.out.println("a lowercase inside vertcat!");
				if (Debug) System.out.println(previousMatchResult.getNumInVertcat());
				Value<V> argument = argValues.get(previousMatchResult.getHowManyMatched());
				//get its shape information
				Shape<V> argumentShape = ((HasShape<V>)argument).getShape();
				if (Debug) System.out.println("argument shape is "+argumentShape);
				if(previousMatchResult.getAllLowercase().containsKey(s)){
					try{
						if((previousMatchResult.getValueOfVariable(s).getValue()==argumentShape.getDimensions()
								.get(previousMatchResult.getNumInVertcat()).getValue())==false){
							if (Debug) System.out.println("two lowercase value not equal, throw error shape!");
							if (Debug) System.out.println("MATLAB syntax error!");
							previousMatchResult.setIsError(true);
							HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
							lowercase.put(s, new DimValue());
							ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
							match.saveLatestMatchedLowercase(s);
							return match;
						}
						HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
						lowercase.put(s, new DimValue(argumentShape.getDimensions().get(previousMatchResult.getNumInVertcat()).getValue(), null));
						ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
						if (Debug) System.out.println(match.getAllLowercase());
						match.setNumInVertcat(previousMatchResult.getNumInVertcat()+1);
						return match;
					}catch (Exception e){}
					//means one of them, the previous matched lowercase or current number in the argument, is null
					if (Debug) System.out.println("there is null information in dimensions!");
					previousMatchResult.setIsError(true);
					return previousMatchResult;
				}
				HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
				if(argumentShape.getDimensions().get(previousMatchResult.getNumInVertcat())==null){
					lowercase.put(s, new DimValue());
					ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
					if (Debug) System.out.println(match.getAllLowercase());
					match.setNumInVertcat(previousMatchResult.getNumInVertcat()+1);
					return match;
				}
				else{
					lowercase.put(s, new DimValue(argumentShape.getDimensions().get(previousMatchResult.getNumInVertcat()).getValue(), null));
					ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
					if (Debug) System.out.println(match.getAllLowercase());
					match.setNumInVertcat(previousMatchResult.getNumInVertcat()+1);
					return match;
				}
			}
			//for assign a lowercase's value to an ArrayIndex
			if(previousMatchResult.getIsArrayIndexAssignRight()==true){
				if (Debug) System.out.println("trying to assign the value of "+s.toString()+" to an array!");
				List<DimValue> dimensions = previousMatchResult.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase()).getDimensions();
				if(previousMatchResult.getWhetherLatestMatchedIsNum()==true){
					if (Debug) System.out.println("inside assigning a lowercase's value to array with num index!");//i.e. M(2)=n;
					//deal with the case that index overflow
					if((dimensions.size()-1)<(previousMatchResult.getLatestMatchedNumber()-1)){
						if (Debug) System.out.println("index overflow "+dimensions.size()+" "+previousMatchResult.getLatestMatchedNumber());
						if (Debug) System.out.println("dimension should not be changed!");
						previousMatchResult.setArrayIndexAssignRight(false);
						return previousMatchResult;
					}
					dimensions.remove((previousMatchResult.getLatestMatchedNumber()-1));
					if(previousMatchResult.hasValue(s)){
						dimensions.add((previousMatchResult.getLatestMatchedNumber()-1), previousMatchResult.getValueOfVariable(s));
					}
					else{
						dimensions.add((previousMatchResult.getLatestMatchedNumber()-1), new DimValue());
					}
				}
				else{
					if (Debug) System.out.println("inside assigning a lowercase's value to array with lowercase index!");//i.e. M(n)=m;
					if (Debug) System.out.println("inside assigning a num to array with lowercase index!");//i.e. M(n)=2;
					//deal with the case that index overflow
					if((dimensions.size()-1)<(previousMatchResult.getValueOfVariable(previousMatchResult.getLatestMatchedLowercase()).getValue()-1)){
						if (Debug) System.out.println("index overflow "+dimensions.size()+" "+previousMatchResult
								.getValueOfVariable(previousMatchResult.getLatestMatchedLowercase()));
						if (Debug) System.out.println("dimension should not be changed!");
						previousMatchResult.setArrayIndexAssignRight(false);
						return previousMatchResult;
					}
					dimensions.remove(previousMatchResult.getValueOfVariable(previousMatchResult.getLatestMatchedLowercase()).getValue()-1);
					dimensions.add(previousMatchResult.getValueOfVariable(previousMatchResult.getLatestMatchedLowercase()).getValue()-1
							, previousMatchResult.getValueOfVariable(s));
				}
				if (Debug) System.out.println("new dimension is "+dimensions);
				if (Debug) System.out.println("shape of "+previousMatchResult.getLatestMatchedUppercase()+" is "+previousMatchResult
						.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase()));
				HashMap<String, Shape<V>> uppercase = new HashMap<String, Shape<V>>();
				uppercase.put(previousMatchResult.getLatestMatchedUppercase(),new ShapeFactory<V>().newShapeFromDimValues(dimensions));
				if (Debug) System.out.println(uppercase);
				ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, null, uppercase);
				match.setArrayIndexAssignRight(false);
				return match;
			}
			if(previousMatchResult.getAllLowercase().containsKey(s)){
				if (Debug) System.out.println(s+" is contained in the hashmap!");
				previousMatchResult.saveLatestMatchedLowercase(s);
				return previousMatchResult;
			}
			//for store some information in a lowercase
			HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
			lowercase.put(s, new DimValue());
			ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
			match.saveLatestMatchedLowercase(s);
			if (Debug) System.out.println("inside SPLowercase "+match.getLatestMatchedLowercase()+", vertcat expression now: "+match.getOutputVertcatExpr());
			return match;
		}
		else{
			previousMatchResult.addToVertcatExpr(previousMatchResult.getValueOfVariable(s));
			return previousMatchResult;
		}
		
	}
	
	public String toString(){
		return s.toString();
	}
}
