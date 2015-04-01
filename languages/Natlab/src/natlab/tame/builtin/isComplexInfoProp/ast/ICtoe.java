package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.valueanalysis.value.Value;

public class ICtoe extends ICternaryOpExp{
	
	ICternaryOpExp toe;
	
	public ICtoe(ICternaryOpExp toe)
	{
		this.toe = toe;
	}
	
	public String toString()
	{
		return toe.toString();
	}

	@Override
	public isComplexInfoPropMatch match(boolean isPatternSide,
			isComplexInfoPropMatch previousMatchResult, List<? extends Value<?>> argValues) {
		// TODO Auto-generated method stub
		return toe.match(isPatternSide, previousMatchResult, argValues);
	}

}
