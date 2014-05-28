package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
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
			isComplexInfoPropMatch previousMatchResult, List<? extends Value<?>> argValues) {
		
		isComplexInfoPropMatch match = xv.match(isPatternSide, previousMatchResult, argValues);
		if (false == match.getLastMatchSucceed())
		{
			match.setError(true);
		}
		return match;
		//this will propagate to any, real or compex - ICType
	}
}
