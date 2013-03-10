package natlab.tame.builtin.shapeprop.ast;

import java.util.HashMap;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.*;

public class SPFunCall<V extends Value<V>> extends SPAbstractMatchElement<V> {
	
	static boolean Debug = false;
	String funName;
	SPArglist<V> arglist;
	
	public SPFunCall(String funName, SPArglist<V> arglist) {
		this.funName = funName;
		this.arglist = arglist;
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		if (funName.equals("previousScalar") && arglist==null) {
			if (Debug) System.out.println("try to get previous matched scalar's value.");
			String latestMatchedUppercase = previousMatchResult.getLatestMatchedUppercase();
			String latestMatchedLowercase = previousMatchResult.getLatestMatchedLowercase();
			if (latestMatchedUppercase.equals("$") && previousMatchResult.hasValue("$")) {
				HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
				lowercase.put(latestMatchedLowercase, previousMatchResult.getValueOfVariable("$"));
				ShapePropMatch<V> matchResult = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
	            return matchResult;	
			}
			else{
				HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
				lowercase.put(latestMatchedLowercase, new DimValue());
				ShapePropMatch<V> matchResult = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
	            return matchResult;	
			}	
		}
		else if (funName.equals("add")) {
			if (Debug) System.out.println("add latest matched lowercase to vertcat.");
			String latestMatchedLowercase = previousMatchResult.getLatestMatchedLowercase();
			if (previousMatchResult.hasValue(latestMatchedLowercase)) {
				previousMatchResult.addToVertcatExpr(previousMatchResult.getValueOfVariable(latestMatchedLowercase));
				return previousMatchResult;
			}
			else {
				previousMatchResult.addToVertcatExpr(new DimValue());
				return previousMatchResult;
			}
		}
		else if (funName.equals("minus")) {
			String[] arg = arglist.toString().split(",");
			if(arg.length==2){
				if (Debug) System.out.println("compute " + arg[0] + " - " + arg[1]);
				if (previousMatchResult.getValueOfVariable(arg[0]).hasIntValue() 
						&& previousMatchResult.getValueOfVariable(arg[1]).hasIntValue()) {
					HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
					int minus = previousMatchResult.getValueOfVariable(arg[0]).getIntValue() 
							- previousMatchResult.getValueOfVariable(arg[1]).getIntValue() + 1;
					lowercase.put(previousMatchResult.getLatestMatchedLowercase(), new DimValue(minus, null));
					ShapePropMatch<V> matchResult = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
		            return matchResult;
				}
			}
			if (Debug) System.out.println("cannot compute minus function in shape propagation!");
			return previousMatchResult;
		}
		else if (funName.equals("div")) {
			String[] arg = arglist.toString().split(",");
			if(arg.length==2){
				if (Debug) System.out.println("compute " + arg[0] + "/" + arg[1]);
				if (previousMatchResult.getValueOfVariable(arg[0]).hasIntValue()
						&& previousMatchResult.getValueOfVariable(arg[1]).hasIntValue()) {
					HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
					int div = previousMatchResult.getValueOfVariable(arg[0]).getIntValue() 
							/ previousMatchResult.getValueOfVariable(arg[1]).getIntValue();
					lowercase.put(previousMatchResult.getLatestMatchedLowercase(), new DimValue(div, null));
					ShapePropMatch<V> matchResult = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
		            return matchResult;
				}
			}
			if (Debug) System.out.println("cannot compute div function in shape propagation!");
			return previousMatchResult;
		}
		else if(funName.equals("previousShapeDim")) {
			// if arglist is empty, this function is trying to get how many dimensions of previous matched shape. i.e. for size.
			if (arglist==null) {
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
			/*
			 *  if arglist is not empty, in this language, which means there is a number in the arglist, 
			 *  this function is trying to get the size of that specific dimension of previous matched shape. i.e. for vertcat and horzcat.
			 */
			else {
				if (Debug) System.out.println("inside previousShapeDim("+arglist.toString()+")");
				ShapePropMatch<V> arglistMatch = arglist.match(isPatternSide, previousMatchResult, argValues, Nargout);
				if (arglistMatch.getShapeOfVariable(arglistMatch.getLatestMatchedUppercase())
						.getDimensions().get(arglistMatch.getLatestMatchedNumber()-1)==null) {
		            return arglistMatch;
				}
				else {
					DimValue dimNum = arglistMatch.getShapeOfVariable(arglistMatch.getLatestMatchedUppercase())
							.getDimensions().get(arglistMatch.getLatestMatchedNumber()-1);
					HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
					lowercase.put(arglistMatch.getLatestMatchedLowercase(), new DimValue(dimNum.getIntValue(), dimNum.getSymbolic()));
					ShapePropMatch<V> matchResult = new ShapePropMatch<V>(arglistMatch, lowercase, null);
		            return matchResult;
				}
			}
		}
		/*
		 *  this is an assert statement, may set matching process has error. TODO divided functions into two categories: 
		 *  1. functions in assignment statements;
		 *  2. functions as an assert statement itself.
		 *  P.S. assert statement functions have the ability to set matching process has error.
		 */
		else if (funName.equals("isequal")) {
			if (Debug) System.out.println("inside isequal("+arglist.toString()+")");
			String[] arg = arglist.toString().split(",");
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
			}
			else {
				previousMatchResult.setIsError(true);
				return previousMatchResult;				
			}
		}
		else if(funName.equals("increment")){
			if (Debug) System.out.println("inside increment("+arglist.toString()+")");
			if (Debug) System.out.println(previousMatchResult.getAllLowercase());
			if(previousMatchResult.hasValue(previousMatchResult.getLatestMatchedLowercase())&&previousMatchResult.hasValue(arglist.toString())){
				HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
				int sum = previousMatchResult.getValueOfVariable(previousMatchResult.getLatestMatchedLowercase()).getIntValue()+previousMatchResult
						.getValueOfVariable(arglist.toString()).getIntValue();
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
		else if(funName.equals("copy")){
			if (Debug) System.out.println("inside copy("+arglist.toString()+")");
			if (Debug) System.out.println(previousMatchResult.getLatestMatchedUppercase());
			HashMap<String, Shape<V>> uppercase = new HashMap<String, Shape<V>>();
			Shape<V> newShape = new ShapeFactory<V>().newShapeFromDimValues(previousMatchResult.getShapeOfVariable(arglist.toString()).getDimensions());
			uppercase.put(previousMatchResult.getLatestMatchedUppercase(), newShape);
			ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, null, uppercase);
			if (Debug) System.out.println(match.getAllUppercase());
			return match;
		}
		else if(funName.equals("minimum")){
			if (Debug) System.out.println("inside minimum("+arglist.toString()+")");
			String[] arg = arglist.toString().split(",");
			if(arg.length==2){
				if(previousMatchResult.hasValue(arg[0])&&previousMatchResult.hasValue(arg[1])){
					HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
					int f = previousMatchResult.getValueOfVariable(arg[0]).getIntValue();
					int s = previousMatchResult.getValueOfVariable(arg[1]).getIntValue();
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
		else if(funName.equals("anyDimensionBigger")){
			if (Debug) System.out.println("inside anyDimensionBigger than "+arglist.toString());
			previousMatchResult = arglist.match(isPatternSide, previousMatchResult, argValues, Nargout);
			int latestMatchedNum = previousMatchResult.getLatestMatchedNumber();
			List<DimValue> dimensions = previousMatchResult.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase()).getDimensions();
			for(DimValue d : dimensions){
				if(d.getIntValue()>=latestMatchedNum){
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
		else if(funName.equals("numOutput")){
			if (Debug) System.out.println("inside numOutput("+arglist.toString()+")");
			if (Debug) System.out.println("currentlly, the number of output variables is "+Nargout);
			previousMatchResult = arglist.match(isPatternSide, previousMatchResult, argValues, Nargout);
			int latestMatchedNum = previousMatchResult.getLatestMatchedNumber();
			if(latestMatchedNum==Nargout){
				return previousMatchResult;
			}
			else{
				previousMatchResult.setIsError(true);
				return previousMatchResult;
			}
		}
		if (Debug) System.out.println("not find function, return null!");
		return null;
	}
	
	public String toString(){
		return funName.toString()+"("+(arglist==null?"":arglist.toString())+")";
	}
}
