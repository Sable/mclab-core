package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Value;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.*;
import natlab.tame.valueanalysis.components.shape.HasShape;
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.tame.valueanalysis.components.shape.ShapeFactory;

public class SPUppercase extends SPAbstractVectorExpr
{
	String s;
	public SPUppercase (String s)
	{
		this.s = s;
		//System.out.println(s);
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues)
	{
		if(isPatternSide==true){
			if(argValues.get(previousMatchResult.getNumMatched())!=null){
				//get indexing current Matrix Value from args
				BasicMatrixValue argument = (BasicMatrixValue)argValues.get(previousMatchResult.getNumMatched());
				//get shape info from current Matrix Value
				Shape<AggrValue<BasicMatrixValue>> argumentShape = ((HasShape)argument).getShape();
				//check whether or not current uppercase already in the previousMatchResult
				try{
					if(previousMatchResult.getLatestMatchedUppercase().equals(s)){
						//cases like (M,M->M), those M should be definitely the same!!! if not, return error information, interesting!
						List<Integer> l = new ArrayList<Integer>();
						l = previousMatchResult.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase()).getDimensions();
						Shape<AggrValue<BasicMatrixValue>> oldShape = (new ShapeFactory<AggrValue<BasicMatrixValue>>(previousMatchResult.factory)).newShapeFromIntegers(l);
						//Shape<AggrValue<BasicMatrixValue>> newShape = argumentShape.merge(oldShape); this is wrong at all! see last comment!
						if(argumentShape.equals(oldShape)==false){//FIXME
							System.out.println("MATLAB syntax error!");
							Shape<AggrValue<BasicMatrixValue>> errorShape = (new ShapeFactory<AggrValue<BasicMatrixValue>>(previousMatchResult.factory)).newShapeFromIntegers(null);
							errorShape.FlagItsError();
							HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
							lowercase.put(s, null);
							HashMap<String, Shape<?>> uppercase = new HashMap<String, Shape<?>>();
							uppercase.put(s, errorShape);
							ShapePropMatch match = new ShapePropMatch(previousMatchResult, lowercase, uppercase);
							match.comsumeArg();
							match.saveLatestMatchedUppercase(s);
							//System.out.println(match.getValueOfVariable(s));
							System.out.println("the shape of "+s+" is "+match.getShapeOfVariable(s));
							System.out.println("matched matrix expression "+match.getLatestMatchedUppercase());
							return match;
						}
						//if new shape and old shape are equals, just return this shape!
						HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
						lowercase.put(s, null);
						HashMap<String, Shape<?>> uppercase = new HashMap<String, Shape<?>>();
						uppercase.put(s, argumentShape);
						ShapePropMatch match = new ShapePropMatch(previousMatchResult, lowercase, uppercase);
						match.comsumeArg();
						match.saveLatestMatchedUppercase(s);
						//System.out.println(match.getValueOfVariable(s));
						System.out.println("the shape of "+s+" is "+match.getShapeOfVariable(s));
						System.out.println("mathcing "+match.getLatestMatchedUppercase());
						return match;
					}
					
				}catch (Exception e){}
				HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
				lowercase.put(s, null);
				HashMap<String, Shape<?>> uppercase = new HashMap<String, Shape<?>>();
				uppercase.put(s, argumentShape);
				ShapePropMatch match = new ShapePropMatch(previousMatchResult, lowercase, uppercase);
				match.comsumeArg();
				match.saveLatestMatchedUppercase(s);
				//System.out.println(match.getValueOfVariable(s));
				System.out.println("the shape of "+s+" is "+match.getShapeOfVariable(s));
				System.out.println("mathcing "+match.getLatestMatchedUppercase());
				return match;
			}
			return previousMatchResult;
		}
		else{
			System.out.println("inside output uppercase "+s);
			//default
			if(previousMatchResult.getShapeOfVariable(s)==null){
				if(previousMatchResult.getOutputVertcatExpr().size()==1){
					previousMatchResult.addToVertcatExpr(previousMatchResult.getOutputVertcatExpr().get(0));
					previousMatchResult.copyVertcatToOutput(s);
					return previousMatchResult;
				}
				else{
					previousMatchResult.copyVertcatToOutput(s);
					return previousMatchResult;
				}
			}
			else
				previousMatchResult.addToOutput(s, previousMatchResult.getShapeOfVariable(s));
				return previousMatchResult;
				
		}
	}
	
	public String toString()
	{
		return s.toString();
	}
}
