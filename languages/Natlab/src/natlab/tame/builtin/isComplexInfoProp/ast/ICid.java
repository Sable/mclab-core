package natlab.tame.builtin.isComplexInfoProp.ast;


import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.valueanalysis.value.Value;

public class ICid extends ICinputAtt{

	String i;
	
	public ICid(String i)
	{
		this.i = i;
	}
	
	public String toString()
	{
		return i.toString();
	}

	@Override
	public isComplexInfoPropMatch match(boolean isPatternSide,
			isComplexInfoPropMatch previousMatchResult, List<? extends Value<?>> argValues) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
