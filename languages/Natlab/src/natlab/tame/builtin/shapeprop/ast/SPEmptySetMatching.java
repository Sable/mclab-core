package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public class SPEmptySetMatching<V extends Value<V>> extends SPAbstractVectorExpr<V>{
	static boolean Debug = false;
	
	public SPEmptySetMatching(){
		//System.out.println("an empty vector matching");
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int num){
		ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult);
		if(isPatternSide==true){
			if (Debug) System.out.println("inside SPEmptySetMatching with arguments "+argValues);
			if(argValues.isEmpty()){
				match.comsumeArg();
				if (Debug) System.out.println("matching an empty arg!");
				return match;
				}
			else{
				if (Debug) System.out.println("inside not empty");
				previousMatchResult.setIsError();
				return previousMatchResult;
				}
			}
		else{
			if (Debug) System.out.println("the output shape is []");
			return previousMatchResult;
		}
	}
	
	public String toString(){
		return "[]";
	}
}
