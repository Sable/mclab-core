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
		String qopVal1, qopVal2, xv2Val;
		if (qop1 == null)
			qopVal1 = "";
		else
			qopVal1 = qop1.toString();
		
		if (qop2 == null)
			qopVal2 = "";
		else
			qopVal2 = qop2.toString();
		
		if (null == xv2)
			xv2Val = "";
		else
			xv2Val = xv2.toString();
	
		return ia.toString()+co.toString()+"?"+xv1.toString()+qopVal1+":"+xv2Val+qopVal2;
	}

	@Override
	public isComplexInfoPropMatch match(boolean isPatternSide,
			isComplexInfoPropMatch previousMatchResult, List<? extends Value<?>> argValues) {
		
		// System.out.println("INSIDE teOp\n");
		
		isComplexInfoPropMatch match = new isComplexInfoPropMatch(previousMatchResult);
		if (false == isPatternSide)//has to be on the RHS
		{
			
			// System.out.println("INSIDE teOp\n");
			
			if (match.getNumMatched() == argValues.size())
			{	
				if(ia.toString().equals("NUMXARGS"))
				{
					match = terMatch(previousMatchResult.getNumXargs(), match);
				}
				else if (ia.toString().equals("NUMAARGS"))
				{
					match = terMatch(previousMatchResult.getNumAargs(), match);
				}
				else if (ia.toString().equals("NUMRARGS"))
				{
					match = terMatch(previousMatchResult.getNumRargs(), match);
				}
			}
			else
			{
				match.setError(true);
			}
		}
		return match;
		
	}

	private isComplexInfoPropMatch terMatch(int numTArgs, isComplexInfoPropMatch match) {
		// TODO Auto-generated method stub
		/*
		 * 1. check for the condition
		 * 2. based on the condition call a match on xv1 or xv2
		 *
		 */
		
		//isComplexInfoPropMatch match = new isComplexInfoPropMatch(previousMatchResult);
		if(co.getOp().equals("<"))
		{
			if(numTArgs<co.getVal())
				match.loadOutput(xv1.toString());
			else
				match.loadOutput(xv2.toString());
		}
		else if(co.getOp().equals(">"))
		{
			if(numTArgs>co.getVal())
				match.loadOutput(xv1.toString());
			else
				match.loadOutput(xv2.toString());
		}
		else if(co.getOp().equals("<="))
		{
			if(numTArgs<=co.getVal())
				match.loadOutput(xv1.toString());
			else
				match.loadOutput(xv2.toString());
		}
		else if(co.getOp().equals(">="))
		{
			if(numTArgs>=co.getVal())
				match.loadOutput(xv1.toString());
			else
				match.loadOutput(xv2.toString());
		}
		else if(co.getOp().equals("=="))
		{
			if(numTArgs==co.getVal())
				match.loadOutput(xv1.toString());
			else
				match.loadOutput(xv2.toString());
		}
		else if(co.getOp().equals("~="))
		{
			if(numTArgs!=co.getVal())
				match.loadOutput(xv1.toString());
			else
				match.loadOutput(xv2.toString());
		}
		
		return match;
	}
	

}
