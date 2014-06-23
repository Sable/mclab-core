package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.valueanalysis.value.Value;

public class ICoutputValueList extends ICNode{

	 ICoutputValueList ovl;
	 ICAbstractValue ov;
	 
	 public ICoutputValueList(ICoutputValueList ovl, ICAbstractValue ov)
	 {
		 this.ovl = ovl;
		 this.ov = ov;
	 }
	 
	 public String toString()
	 {
		 return (ovl==null?"":ovl.toString()+",")+ov.toString();
	 }

	@Override
	public isComplexInfoPropMatch match(boolean isPatternSide,
			isComplexInfoPropMatch previousMatchResult, List<? extends Value<?>> argValues) {
		
		if (null != ovl)
		{
		isComplexInfoPropMatch listOfArgsMatch = ovl.match(isPatternSide, previousMatchResult, argValues);
		isComplexInfoPropMatch match = ov.match(isPatternSide, listOfArgsMatch, argValues);
		return match;
		}
		else
		{
			isComplexInfoPropMatch match = ov.match(isPatternSide, previousMatchResult, argValues);
			return match;
		}
	}
}
