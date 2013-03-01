package natlab.tame.builtin.shapeprop.ast;

import java.util.HashMap;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;
import natlab.tame.valueanalysis.components.constant.*;
import natlab.tame.valueanalysis.components.shape.*;

public class SPScalar<V extends Value<V>> extends SPAbstractVectorExpr<V>{
	static boolean Debug = false;
	String s;
	
	public SPScalar (String s){
		this.s = s;
		//System.out.println("$");
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int num){
		if (isPatternSide==true){  //we find a scalar in the tree, we have to distinguish whether it is in pattern matching part, or output part
			//if the whole argument is null
			if(argValues.isEmpty()){
				previousMatchResult.setIsError();
				return previousMatchResult;
			}
			if (argValues.get(previousMatchResult.getNumMatched())!=null){
				//get indexing basicMatrixValue
				Value<V> argument = argValues.get(previousMatchResult.getNumMatched());
				if (Debug) System.out.println(argument);
				if((((HasConstant)argument).getConstant()==null)&&((((HasShape<V>)argument).getShape())==null)){
					HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
					lowercase.put(s, null);
					HashMap<String, Shape<V>> uppercase = new HashMap<String, Shape<V>>();
					uppercase.put(s, (new ShapeFactory<V>()).newShapeFromIntegers((new DoubleConstant(1).getShape())));
					ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, lowercase, uppercase);
					match.comsumeArg();
					match.saveLatestMatchedUppercase(s);
					if (Debug) System.out.println(match.getAllLowercase());
					if (Debug) System.out.println("inside empty constant value and shape value mathcing a Scalar!");
					return match;
				}
				//get its constant int value
				if((((HasConstant)argument).getConstant()==null)){//maybe it's a scalar but value is unknown or it's not a scalar, which means not matched!
					//first, test whether or not it's not a scalar, which means, constant value is empty but shape is not [1,1]
					if(((HasShape<V>)argument).getShape().equals((new ShapeFactory<V>()).newShapeFromIntegers((new DoubleConstant(1).getShape())))!=true){
						if (Debug) System.out.println("it's may not a scalar");
						previousMatchResult.setIsError();
						return previousMatchResult;
					}
					HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
					lowercase.put(s, null);
					HashMap<String, Shape<V>> uppercase = new HashMap<String, Shape<V>>();
					Shape<V> argumentShape = ((HasShape<V>)argument).getShape();
					uppercase.put(s, argumentShape);
					ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, lowercase, uppercase);
					match.comsumeArg();
					match.saveLatestMatchedUppercase(s);
					if (Debug) System.out.println(match.getAllLowercase());
					if (Debug) System.out.println("inside empty constant value mathcing a Scalar!");
					return match;
				}
				try{//for the case, the argument is a char or string constant!
					double argumentConstantDouble = (Double) ((HasConstant)argument).getConstant().getValue();
					int argumentIntValue = (int) argumentConstantDouble;
					if (Debug) System.out.println("argument value is "+argumentIntValue);
					//get its shape information
					Shape<V> argumentShape = ((HasShape<V>)argument).getShape();
					if (Debug) System.out.println("argument shape is "+argumentShape);
					
					HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
					lowercase.put(s, argumentIntValue);
					HashMap<String, Shape<V>> uppercase = new HashMap<String, Shape<V>>();
					uppercase.put(s, argumentShape);
					ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, lowercase, uppercase);
					match.comsumeArg();
					match.saveLatestMatchedUppercase(s);
					if (Debug) System.out.println("mathcing a Scalar!");
					return match;
				}catch (Exception e){
					previousMatchResult.setIsError();
					return previousMatchResult;
				}
				
			}
			//if the argument is empty, of course, this is not-matched case
			else
				previousMatchResult.setIsError();
				return previousMatchResult;
		}
		else{
			previousMatchResult.addToOutput((new ShapeFactory<V>()).newShapeFromIntegers((new DoubleConstant(1).getShape())));
			return previousMatchResult;
		}
	}
		
	
	public String toString(){
		return s.toString();
	}
}
