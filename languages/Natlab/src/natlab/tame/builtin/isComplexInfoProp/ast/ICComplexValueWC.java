package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.valueanalysis.value.Value;

public class ICComplexValueWC extends ICAbstractValue{
	
	ICAbstractValue xvc;
	
	public ICComplexValueWC(ICAbstractValue xvc)
	{
		this.xvc = xvc;
	}

	
	public String toString()
	{
		return xvc.toString();
	}


	@Override
	public isComplexInfoPropMatch match(boolean isPatternSide,
			isComplexInfoPropMatch previousMatchResult, List<? extends Value<?>> argValues) {
		// TODO Auto-generated method stub
		return null;
	}
}
