package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.components.constant.*;

public class SPUppercase<V extends Value<V>> extends SPAbstractVectorExpr<V>{
	static boolean Debug = false;
	String s;
	public SPUppercase (String s){
		this.s = s;
		//System.out.println(s);
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int num){
		if(isPatternSide){
			if(previousMatchResult.getIsInsideAssign()){
				previousMatchResult.saveLatestMatchedUppercase(s);
				return previousMatchResult;
			}
			if(argValues.get(previousMatchResult.getHowManyMatched())!=null){
				//get indexing current Matrix Value from args
				//get shape info from current Matrix Value
				Shape<V> argumentShape = ((HasShape<V>)argValues.get(previousMatchResult.getHowManyMatched())).getShape();
				Constant argumentConstant =((HasConstant)argValues.get(previousMatchResult.getHowManyMatched())).getConstant();
				if(argumentConstant!=null){
					if (Debug) System.out.println("it's a constant!");
					previousMatchResult.setIsError(true);
					return previousMatchResult;
				}
				//check whether or not current uppercase already in the previousMatchResult
				try{
					if(previousMatchResult.getLatestMatchedUppercase().equals(s)){
						//cases like (M,M->M), those M should be definitely the same!!! if not, return error information, interesting!
						List<DimValue> l = new ArrayList<DimValue>();
						l = previousMatchResult.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase()).getDimensions();
						Shape<V> oldShape = new ShapeFactory<V>().newShapeFromDimValues(l);
						//Shape<AggrValue<BasicMatrixValue>> newShape = argumentShape.merge(oldShape); this is wrong at all! see last comment!
						if(argumentShape.getDimensions().equals(oldShape.getDimensions())==false){
							//FIXME really weird, cannot call equals method in Shape class, the problem is still generic problem,
							//cannot cast from Shape<V> to Shape<V>
							if (Debug) System.out.println("MATLAB syntax error!");
							//Shape<AggrValue<BasicMatrixValue>> errorShape = (new ShapeFactory<AggrValue<BasicMatrixValue>>
							//(previousMatchResult.factory)).newShapeFromIntegers(null);
							Shape<V> errorShape = (new ShapeFactory<V>()).newShapeFromIntegers(null);
							errorShape.flagHasError();
							HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
							lowercase.put(s, new DimValue());
							HashMap<String, Shape<V>> uppercase = new HashMap<String, Shape<V>>();
							uppercase.put(s, errorShape);
							ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, lowercase, uppercase);
							match.comsumeArg();
							match.saveLatestMatchedUppercase(s);
							match.setIsError(true);//this is important!! break from matching algorithm.
							//System.out.println(match.getValueOfVariable(s));
							if (Debug) System.out.println("the shape of "+s+" is "+match.getShapeOfVariable(s));
							if (Debug) System.out.println("matched matrix expression "+match.getLatestMatchedUppercase());
							return match;
						}
						//if new shape and old shape are equals, just return this shape!
						HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
						lowercase.put(s, new DimValue());
						HashMap<String, Shape<V>> uppercase = new HashMap<String, Shape<V>>();
						uppercase.put(s, argumentShape);
						ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, lowercase, uppercase);
						match.comsumeArg();
						match.saveLatestMatchedUppercase(s);
						//System.out.println(match.getValueOfVariable(s));
						if (Debug) System.out.println("the shape of "+s+" is "+match.getShapeOfVariable(s));
						if (Debug) System.out.println("mathcing "+match.getLatestMatchedUppercase());
						return match;
					}
					
				}catch (Exception e){}
				HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
				lowercase.put(s, new DimValue());
				HashMap<String, Shape<V>> uppercase = new HashMap<String, Shape<V>>();
				uppercase.put(s, argumentShape);
				ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, lowercase, uppercase);
				match.comsumeArg();
				match.saveLatestMatchedUppercase(s);
				//System.out.println(match.getValueOfVariable(s));
				if (Debug) System.out.println("the shape of "+s+" is "+match.getShapeOfVariable(s));
				if (Debug) System.out.println("mathcing "+match.getLatestMatchedUppercase());
				return match;
			}
			//FIXME if index pointing empty, means not match, do something
			return previousMatchResult;
		}
		else{
			if (Debug) System.out.println("inside output uppercase "+s);
			// default, which means in the pattern match side, there is no Uppercase matched.
			if(previousMatchResult.getShapeOfVariable(s)==null){
				if(previousMatchResult.getOutputVertcatExpr().size()==0){
					if(previousMatchResult.getLatestMatchedUppercase().equals("$")){
						previousMatchResult.addToOutput(previousMatchResult.getShapeOfVariable("$"));
						previousMatchResult.emitOneResult();
						return previousMatchResult;
					}
					previousMatchResult.addToOutput(null);
					previousMatchResult.emitOneResult();
					return previousMatchResult;
				}
				else if(previousMatchResult.getOutputVertcatExpr().size()==1){
					previousMatchResult.addToVertcatExpr(previousMatchResult.getOutputVertcatExpr().get(0));
					previousMatchResult.copyVertcatToOutput();
					previousMatchResult.emitOneResult();
					return previousMatchResult;
				}
				else {
					previousMatchResult.copyVertcatToOutput();
					previousMatchResult.emitOneResult();
					return previousMatchResult;
				}
			}
			else{
				previousMatchResult.addToOutput(previousMatchResult.getShapeOfVariable(s));
				previousMatchResult.emitOneResult();
				return previousMatchResult;
			}	
		}
	}
	
	public String toString(){
		return s.toString();
	}
}
