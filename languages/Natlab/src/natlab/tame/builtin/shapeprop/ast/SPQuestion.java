package natlab.tame.builtin.shapeprop.ast;

public class SPQuestion extends SPAbstractMatchExpr
{
	SPAbstractMatchExpr spm;
	public SPQuestion (SPAbstractMatchExpr spm)
	{
		this.spm = spm;
		//System.out.println("?");
	}
	
	public String toString()
	{
		return spm.toString()+"?";
	}
}
