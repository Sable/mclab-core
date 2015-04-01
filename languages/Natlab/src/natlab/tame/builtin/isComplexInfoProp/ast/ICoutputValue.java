package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.valueanalysis.value.Value;


public class ICoutputValue extends ICAbstractValue{

	ICAbstractValue ov;
	
	ICoutputValue(ICAbstractValue ov)
	{
		this.ov = ov;
	}
	
	public String toString()
	{
		return ov.toString();
	}
	
	public isComplexInfoPropMatch match(boolean isPatternSide, isComplexInfoPropMatch previousMatchResult, List<? extends Value<?>> argValues)
	{
		return ov.match(isPatternSide, previousMatchResult, argValues);
	}
	//TODO - is this correct ?
}