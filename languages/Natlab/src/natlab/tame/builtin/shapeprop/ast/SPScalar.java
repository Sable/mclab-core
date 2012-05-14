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

public class SPScalar extends SPAbstractVectorExpr
{
	static boolean Debug;
	String s;
	
	public SPScalar (String s)
	{
		this.s = s;
		//System.out.println("$");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues)
	{
		//System.out.println("inside dollar");
		if (isPatternSide==true){  //we find a DOLLAR in the tree, we have to distinguish whether it is in pattern matching part, or output part
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
					//System.out.println(match.getValueOfVariable("Scalar"));
					//System.out.println(match.getAllResults());
					if (Debug) System.out.println(match.getAllLowercase());
					if (Debug) System.out.println("mathcing a Scalar!");
					return match;
				}
				//get its constant int value
				if((((HasConstant)argument).getConstant()==null)){
					HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
					lowercase.put(s, null);
					HashMap<String, Shape<?>> uppercase = new HashMap<String, Shape<?>>();
					Shape<?> argumentShape = ((HasShape)argument).getShape();
					uppercase.put(s, argumentShape);
					ShapePropMatch match = new ShapePropMatch(previousMatchResult, lowercase, uppercase);
					match.comsumeArg();
					match.saveLatestMatchedUppercase(s);
					//System.out.println(match.getValueOfVariable("Scalar"));
					//System.out.println(match.getAllResults());
					if (Debug) System.out.println(match.getAllLowercase());
					if (Debug) System.out.println("mathcing a Scalar!");
					return match;
				}
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
				//System.out.println(match.getValueOfVariable(s));
				//System.out.println(match.getAllResults());
				//System.out.println(match.getAllLowercase());
				if (Debug) System.out.println("mathcing a Scalar!");
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
		return s.toString();
	}
}
