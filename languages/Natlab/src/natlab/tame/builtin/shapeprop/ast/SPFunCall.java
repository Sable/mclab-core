package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPFunCall extends SPAbstractVertcatExprArg{
	static boolean Debug = true;
	String i;
	SPAbstractVertcatExprArg ls;
	public SPFunCall(String i, SPAbstractVertcatExprArg ls){
		this.i = i;
		this.ls = ls;
		//System.out.println("functionCall:"+i);
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues){
		//System.out.println("function: "+i);
		if((i.indexOf("previousScalar")==0)&(ls==null)){
			if (Debug) System.out.println("inside previousScalar");
			HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
			previousMatchResult.getLatestMatchedLowercase();
			if (Debug) System.out.println(previousMatchResult.getLatestMatchedLowercase());
			try{
				if (Debug) System.out.println(previousMatchResult.getValueOfVariable(previousMatchResult.getLatestMatchedUppercase()));
			}catch (Exception e){
				lowercase.put(previousMatchResult.getLatestMatchedLowercase(), null);
				ShapePropMatch matchResult = new ShapePropMatch(previousMatchResult, lowercase, null);
				//System.out.println(matchResult.getAllLowercase());
	            return matchResult;				
			}
			lowercase.put(previousMatchResult.getLatestMatchedLowercase(), previousMatchResult.getValueOfVariable(previousMatchResult.getLatestMatchedUppercase()));
			ShapePropMatch matchResult = new ShapePropMatch(previousMatchResult, lowercase, null);
			//System.out.println(matchResult.getAllLowercase());
            return matchResult;
		} 
		else if(i.indexOf("add")==0){
			if (Debug) System.out.println("inside add");
			try{
				if (Debug) System.out.println(previousMatchResult.getValueOfVariable((previousMatchResult.getLatestMatchedLowercase())));
			}catch (Exception e){
				previousMatchResult.addToVertcatExpr(null);
				if (Debug) System.out.println(previousMatchResult.getOutputVertcatExpr());
				return previousMatchResult;
			}
			previousMatchResult.addToVertcatExpr(previousMatchResult.getValueOfVariable((previousMatchResult.getLatestMatchedLowercase())));
			if (Debug) System.out.println(previousMatchResult.getOutputVertcatExpr());
			return previousMatchResult;
		}
		else if(i.indexOf("minus")==0){
			if (Debug) System.out.println("inside minus("+ls.toString()+")");
			String[] arg = ls.toString().split(",");
			if(arg.length==2){
				try{
					int minus = previousMatchResult.getValueOfVariable(arg[0])-previousMatchResult.getValueOfVariable(arg[1])+1;
					HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
					lowercase.put(previousMatchResult.getLatestMatchedLowercase(), minus);
					ShapePropMatch matchResult = new ShapePropMatch(previousMatchResult, lowercase, null);
					if (Debug) System.out.println(matchResult.getAllLowercase());
		            return matchResult;
				}
				catch(Exception e){
					System.out.println("one of the arguments is null!");
					HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
					lowercase.put(previousMatchResult.getLatestMatchedLowercase(), null);
					ShapePropMatch matchResult = new ShapePropMatch(previousMatchResult, lowercase, null);
					if (Debug) System.out.println(matchResult.getAllLowercase());
		            return matchResult;
				}
			}
			return previousMatchResult;
		}
		else if(i.indexOf("previousShapeDim")==0){
			if (Debug) System.out.println("inside previousShapeDim("+ls.toString()+")");
			previousMatchResult = ls.match(isPatternSide, previousMatchResult, argValues);//try to get argument information
			int dimNum = previousMatchResult.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase()).getDimensions().get(previousMatchResult.getLatestMatchedNumber()-1);
			HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
			lowercase.put(previousMatchResult.getLatestMatchedLowercase(), dimNum);
			ShapePropMatch matchResult = new ShapePropMatch(previousMatchResult, lowercase, null);
			if (Debug) System.out.println(matchResult.getAllLowercase());
            return matchResult;
		}
		else if(i.indexOf("isequal")==0){
			if (Debug) System.out.println("inside isequal("+ls.toString()+")");
			String[] arg = ls.toString().split(",");
			if(arg.length==2){
				if (Debug) System.out.println(previousMatchResult.getShapeOfVariable(arg[0])+" compare with "+previousMatchResult.getShapeOfVariable(arg[1]));
				Shape<?> first = (Shape<?>)previousMatchResult.getShapeOfVariable(arg[0]);
				Shape<?> second = (Shape<?>)previousMatchResult.getShapeOfVariable(arg[1]);
				//actually, I don't know what happened here, need more consideration later.
				if(((Shape<AggrValue<BasicMatrixValue>>)first).equals((Shape<AggrValue<BasicMatrixValue>>)second)){
					if (Debug) System.out.println(previousMatchResult.getShapeOfVariable(arg[0])+" is equal to "+previousMatchResult.getShapeOfVariable(arg[1]));
					return previousMatchResult;
				}
				else{
					previousMatchResult.setIsError();
					return previousMatchResult;
				}
			}
		}
		else if(i.indexOf("increment")==0){
			if (Debug) System.out.println("inside increment("+ls.toString()+")");
			if (Debug) System.out.println(previousMatchResult.getAllLowercase());
			int sum = previousMatchResult.getValueOfVariable(previousMatchResult.getLatestMatchedLowercase())+previousMatchResult.getValueOfVariable(ls.toString());
			if (Debug) System.out.println(sum);
			HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
			lowercase.put(previousMatchResult.getLatestMatchedLowercase(), sum);
			previousMatchResult = new ShapePropMatch(previousMatchResult, lowercase, null);
			if (Debug) System.out.println(previousMatchResult.getAllLowercase());
			return previousMatchResult;
		}
		else if(i.indexOf("copy")==0){
			if (Debug) System.out.println("inside copy("+ls.toString()+")");
			if (Debug) System.out.println(previousMatchResult.getLatestMatchedUppercase());
			HashMap<String, Shape<?>> uppercase = new HashMap<String, Shape<?>>();
			Shape<?> newShape = (new ShapeFactory()).newShapeFromIntegers(previousMatchResult.getShapeOfVariable(ls.toString()).getDimensions());
			uppercase.put(previousMatchResult.getLatestMatchedUppercase(), newShape);
			ShapePropMatch match = new ShapePropMatch(previousMatchResult, null, uppercase);
			if (Debug) System.out.println(match.getAllUppercase());
			return match;
		}
		else if(i.indexOf("minimum")==0){
			if (Debug) System.out.println("inside minimum("+ls.toString()+")");
			String[] arg = ls.toString().split(",");
			if(arg.length==2){
				try{
					int f = previousMatchResult.getValueOfVariable(arg[0]);
					int s = previousMatchResult.getValueOfVariable(arg[1]);
					String result = (f<s)?arg[0]:arg[1];
					if (Debug) System.out.println("the minimum one is "+result);
					HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
					lowercase.put(previousMatchResult.getLatestMatchedLowercase(), previousMatchResult.getValueOfVariable(result));
					ShapePropMatch match = new ShapePropMatch(previousMatchResult, lowercase, null);
					return match;
				}
				catch(Exception e){
					if (Debug) System.out.println("one of the arguments of minimum is null!");
					HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
					lowercase.put(previousMatchResult.getLatestMatchedLowercase(), null);
					ShapePropMatch match = new ShapePropMatch(previousMatchResult, lowercase, null);
					return match;
				}
				}
			//return error shape, FIXME
		}
		else if(i.indexOf("anyDimensionBigger")==0){
			if (Debug) System.out.println("inside anyDimensionBigger than "+ls.toString());
			previousMatchResult = ls.match(isPatternSide, previousMatchResult, argValues);
			int latestMatchedNum = previousMatchResult.getLatestMatchedNumber();
			List<Integer> dimensions = previousMatchResult.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase()).getDimensions();
			for(Integer d : dimensions){
				if(d>=latestMatchedNum){
					return previousMatchResult;
				}
			}
			Shape<?> errorShape = (new ShapeFactory()).newShapeFromIntegers(null);
			errorShape.FlagItsError();
			HashMap<String, Shape<?>> uppercase = new HashMap<String, Shape<?>>();
			uppercase.put(previousMatchResult.getLatestMatchedUppercase(), errorShape);
			ShapePropMatch match = new ShapePropMatch(previousMatchResult, null, uppercase);
			return match;
			
		}
		if (Debug) System.out.println("not find function!");
		return null;
	}
	
	public String toString(){
		return i.toString()+"("+(ls==null?"":ls.toString())+")";
	}
}
