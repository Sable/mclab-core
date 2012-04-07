package natlab.tame.builtin.shapeprop.ast;

public class SPMult extends SPAbstractMatchExpr
{
	SPAbstractMatchExpr sp;
	public SPMult (SPAbstractMatchExpr sp)
	{
		this.sp = sp;
		//System.out.println("*");
	}
	
	public String toString()
	{
		return sp.toString()+"*";
	}
}
