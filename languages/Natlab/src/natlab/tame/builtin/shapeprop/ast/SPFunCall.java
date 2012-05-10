package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPFunCall extends SPAbstractVertcatExprArg
{
	String i;
	SPAbstractVertcatExprArg ls;
	public SPFunCall(String i, SPAbstractVertcatExprArg ls)
	{
		this.i = i;
		this.ls = ls;
		//System.out.println("functionCall:"+i);
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues)
	{
		//System.out.println("function: "+i);
		if((i.indexOf("previousScalar")==0)&(ls==null)){
			System.out.println("inside previousScalar");
			HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
			previousMatchResult.getLatestMatchedLowercase();
			System.out.println(previousMatchResult.getLatestMatchedLowercase());
			try{
				System.out.println(previousMatchResult.getValueOfVariable(previousMatchResult.getLatestMatchedUppercase()));
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
			System.out.println("inside add");
			try{
				System.out.println(previousMatchResult.getValueOfVariable((previousMatchResult.getLatestMatchedLowercase())));
			}catch (Exception e){
				previousMatchResult.addToVertcatExpr(null);
				System.out.println(previousMatchResult.getOutputVertcatExpr());
				return previousMatchResult;
			}
			previousMatchResult.addToVertcatExpr(previousMatchResult.getValueOfVariable((previousMatchResult.getLatestMatchedLowercase())));
			System.out.println(previousMatchResult.getOutputVertcatExpr());
			return previousMatchResult;
		}
		else if(i.indexOf("minus")==0){
			System.out.println("inside minus("+ls.toString()+")");
			String[] arg = ls.toString().split(",");
			if(arg.length==2){
				int k = previousMatchResult.getValueOfVariable(arg[0])-previousMatchResult.getValueOfVariable(arg[1])+1;
				HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
				lowercase.put(previousMatchResult.getLatestMatchedLowercase(), k);
				ShapePropMatch matchResult = new ShapePropMatch(previousMatchResult, lowercase, null);
				System.out.println(matchResult.getAllLowercase());
	            return matchResult;
			}
			return previousMatchResult;
		}
		return null;
	}
	
	public String toString()
	{
		return i.toString()+"("+(ls==null?"":ls.toString())+")";
	}
}
