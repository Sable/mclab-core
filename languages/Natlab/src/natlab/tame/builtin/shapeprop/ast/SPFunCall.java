package natlab.tame.builtin.shapeprop.ast;

import java.util.HashMap;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.*;

public class SPFunCall<V extends Value<V>> extends SPAbstractVertcatExprArg<V>{
	static boolean Debug = false;
	String i;
	SPAbstractVertcatExprArg<V> ls;
	public SPFunCall(String i, SPAbstractVertcatExprArg<V> ls){
		this.i = i;
		this.ls = ls;
		//System.out.println("functionCall:"+i);
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int num){
		//System.out.println("function: "+i);
		if((i.indexOf("previousScalar")==0)&(ls==null)){
			if (Debug) System.out.println("inside previousScalar");
			previousMatchResult.getLatestMatchedLowercase();
			if (Debug) System.out.println(previousMatchResult.getLatestMatchedLowercase());
			if(previousMatchResult.hasValue(previousMatchResult.getLatestMatchedUppercase())){
				HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
				lowercase.put(previousMatchResult.getLatestMatchedLowercase(), previousMatchResult.getValueOfVariable(
						previousMatchResult.getLatestMatchedUppercase()));
				ShapePropMatch<V> matchResult = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
				//System.out.println(matchResult.getAllLowercase());
	            return matchResult;	
			}
			else{
				HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
				lowercase.put(previousMatchResult.getLatestMatchedLowercase(), new DimValue());
				ShapePropMatch<V> matchResult = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
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
				previousMatchResult.addToVertcatExpr(new DimValue());
				if (Debug) System.out.println(previousMatchResult.getOutputVertcatExpr());
				return previousMatchResult;
			}
		}
		else if(i.indexOf("minus")==0){
			if (Debug) System.out.println("inside minus("+ls.toString()+")");
			String[] arg = ls.toString().split(",");
			if(arg.length==2){
				if(previousMatchResult.hasValue(arg[0])&&previousMatchResult.hasValue(arg[1])){
					HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
					int minus = previousMatchResult.getValueOfVariable(arg[0]).getValue()-previousMatchResult.getValueOfVariable(arg[1]).getValue()+1;
					lowercase.put(previousMatchResult.getLatestMatchedLowercase(), new DimValue(minus, null));
					ShapePropMatch<V> matchResult = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
					if (Debug) System.out.println(matchResult.getAllLowercase());
		            return matchResult;
				}
				else{
					HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
					if (Debug) System.out.println("one of the arguments is null!");
					lowercase.put(previousMatchResult.getLatestMatchedLowercase(), new DimValue());
					ShapePropMatch<V> matchResult = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
					if (Debug) System.out.println(matchResult.getAllLowercase());
		            return matchResult;
				}
			}
			return previousMatchResult;
		}
		else if(i.indexOf("div")==0){
			if (Debug) System.out.println("inside div("+ls.toString()+")");
			String[] arg = ls.toString().split(",");
			if(arg.length==2){
				if(previousMatchResult.hasValue(arg[0])&&previousMatchResult.hasValue(arg[1])){
					HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
					int div = previousMatchResult.getValueOfVariable(arg[0]).getValue()/previousMatchResult.getValueOfVariable(arg[1]).getValue();
					lowercase.put(previousMatchResult.getLatestMatchedLowercase(), new DimValue(div, null));
					ShapePropMatch<V> matchResult = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
					if (Debug) System.out.println(matchResult.getAllLowercase());
		            return matchResult;
				}
				else{
					HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
					if (Debug) System.out.println("one of the arguments is null!");
					lowercase.put(previousMatchResult.getLatestMatchedLowercase(), new DimValue());
					ShapePropMatch<V> matchResult = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
					if (Debug) System.out.println(matchResult.getAllLowercase());
		            return matchResult;
				}
			}
			return previousMatchResult;
		}
		else if(i.indexOf("previousShapeDim")==0){
			if(ls==null){
				if (Debug) System.out.println("inside previousShapeDim()");
				Shape<V> previousMatched = previousMatchResult.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase());
				List<DimValue> dimensions = previousMatched.getDimensions();
				int numberOfDimensions = dimensions.size();
				if (Debug) System.out.println("this matched shape has "+numberOfDimensions+" dimensions");
				HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
				lowercase.put(previousMatchResult.getLatestMatchedLowercase(), new DimValue(numberOfDimensions, null));
				ShapePropMatch<V> matchResult = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
	            return matchResult;
			}
			else{
				if (Debug) System.out.println("inside previousShapeDim("+ls.toString()+")");
				previousMatchResult = ls.match(isPatternSide, previousMatchResult, argValues, num);//try to get argument information
				if(previousMatchResult.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase()).getDimensions()
						.get(previousMatchResult.getLatestMatchedNumber()-1)==null){
					HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
					lowercase.put(previousMatchResult.getLatestMatchedLowercase(), new DimValue());
					ShapePropMatch<V> matchResult = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
					if (Debug) System.out.println(matchResult.getAllLowercase());
		            return matchResult;
				}
				else{
					DimValue dimNum = previousMatchResult.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase())
							.getDimensions().get(previousMatchResult.getLatestMatchedNumber()-1);
					HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
					lowercase.put(previousMatchResult.getLatestMatchedLowercase(), new DimValue(dimNum.getValue(), null));
					ShapePropMatch<V> matchResult = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
					if (Debug) System.out.println(matchResult.getAllLowercase());
		            return matchResult;
				}
			}
		}
		else if(i.indexOf("isequal")==0){
			if (Debug) System.out.println("inside isequal("+ls.toString()+")");
			String[] arg = ls.toString().split(",");
			if(arg.length==2){
				if (Debug) System.out.println(previousMatchResult.getShapeOfVariable(arg[0])+" compare with "+previousMatchResult
						.getShapeOfVariable(arg[1]));
				Shape<V> first = (Shape<V>)previousMatchResult.getShapeOfVariable(arg[0]);
				Shape<V> second = (Shape<V>)previousMatchResult.getShapeOfVariable(arg[1]);
				//actually, I don't know what happened here, need more consideration later.
				if(first.getDimensions().size()==second.getDimensions().size()){
		    		int j=0;
		    		for(DimValue i : first.getDimensions()){
		    			if (Debug) System.out.println("testing weather or not shape equals!");
		    			//System.out.println("i is "+i+", j is "+o.getCertainDimensionSize(j));
		    			if(i==second.getDimensions().get(j)){
		    				j=j+1;
		    			}
		    			else{
		    				if (Debug) System.out.println("inside shape equals false!");
		    				return null;//FIXME
		    			}
		    		}
		    		if (Debug) System.out.println(previousMatchResult.getShapeOfVariable(arg[0])+" is equal to "+previousMatchResult
		    				.getShapeOfVariable(arg[1]));
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
				HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
				int sum = previousMatchResult.getValueOfVariable(previousMatchResult.getLatestMatchedLowercase()).getValue()+previousMatchResult
						.getValueOfVariable(ls.toString()).getValue();
				if (Debug) System.out.println(sum);
				lowercase.put(previousMatchResult.getLatestMatchedLowercase(), new DimValue(sum, null));
				previousMatchResult = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
				if (Debug) System.out.println(previousMatchResult.getAllLowercase());
				return previousMatchResult;
			}
			else{
				HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
				lowercase.put(previousMatchResult.getLatestMatchedLowercase(), new DimValue());
				previousMatchResult = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
				if (Debug) System.out.println(previousMatchResult.getAllLowercase());
				return previousMatchResult;
			}
		}
		else if(i.indexOf("copy")==0){
			if (Debug) System.out.println("inside copy("+ls.toString()+")");
			if (Debug) System.out.println(previousMatchResult.getLatestMatchedUppercase());
			HashMap<String, Shape<V>> uppercase = new HashMap<String, Shape<V>>();
			Shape<V> newShape = new ShapeFactory<V>().newShapeFromDimValues(previousMatchResult.getShapeOfVariable(ls.toString()).getDimensions());
			uppercase.put(previousMatchResult.getLatestMatchedUppercase(), newShape);
			ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, null, uppercase);
			if (Debug) System.out.println(match.getAllUppercase());
			return match;
		}
		else if(i.indexOf("minimum")==0){
			if (Debug) System.out.println("inside minimum("+ls.toString()+")");
			String[] arg = ls.toString().split(",");
			if(arg.length==2){
				if(previousMatchResult.hasValue(arg[0])&&previousMatchResult.hasValue(arg[1])){
					HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
					int f = previousMatchResult.getValueOfVariable(arg[0]).getValue();
					int s = previousMatchResult.getValueOfVariable(arg[1]).getValue();
					String result = (f<s)?arg[0]:arg[1];
					if (Debug) System.out.println("the minimum one is "+result);
					lowercase.put(previousMatchResult.getLatestMatchedLowercase(), previousMatchResult.getValueOfVariable(result));
					ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
					return match;
				}
				else{
					HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
					if (Debug) System.out.println("one of the arguments of minimum is null!");
					lowercase.put(previousMatchResult.getLatestMatchedLowercase(), new DimValue());
					ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
					return match;
				}
			}
			//return error shape, FIXME
		}
		else if(i.indexOf("anyDimensionBigger")==0){
			if (Debug) System.out.println("inside anyDimensionBigger than "+ls.toString());
			previousMatchResult = ls.match(isPatternSide, previousMatchResult, argValues, num);
			int latestMatchedNum = previousMatchResult.getLatestMatchedNumber();
			List<DimValue> dimensions = previousMatchResult.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase()).getDimensions();
			for(DimValue d : dimensions){
				if(d.getValue()>=latestMatchedNum){
					return previousMatchResult;
				}
			}
			Shape<V> errorShape = (new ShapeFactory<V>()).newShapeFromIntegers(null);
			errorShape.flagHasError();
			HashMap<String, Shape<V>> uppercase = new HashMap<String, Shape<V>>();
			uppercase.put(previousMatchResult.getLatestMatchedUppercase(), errorShape);
			ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, null, uppercase);
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
