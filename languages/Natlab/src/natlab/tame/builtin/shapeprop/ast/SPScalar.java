package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Value;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.components.constant.*;
import natlab.tame.valueanalysis.components.shape.*;
//import natlab.tame.valueanalysis.basicmatrix.*;

public class SPScalar extends SPAbstractVectorExpr{
	static boolean Debug = true;
	String s;
	
	public SPScalar (String s){
		this.s = s;
		//System.out.println("$");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues){
		if (isPatternSide==true){  //we find a scalar in the tree, we have to distinguish whether it is in pattern matching part, or output part
			//if the whole argument is null
			if(argValues.isEmpty()){
				previousMatchResult.setIsError();
				return previousMatchResult;
			}
			if (argValues.get(previousMatchResult.getNumMatched())!=null){
				//get indexing basicMatrixValue
				Value<?> argument = argValues.get(previousMatchResult.getNumMatched());
				if (Debug) System.out.println(argument);
				if((((HasConstant)argument).getConstant()==null)&&((((HasShape)argument).getShape())==null)){
					HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
					lowercase.put(s, null);
					HashMap<String, Shape<?>> uppercase = new HashMap<String, Shape<?>>();
					uppercase.put(s, (new ShapeFactory()).newShapeFromIntegers((new DoubleConstant(1).getShape())));
					ShapePropMatch match = new ShapePropMatch(previousMatchResult, lowercase, uppercase);
					match.comsumeArg();
					match.saveLatestMatchedUppercase(s);
					if (Debug) System.out.println(match.getAllLowercase());
					if (Debug) System.out.println("inside empty constant value and shape value mathcing a Scalar!");
					return match;
				}
				//get its constant int value
				if((((HasConstant)argument).getConstant()==null)){//maybe it's a scalar but value is unknown or it's not a scalar, which means not matched!
					//first, test whether or not it's not a scalar, which means, constant value is empty but shape is not [1,1]
					if(((HasShape)argument).getShape().equals((new ShapeFactory()).newShapeFromIntegers((new DoubleConstant(1).getShape())))!=true){
						if (Debug) System.out.println("it's definitely not a scalar");
						previousMatchResult.setIsError();
						return previousMatchResult;
					}
					HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
					lowercase.put(s, null);
					HashMap<String, Shape<?>> uppercase = new HashMap<String, Shape<?>>();
					Shape<?> argumentShape = ((HasShape)argument).getShape();
					uppercase.put(s, argumentShape);
					ShapePropMatch match = new ShapePropMatch(previousMatchResult, lowercase, uppercase);
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
					Shape<?> argumentShape = ((HasShape)argument).getShape();
					if (Debug) System.out.println("argument shape is "+argumentShape);
					
					HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
					lowercase.put(s, argumentIntValue);
					HashMap<String, Shape<?>> uppercase = new HashMap<String, Shape<?>>();
					uppercase.put(s, argumentShape);
					ShapePropMatch match = new ShapePropMatch(previousMatchResult, lowercase, uppercase);
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
			previousMatchResult.addToOutput(s, (new ShapeFactory()).newShapeFromIntegers((new DoubleConstant(1).getShape())));
			return previousMatchResult;
		}
	}
		
	
	public String toString(){
		return s.toString();
	}
}
