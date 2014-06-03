package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.valueanalysis.value.Value;

public class ICComplexValueWCQ extends ICAbstractValue{
	
	ICAbstractValue xvc;
	ICQOp qop;
	public ICComplexValueWCQ(ICAbstractValue xv, ICQOp qop)
	{
		this.xvc = xv;
		this.qop = qop;
	}

	
	public String toString()
	{
		return xvc.toString()+qop.toString();
	}


	@Override
	public isComplexInfoPropMatch match(boolean isPatternSide,
			isComplexInfoPropMatch previousMatchResult, List<? extends Value<?>> argValues) {
		// TODO Auto-generated method stub
		return null;
	}
}
