package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.valueanalysis.value.Value;

public class ICConditionSet extends ICNode{

	ICConditionSet cos;
	ICCondition co;
	ICLogical lo;
	
	public ICConditionSet(ICConditionSet cos, ICLogical lo, ICCondition co)
	{
		this.cos = cos;
		this.lo = lo;
		this.co =  co;
	}
	
	public String toString()
	{
		return (cos==null?"":cos.toString()+lo.toString())+co.toString();
		//return co.toString();
	}

	@Override
	public isComplexInfoPropMatch match(boolean isPatternSide,
			isComplexInfoPropMatch previousMatchResult, List<? extends Value<?>> argValues) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
