package natlab.tame.builtin.shapeprop.ast;

import java.util.HashMap;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.Value;

public class SPFunCall extends SPAbstractVertcatExprArg{
	static boolean Debug = false;
	String i;
	SPAbstractVertcatExprArg ls;
	public SPFunCall(String i, SPAbstractVertcatExprArg ls){
		this.i = i;
		this.ls = ls;
		//System.out.println("functionCall:"+i);
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues, int num){
		//System.out.println("function: "+i);
		if((i.indexOf("previousScalar")==0)&(ls==null)){
			if (Debug) System.out.println("inside previousScalar");
			previousMatchResult.getLatestMatchedLowercase();
			if (Debug) System.out.println(previousMatchResult.getLatestMatchedLowercase());
			if(previousMatchResult.hasValue(previousMatchResult.getLatestMatchedUppercase())){
				HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
				lowercase.put(previousMatchResult.getLatestMatchedLowercase(), previousMatchResult.getValueOfVariable(
						previousMatchResult.getLatestMatchedUppercase()));
				ShapePropMatch matchResult = new ShapePropMatch(previousMatchResult, lowercase, null);
				//System.out.println(matchResult.getAllLowercase());
	            return matchResult;	
			}
			else{
				HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
				lowercase.put(previousMatchResult.getLatestMatchedLowercase(), null);
				ShapePropMatch matchResult = new ShapePropMatch(previousMatchResult, lowercase, null);
				//System.out.println(matchResult.getAllLowercase());
	            return matchResult;	
			}	
		}
		else if(i.indexOf("add")==0){
			if (Debug) System.out.println("inside add");
			if(previousMatchResult.hasValue(previousMatchResult.getLatestMatchedLowercase())){
				previousMatchResult.addToVertcatExpr(previousMatchResult.getValueOfVariable(
						(previousMatchResult.getLatestMatchedLowercase())));
				if (Debug) System.out.println(previousMatchResult.getOutputVertcatExpr());
				return previousMatchResult;
			}
			else{
				previousMatchResult.addToVertcatExpr(null);
				if (Debug) System.out.println(previousMatchResult.getOutputVertcatExpr());
				return previousMatchResult;
			}
		}
		else if(i.indexOf("minus")==0){
			if (Debug) System.out.println("inside minus("+ls.toString()+")");
			String[] arg = ls.toString().split(",");
			if(arg.length==2){
				if(previousMatchResult.hasValue(arg[0])&&previousMatchResult.hasValue(arg[1])){
					HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
					int minus = previousMatchResult.getValueOfVariable(arg[0])-previousMatchResult.getValueOfVariable(arg[1])+1;
					lowercase.put(previousMatchResult.getLatestMatchedLowercase(), minus);
					ShapePropMatch matchResult = new ShapePropMatch(previousMatchResult, lowercase, null);
					if (Debug) System.out.println(matchResult.getAllLowercase());
		            return matchResult;
				}
				else{
					HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
					if (Debug) System.out.println("one of the arguments is null!");
					lowercase.put(previousMatchResult.getLatestMatchedLowercase(), null);
					ShapePropMatch matchResult = new ShapePropMatch(previousMatchResult, lowercase, null);
					if (Debug) System.out.println(matchResult.getAllLowercase());
		            return matchResult;
				}
			}
			return previousMatchResult;
		}
		else if(i.indexOf("previousShapeDim")==0){
			if(ls==null){
				if (Debug) System.out.println("inside previousShapeDim()");
				Shape<?> previousMatched = previousMatchResult.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase());
				List<Integer> dimensions = previousMatched.getDimensions();
				int numberOfDimensions = 0;
				for(Integer i: dimensions){
					numberOfDimensions = numberOfDimensions+1;
				}
				if (Debug) System.out.println("this matched shape has "+numberOfDimensions+" dimensions");
				HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
				lowercase.put(previousMatchResult.getLatestMatchedLowercase(), numberOfDimensions);
				ShapePropMatch matchResult = new ShapePropMatch(previousMatchResult, lowercase, null);
	            return matchResult;
			}
			else{
				if (Debug) System.out.println("inside previousShapeDim("+ls.toString()+")");
				previousMatchResult = ls.match(isPatternSide, previousMatchResult, argValues, num);//try to get argument information
				if(previousMatchResult.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase()).getDimensions().get(previousMatchResult.getLatestMatchedNumber()-1)==null){
					HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
					lowercase.put(previousMatchResult.getLatestMatchedLowercase(), null);
					ShapePropMatch matchResult = new ShapePropMatch(previousMatchResult, lowercase, null);
					if (Debug) System.out.println(matchResult.getAllLowercase());
		            return matchResult;
				}
				else{
					int dimNum = previousMatchResult.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase()).getDimensions().get(previousMatchResult.getLatestMatchedNumber()-1);
					HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
					lowercase.put(previousMatchResult.getLatestMatchedLowercase(), dimNum);
					ShapePropMatch matchResult = new ShapePropMatch(previousMatchResult, lowercase, null);
					if (Debug) System.out.println(matchResult.getAllLowercase());
		            return matchResult;
				}
			}
		}
		else if(i.indexOf("isequal")==0){
			if (Debug) System.out.println("inside isequal("+ls.toString()+")");
			String[] arg = ls.toString().split(",");
			if(arg.length==2){
				if (Debug) System.out.println(previousMatchResult.getShapeOfVariable(arg[0])+" compare with "+previousMatchResult.getShapeOfVariable(arg[1]));
				Shape<?> first = (Shape<?>)previousMatchResult.getShapeOfVariable(arg[0]);
				Shape<?> second = (Shape<?>)previousMatchResult.getShapeOfVariable(arg[1]);
				//actually, I don't know what happened here, need more consideration later.
				if(first.getSize()==second.getSize()){
		    		int j=0;
		    		for(Integer i : first.getDimensions()){
		    			if (Debug) System.out.println("testing weather or not shape equals!");
		    			//System.out.println("i is "+i+", j is "+o.getCertainDimensionSize(j));
		    			if(i==second.getCertainDimensionSize(j)){
		    				j=j+1;
		    			}
		    			else{
		    				if (Debug) System.out.println("inside shape equals false!");
		    				return null;//FIXME
		    			}
		    		}
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
			if(previousMatchResult.hasValue(previousMatchResult.getLatestMatchedLowercase())&&previousMatchResult.hasValue(ls.toString())){
				HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
				int sum = previousMatchResult.getValueOfVariable(previousMatchResult.getLatestMatchedLowercase())+previousMatchResult.getValueOfVariable(ls.toString());
				if (Debug) System.out.println(sum);
				lowercase.put(previousMatchResult.getLatestMatchedLowercase(), sum);
				previousMatchResult = new ShapePropMatch(previousMatchResult, lowercase, null);
				if (Debug) System.out.println(previousMatchResult.getAllLowercase());
				return previousMatchResult;
			}
			else{
				HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
				lowercase.put(previousMatchResult.getLatestMatchedLowercase(), null);
				previousMatchResult = new ShapePropMatch(previousMatchResult, lowercase, null);
				if (Debug) System.out.println(previousMatchResult.getAllLowercase());
				return previousMatchResult;
			}
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
				if(previousMatchResult.hasValue(arg[0])&&previousMatchResult.hasValue(arg[1])){
					HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
					int f = previousMatchResult.getValueOfVariable(arg[0]);
					int s = previousMatchResult.getValueOfVariable(arg[1]);
					String result = (f<s)?arg[0]:arg[1];
					if (Debug) System.out.println("the minimum one is "+result);
					lowercase.put(previousMatchResult.getLatestMatchedLowercase(), previousMatchResult.getValueOfVariable(result));
					ShapePropMatch match = new ShapePropMatch(previousMatchResult, lowercase, null);
					return match;
				}
				else{
					HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
					if (Debug) System.out.println("one of the arguments of minimum is null!");
					lowercase.put(previousMatchResult.getLatestMatchedLowercase(), null);
					ShapePropMatch match = new ShapePropMatch(previousMatchResult, lowercase, null);
					return match;
				}
			}
			//return error shape, FIXME
		}
		else if(i.indexOf("anyDimensionBigger")==0){
			if (Debug) System.out.println("inside anyDimensionBigger than "+ls.toString());
			previousMatchResult = ls.match(isPatternSide, previousMatchResult, argValues, num);
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
		else if(i.indexOf("numOutput")==0){
			if (Debug) System.out.println("inside numOutput("+ls.toString()+")");
			if (Debug) System.out.println("currentlly, the number of output variables is "+num);
			previousMatchResult = ls.match(isPatternSide, previousMatchResult, argValues, num);
			int latestMatchedNum = previousMatchResult.getLatestMatchedNumber();
			if(latestMatchedNum==num){
				return previousMatchResult;
			}
			else{
				previousMatchResult.setIsError();
				return previousMatchResult;
			}
		}
		if (Debug) System.out.println("not find function, return null!");
		return null;
	}
	
	public String toString(){
		return i.toString()+"("+(ls==null?"":ls.toString())+")";
	}
}
