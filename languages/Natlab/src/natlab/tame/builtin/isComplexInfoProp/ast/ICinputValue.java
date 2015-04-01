package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.valueanalysis.value.Value;


public class ICinputValue extends ICAbstractValue{

	ICAbstractValue iv;
	
	ICinputValue(ICAbstractValue iv)
	{
		this.iv = iv;
	}
	
	public String toString()
	{
		return iv.toString();
	}

	@Override
	public isComplexInfoPropMatch match(boolean isPatternSide,
			isComplexInfoPropMatch previousMatchResult, List<? extends Value<?>> argValues) {
		return iv.match(isPatternSide, previousMatchResult, argValues);
	}
	
}
