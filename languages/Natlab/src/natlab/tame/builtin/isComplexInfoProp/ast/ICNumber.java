package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.valueanalysis.value.Value;

public class ICNumber extends ICValue{

	Number n;
	public ICNumber(Number n)
	{
		this.n = n;
	}
	
	public String toString()
	{
		return n.toString();
	}
	
	public int toNumber() {
		// TODO Auto-generated method stub
		return (Integer) n;
	}

	@Override
	public isComplexInfoPropMatch match(boolean isPatternSide,
			isComplexInfoPropMatch previousMatchResult, List<? extends Value<?>> argValues) {
		// TODO Auto-generated method stub
		return null;
	}
}
