package natlab.tame.builtin.shapeprop.ast;

public class SPRParen extends SPAbstractMatchExpr
{
	SPAbstractPattern p;
	public SPRParen (SPAbstractPattern p)
	{
		this.p = p;
		//System.out.println("()");
	}
	
	public String toString()
	{
		return "("+p.toString()+")";
	}
}
