package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.valueanalysis.value.Value;

public class ICCondition extends ICNode{
	ICRelop ro;
	ICValue val;
	
	public ICCondition(ICRelop ro, ICValue val)
	{
		this.ro = ro;
		this.val = val;
		
	}
	
	public String toString()
	{
		return ro.toString()+val.toString();
	}
	
	public String getOp()
	{
		return ro.toString();
	}
	
	public int getVal()
	{
		return val.toNumber();
	}
	

	@Override
	public isComplexInfoPropMatch match(boolean isPatternSide,
			isComplexInfoPropMatch previousMatchResult,List<? extends Value<?>> argValues) {
		// TODO Auto-generated method stub
		return null;
	}

}
