package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.valueanalysis.value.Value;

public class ICinputValueList extends ICNode{

	 ICinputValueList ivl;
	 ICAbstractValue iv;
	 
	 public ICinputValueList(ICinputValueList ivl, ICAbstractValue iv)
	 {
		 this.ivl = ivl;
		 this.iv = iv;
	 }
	 
	 public String toString()
	 {
		 return (ivl==null?"":ivl.toString()+",")+iv.toString();
	 }

	@Override
	public isComplexInfoPropMatch match(boolean isPatternSide,
			isComplexInfoPropMatch previousMatchResult, List<? extends Value<?>> argValues) {
		
		if (null != ivl)
		{
		isComplexInfoPropMatch listOfArgsMatch = ivl.match(isPatternSide, previousMatchResult, argValues);
		isComplexInfoPropMatch match = iv.match(isPatternSide, listOfArgsMatch, argValues);
		return match;
		}
		else
		{
			isComplexInfoPropMatch match = iv.match(isPatternSide, previousMatchResult, argValues);
			return match;
		}
		
		
	}
}
