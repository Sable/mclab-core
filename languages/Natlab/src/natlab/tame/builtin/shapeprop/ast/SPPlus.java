package natlab.tame.builtin.shapeprop.ast;

public class SPPlus extends SPAbstractMatchExpr
{
	SPAbstractMatchExpr sp;
	public SPPlus (SPAbstractMatchExpr sp)
	{
		this.sp = sp;
		//System.out.println("+");
	}
	
	public String toString()
	{
		return sp.toString()+"+";
	}
}
