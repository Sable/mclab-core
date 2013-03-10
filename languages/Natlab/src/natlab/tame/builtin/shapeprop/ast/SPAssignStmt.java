package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public class SPAssignStmt<V extends Value<V>> extends SPAbstractPattern<V>{
	SPAbstractMatchElement<V> lhs;
	SPAbstractMatchElement<V> rhs;
	public SPAssignStmt(SPAbstractMatchElement<V> lhs, SPAbstractMatchElement<V> rhs){
		this.lhs = lhs;
		this.rhs = rhs;
		//System.out.println("=");
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int num){
		previousMatchResult.setIsInsideAssign(true);
		ShapePropMatch<V> match = lhs.match(isPatternSide, previousMatchResult, argValues, num);
		ShapePropMatch<V> rhsMatch = rhs.match(isPatternSide, match, argValues, num);
		rhsMatch.setIsInsideAssign(false);
		return rhsMatch;
	}
	
	public String toString(){
		return lhs.toString()+"="+rhs.toString();
	}
}
