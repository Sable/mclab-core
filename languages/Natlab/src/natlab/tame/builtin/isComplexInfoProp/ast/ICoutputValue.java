package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
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
	
	public isComplexInfoPropMatch match(boolean isPatternSide, isComplexInfoPropMatch previousMatchResult, List<Integer> argValues)
	{
		return ov.match(isPatternSide, previousMatchResult, argValues);
	}
	//TODO - is this correct ?
}