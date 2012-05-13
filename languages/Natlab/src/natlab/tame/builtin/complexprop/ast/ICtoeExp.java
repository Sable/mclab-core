package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;


public class ICtoeExp extends ICternaryOpExp{
	
	ICinputAtt ia;
	ICCondition co;
	ICAbstractValue xv1;
	ICAbstractValue xv2;
	ICQOp qop1;
	ICQOp qop2;
	
	public ICtoeExp (ICinputAtt ia, ICCondition co, ICAbstractValue xv1, ICQOp qop1, ICAbstractValue xv2, ICQOp qop2)
	{
		this.ia = ia;
		this.co =co;
		this.xv1 = xv1;
		this.qop1 = qop1;
		this.xv2 = xv2;
		this.qop2 = qop2;
	}
	
	public String toString ()
	{
		String qopVal1, qopVal2;
		if (qop1 == null)
			qopVal1 = "";
		else
			qopVal1 = qop1.toString();
		
		if (qop2 == null)
			qopVal2 = "";
		else
			qopVal2 = qop2.toString();
	
		return ia.toString()+co.toString()+"?"+xv1.toString()+qopVal1+":"+xv2.toString()+qopVal2;
	}

	@Override
	public isComplexInfoPropMatch match(boolean isPatternSide,
			isComplexInfoPropMatch previousMatchResult, List<Integer> argValues) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
