package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.components.constant.*;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.*;

public class SPStringLiteral<V extends Value<V>> extends SPAbstractMatchElement<V>{
	static boolean Debug = false;
	String id;
	public SPStringLiteral(String id){
		this.id = id;
		//System.out.println(id);
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int num){
		//a string literal used to match a constant string argument
		if(argValues.get(previousMatchResult.getNumMatched())!=null){
			if (Debug) System.out.println(argValues.size());
			//get indexing current Matrix Value from args
			Value<V> argument = argValues.get(previousMatchResult.getNumMatched());
			//get shape info from current Matrix Value
			Shape<V> argumentShape = ((HasShape<V>)argument).getShape();
			Constant argumentConstant =((HasConstant)argument).getConstant();
			if (Debug) System.out.println("current pointed string argument is "+argumentConstant+", and its shape is "+argumentShape);
			if(argumentConstant.toString().equals(id)){
				if (Debug) System.out.println("matching a string argument '"+argumentConstant+"' !");
				previousMatchResult.comsumeArg();
				return previousMatchResult;
			}
			previousMatchResult.setIsError();
			return previousMatchResult;
		}
		//FIXME if index pointing empty, means not match, do something
		return previousMatchResult;
	}
	
	public String toString(){
		return "'"+id.toString()+"'";
	}
}
