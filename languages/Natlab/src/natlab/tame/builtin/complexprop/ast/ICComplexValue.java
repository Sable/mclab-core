package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class ICComplexValue extends ICAbstractValue{
	
	ICAbstractValue xv;
	
	public ICComplexValue(ICAbstractValue xv)
	{
		this.xv = xv;
	}

	
	public String toString()
	{
		return xv.toString();
	}


	@Override
	public isComplexInfoPropMatch match(boolean isPatternSide,
			isComplexInfoPropMatch previousMatchResult, List<Integer> argValues) {
		
		isComplexInfoPropMatch match = xv.match(isPatternSide, previousMatchResult, argValues);
		return match;
		//this will propagate to any, real or compex - ICType
	}
}
