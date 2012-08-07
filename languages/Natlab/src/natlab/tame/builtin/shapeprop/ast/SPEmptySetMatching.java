package natlab.tame.builtin.shapeprop.ast;

import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Value;

public class SPEmptySetMatching extends SPAbstractVectorExpr{
	static boolean Debug = false;
	
	public SPEmptySetMatching(){
		//System.out.println("an empty vector matching");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues, int num){
		ShapePropMatch match = new ShapePropMatch(previousMatchResult);
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
